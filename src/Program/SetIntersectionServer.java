// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;
import java.util.*;

import YaoGC.*;
import Utils.*;

public class SetIntersectionServer extends ProgServer {
    static int N, L;
    static BigInteger[] X, Y; 

    private BigInteger[][] Xlps, Ylps;

    private State[] outputStates;

    private static final Random rnd = new Random();

    public SetIntersectionServer(int n, int l, BigInteger[] x, BigInteger[] y) {
	N = n; L = l; 
	X = x; Y = y;
    }

    protected void init() throws Exception {
	SetIntersectionCommon.oos.writeInt(N);
	SetIntersectionCommon.oos.writeInt(L);
	SetIntersectionCommon.oos.writeObject(Y);
	SetIntersectionCommon.oos.flush();

	SetIntersectionCommon.N = N;
	SetIntersectionCommon.L = L;

	super.init();
	SetIntersectionCommon.initCircuits();

	generateLabelPairs();
    }

    private void generateLabelPairs() {
	Xlps = new BigInteger[N*L][2];
	Ylps = new BigInteger[N*L][2];

	for (int i = 0; i < N*L; i++) {
	    Xlps[i] = Wire.newLabelPair();
	}

	for (int i = 0; i < N*L; i++) {
	    Ylps[i] = Wire.newLabelPair();
	}
    }

    protected void execTransfer() throws Exception {
    	int bytelength = (Wire.labelBitLength-1)/8 + 1;

    	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < L; j++) {
		int idx = X[i].testBit(j) ? 1 : 0;
		Utils.writeBigInteger(Xlps[i*L+j][idx], bytelength, 
				      SetIntersectionCommon.oos);
	    }
    	}
	SetIntersectionCommon.oos.flush();
    	StopWatch.taskTimeStamp("sending labels for selfs inputs");

    	snder.execProtocol(Ylps);
    	StopWatch.taskTimeStamp("sending labels for peers inputs");
    }

    protected void execCircuit() throws Exception {
    	BigInteger[] Xlbs = new BigInteger[N*L];
    	BigInteger[] Ylbs = new BigInteger[N*L];

    	for (int i = 0; i < Xlbs.length; i++)
    	    Xlbs[i] = Xlps[i][0];

    	for (int i = 0; i < Ylbs.length; i++)
    	    Ylbs[i] = Ylps[i][0];

    	outputStates = SetIntersectionCommon.execCircuit(Xlbs, Ylbs);
    }

    protected void interpretResult() throws Exception {
	ppouts = new ArrayList<BigInteger>();
	for (int k = 0; k < outputStates.length; k++) {
	    BigInteger[] outLabels = (BigInteger[]) SetIntersectionCommon.ois.readObject();

	    BigInteger output = BigInteger.ZERO;
	    for (int i = 0; i < outLabels.length; i++) {
		if (outputStates[k].wires[i].value != Wire.UNKNOWN_SIG) {
		    if (outputStates[k].wires[i].value == 1)
			output = output.setBit(i);
		    continue;
		}
		else if (outLabels[i].equals(outputStates[k].wires[i].invd ? 
					     outputStates[k].wires[i].lbl :
					     outputStates[k].wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)))) {
		    output = output.setBit(i);
	    }
		else if (!outLabels[i].equals(outputStates[k].wires[i].invd ? 
					      outputStates[k].wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) :
					      outputStates[k].wires[i].lbl)) 
		    throw new Exception("Bad label encountered: i = " + i + "\t" +
					Color.red + outLabels[i] + " != (" + 
					outputStates[k].wires[i].lbl + ", " +
					outputStates[k].wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ")" + Color.black);
	    }
	    ppouts.add(output);
	}

	StopWatch.taskTimeStamp("output labels received and interpreted");
    }

    private ArrayList<BigInteger> ppouts;

    protected void verify_result() throws Exception {
	ArrayList<BigInteger> outs = new ArrayList<BigInteger>();

	int k = 0;
	for (int i = 0; i < N; i++)
	    for (int j = 0; j < N; j++) {
		if (X[i].equals(Y[j])) {
		    outs.add(X[i]);
		    k++;
		    break;
		}
	    }

	Collections.sort(outs);
	System.out.println("output (verify): ");

	for (int i = 0; i < outs.size(); i++)
	    System.out.print(outs.get(i) + ", ");
	System.out.println();

	Collections.sort(ppouts);
	System.out.println("output (pp):\t ");
	for (int i = 0; i < ppouts.size(); i++)
	    if (ppouts.get(i).equals(BigInteger.ZERO)) {
		ppouts.remove(i);
		i--;
	    }
	    else 
		System.out.print(ppouts.get(i) + ", ");
	System.out.println();

	boolean b = false;
	if (outs.size() != ppouts.size()) {
	    b = false;
	}
	else {
	    for (int i = 0; i < outs.size(); i++)
		if (!outs.get(i).equals(ppouts.get(i))) {
		    b = false;
		    break;
		}
	    b = true;
	}
	System.out.println("Correctness Verification: " + b);
	System.out.println("\n");
    }
}