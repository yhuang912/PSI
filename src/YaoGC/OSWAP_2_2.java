// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class OSWAP_2_2 extends CompositeCircuit {
    private final static int XOR0 = 0;
    private final static int XOR1 = 1;
    private final static int XOR2 = 2;
    private final static int HAND = 3;
    
    public  final static int X    = 0;
    public  final static int Y    = 1;
    
    public OSWAP_2_2() {
	super(2, 2, 4, "OSWAP");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR0] = new XOR_2_1();
	subCircuits[XOR1] = new XOR_2_1();
	subCircuits[XOR2] = new XOR_2_1();
	subCircuits[HAND] = HALFAND_1_1.newInstance();
	
	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[X].connectTo(subCircuits[XOR0].inputWires, 0);
	inputWires[X].connectTo(subCircuits[XOR1].inputWires, 0);
	inputWires[Y].connectTo(subCircuits[XOR0].inputWires, 1);
	inputWires[Y].connectTo(subCircuits[XOR2].inputWires, 0);

	subCircuits[XOR0].outputWires[0].connectTo(subCircuits[HAND].inputWires, 0);

	subCircuits[HAND].outputWires[0].connectTo(subCircuits[XOR1].inputWires, 1);
	subCircuits[HAND].outputWires[0].connectTo(subCircuits[XOR2].inputWires, 1);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[XOR1].outputWires[0];
	outputWires[1] = subCircuits[XOR2].outputWires[0];
    }
}