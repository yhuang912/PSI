// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class SORTER_2L_2L extends CompositeCircuit {
    private final int L;

    public final static int GT = 0;
    public final static int CS = 1;

    public SORTER_2L_2L(int l) {
	super(2*l, 2*l, 2, "SORTER");
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[GT] = new GT_2L_1(L);
	subCircuits[CS] = new CONDSWAP_2Lplus1_2L(L);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i+L].connectTo(subCircuits[GT].inputWires, GT_2L_1.X(i));
	    inputWires[i  ].connectTo(subCircuits[GT].inputWires, GT_2L_1.Y(i));

	    inputWires[i+L].connectTo(subCircuits[CS].inputWires, i  );
	    inputWires[i  ].connectTo(subCircuits[CS].inputWires, i+L);
	}

	subCircuits[GT].outputWires[0].connectTo(subCircuits[CS].inputWires, 2*L);
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++) {
	    outputWires[i+L] = subCircuits[CS].outputWires[i];
	    outputWires[i  ] = subCircuits[CS].outputWires[i+L];
	}
    }
}