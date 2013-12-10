// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class SPCMP_2L_L extends CompositeCircuit {
    private final int L;
    static final int XOR = 0;
    static final int  OR = 1;
    static final int MUX = 2;

    public SPCMP_2L_L(int l) {
	super(2*l, l, 3, "SPCMP");

	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR] = new XOR_2L_L(L);
	subCircuits[ OR] = new OR_L_1(L);
	subCircuits[MUX] = new MUX_2Lplus1_L(L);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i].connectTo(subCircuits[XOR].inputWires, i);
	    inputWires[i+L].connectTo(subCircuits[XOR].inputWires, i+L);

	    inputWires[i].connectTo(subCircuits[MUX].inputWires, MUX_2Lplus1_L.X(i));

	    subCircuits[XOR].outputWires[i].connectTo(subCircuits[OR].inputWires, i);
	}

	subCircuits[OR].outputWires[0].connectTo(subCircuits[MUX].inputWires, 2*L);
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++)
	    outputWires[i] = subCircuits[MUX].outputWires[i];
    }

    protected void fixInternalWires() {
	for (int i = 0; i < L; i++) {
	    Wire internalWire = subCircuits[MUX].inputWires[MUX_2Lplus1_L.Y(i)];
	    internalWire.fixWire(0);
	}
    }
}