// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class SPCMP_3L_L extends CompositeCircuit {
    private final int L;
    static final int XOR0 = 0;
    static final int XOR1 = 1;
    static final int  OR0 = 2;
    static final int  OR1 = 3;
    // static final int  OR2 = 4;
    static final int  AND = 4;
    static final int  MUX = 5;

    public SPCMP_3L_L(int l) {
	super(3*l, l, 6, "SPCMP");

	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR0] = new XOR_2L_L(L);
	subCircuits[XOR1] = new XOR_2L_L(L);
	subCircuits[ OR0] = new OR_L_1(L);
	subCircuits[ OR1] = new OR_L_1(L);
	// subCircuits[ OR2] = OR_2_1.newInstance();
	subCircuits[ AND] = AND_2_1.newInstance();
	subCircuits[ MUX]  = new MUX_2Lplus1_L(L);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i].connectTo(subCircuits[XOR0].inputWires, i);
	    inputWires[i+L].connectTo(subCircuits[XOR0].inputWires, i+L);

	    inputWires[i+L].connectTo(subCircuits[XOR1].inputWires, i);
	    inputWires[i+2*L].connectTo(subCircuits[XOR1].inputWires, i+L);

	    subCircuits[XOR0].outputWires[i].connectTo(subCircuits[OR0].inputWires, i);
	    subCircuits[XOR1].outputWires[i].connectTo(subCircuits[OR1].inputWires, i);

	    inputWires[i+L].connectTo(subCircuits[MUX].inputWires, MUX_2Lplus1_L.X(i));
	}

	subCircuits[OR0].outputWires[0].connectTo(subCircuits[AND].inputWires, 0);
	subCircuits[OR1].outputWires[0].connectTo(subCircuits[AND].inputWires, 1);
	subCircuits[AND].outputWires[0].connectTo(subCircuits[MUX].inputWires, 2*L);
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