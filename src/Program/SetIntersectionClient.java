package Program;

import java.math.*;
import java.io.*;
import java.util.*;

import OT.*;
import YaoGC.*;
import Utils.*;

public class SetIntersectionClient extends ProgClient {
    static int N, L;
    static BigInteger[] Y; 

    private BigInteger[] Xlbs, Ylbs;
    private State[] outputStates;

    public SetIntersectionClient() {

    }

    protected void init() throws Exception {
	N = SetIntersectionCommon.ois.readInt();
	L = SetIntersectionCommon.ois.readInt();
	Y = (BigInteger[]) SetIntersectionCommon.ois.readObject();
	
	SetIntersectionCommon.N = N;
	SetIntersectionCommon.L = L;

	otNumOfPairs = N*L;

	super.init();
	SetIntersectionCommon.initCircuits();
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	Xlbs = new BigInteger[N*L];
    	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < L; j++) {
		Xlbs[i*L+j] = Utils.readBigInteger(bytelength, 
						   SetIntersectionCommon.ois);
	    }
    	}
	StopWatch.taskTimeStamp("receiving labels for peer's inputs");

	BigInteger temp = BigInteger.ZERO;
	for (int i = 0; i < N; i++)
	    for (int j = 0; j < L; j++)
		if (Y[i].testBit(j))
		    temp = temp.setBit(i*L+j);

	rcver.execProtocol(temp);
	Ylbs = rcver.getData();
	StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
	outputStates = SetIntersectionCommon.execCircuit(Xlbs, Ylbs);
    }

    protected void interpretResult() throws Exception {
	for (int i = 0; i < outputStates.length; i++)
	    SetIntersectionCommon.oos.writeObject(outputStates[i].toLabels());
	SetIntersectionCommon.oos.flush();
    }

    protected void verify_result() throws Exception { }
}