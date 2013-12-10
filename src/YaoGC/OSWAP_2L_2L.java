// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.util.Random;
import java.math.BigInteger;
import Cipher.Cipher;

public class OSWAP_2L_2L extends CompositeCircuit {
    private final int L;
    public static int S;
    private static Random rnd = new Random();

    public OSWAP_2L_2L(int l) {
	super(2*l, 2*l, l, "OSWAP");
	L = l;
    }
    
    public State startExecuting(State s) {
	S = rnd.nextInt(2);
	// S = 0;
	return super.startExecuting(s);
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < L; i++) {
	    subCircuits[i] = new OSWAP_2_2();
	}

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i  ].connectTo(subCircuits[i].inputWires, OSWAP_2_2.X);
	    inputWires[i+L].connectTo(subCircuits[i].inputWires, OSWAP_2_2.Y);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++) {
	    outputWires[i  ] = subCircuits[i].outputWires[0];
	    outputWires[i+L] = subCircuits[i].outputWires[1];
	}
    }
}