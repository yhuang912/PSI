// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class CONDSWAP_2Lplus1_2L extends CompositeCircuit {
    private final int L;
    final int C;

    public CONDSWAP_2Lplus1_2L(int l) {
	super(2*l+1, 2*l, l, "CONDSWAP");
	L = l;
	C = 2*L;
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < L; i++) {
	    subCircuits[i] = new CONDSWAP_3_2();
	}

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i  ].connectTo(subCircuits[i].inputWires, CONDSWAP_3_2.X);
	    inputWires[i+L].connectTo(subCircuits[i].inputWires, CONDSWAP_3_2.Y);
	    inputWires[C   ].connectTo(subCircuits[i].inputWires, CONDSWAP_3_2.C);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++) {
	    outputWires[i  ] = subCircuits[i].outputWires[0];
	    outputWires[i+L] = subCircuits[i].outputWires[1];
	}
    }

    // static int X(int i) { return 2*i+1; }

    // static int Y(int i) { return 2*i; }

    // static int Z1(int i) { return 2*i+1; }

    // static int Z2(int i) { return 2*i; }
}