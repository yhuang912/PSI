// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import Cipher.Cipher;
import Utils.*;

public abstract class SimpleCircuit_2_1 extends Circuit {

    protected BigInteger[][] gtt;

    // public static int counter = 0;

    public SimpleCircuit_2_1(String name) {
	super(2, 1, name);

	// if (this instanceof AND_2_1 || this instanceof OR_2_1)
	//     counter++;
    }

    public void build() throws Exception {
	createInputWires();
	createOutputWires();
    }

    protected void createInputWires() {
	super.createInputWires();

	for (int i = 0; i < inDegree; i++) 
	    inputWires[i].addObserver(this, new TransitiveObservable.Socket(inputWires, i));
    }

    protected void createOutputWires() {
	outputWires[0] = new Wire();
    }

    protected void execute() {
	// if (this instanceof AND_2_1 || this instanceof OR_2_1)
	//     counter++;

	Wire inWireL = inputWires[0];
	Wire inWireR = inputWires[1];
	Wire outWire = outputWires[0];

	if (inWireL.value != Wire.UNKNOWN_SIG && inWireR.value != Wire.UNKNOWN_SIG) {
	    compute();
	}
	else if (inWireL.value != Wire.UNKNOWN_SIG) {
	    if (shortCut())
		outWire.invd = false;
	    else {
		outWire.value = Wire.UNKNOWN_SIG;
		outWire.invd = inWireR.invd;
		outWire.setLabel(inWireR.lbl);
	    }
	}
	else if (inWireR.value != Wire.UNKNOWN_SIG) {
	    if (shortCut()) 
		outWire.invd = false;
	    else {
		outWire.value = Wire.UNKNOWN_SIG;
		outWire.invd = inWireL.invd;
		outWire.setLabel(inWireL.lbl);
	    }
	}
	else {
	    outWire.value = Wire.UNKNOWN_SIG;
	    outWire.invd = false;

	    if (collapse()) {

	    }
	    else {
		execYao();
	    }
	}
	
	// debugPrint();

	outWire.setReady();
    }

    protected abstract void execYao();

    protected abstract boolean shortCut();
    protected abstract boolean collapse();

    protected void sendGTT() {
    	try {
	    int bytelength = (Wire.labelBitLength-1)/8 + 1;
	    Utils.writeBigInteger(gtt[0][1], bytelength, oos);
	    Utils.writeBigInteger(gtt[1][0], bytelength, oos);
	    Utils.writeBigInteger(gtt[1][1], bytelength, oos);
    		    
    	    oos.flush();
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }
    
    protected void receiveGTT() {
	try {
	    gtt = new BigInteger[2][2];

	    int bytelength = (Wire.labelBitLength-1)/8 + 1;
	    gtt[0][0] = BigInteger.ZERO;
	    gtt[0][1] = Utils.readBigInteger(bytelength, ois);
	    gtt[1][0] = Utils.readBigInteger(bytelength, ois);
	    gtt[1][1] = Utils.readBigInteger(bytelength, ois);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    protected void encryptTruthTable() {
    	Wire inWireL = inputWires[0];
    	Wire inWireR = inputWires[1];
    	Wire outWire = outputWires[0];

    	BigInteger[] labelL = {inWireL.lbl, Wire.conjugate(inWireL.lbl)};
    	if (inWireL.invd == true) {
    	    BigInteger tmp = labelL[0];
    	    labelL[0] = labelL[1];
    	    labelL[1] = tmp;
    	}

    	BigInteger[] labelR = {inWireR.lbl, Wire.conjugate(inWireR.lbl)};
    	if (inWireR.invd == true) {
    	    BigInteger tmp = labelR[0];
    	    labelR[0] = labelR[1];
    	    labelR[1] = tmp;
    	}

    	int k = outWire.serialNum;

	int cL = inWireL.lbl.testBit(0) ? 1 : 0;
	int cR = inWireR.lbl.testBit(0) ? 1 : 0;

	if (cL != 0 || cR != 0)
	    gtt[0 ^ cL][0 ^ cR] = Cipher.encrypt(labelL[0], labelR[0], k, gtt[0 ^ cL][0 ^ cR]);
	if (cL != 0 || cR != 1)
	    gtt[0 ^ cL][1 ^ cR] = Cipher.encrypt(labelL[0], labelR[1], k, gtt[0 ^ cL][1 ^ cR]);
	if (cL != 1 || cR != 0)
	    gtt[1 ^ cL][0 ^ cR] = Cipher.encrypt(labelL[1], labelR[0], k, gtt[1 ^ cL][0 ^ cR]);
	if (cL != 1 || cR != 1)
	    gtt[1 ^ cL][1 ^ cR] = Cipher.encrypt(labelL[1], labelR[1], k, gtt[1 ^ cL][1 ^ cR]);
    }

    private void debugPrint() {
	Wire inWireL = inputWires[0];
	Wire inWireR = inputWires[1];
	Wire outWire = outputWires[0];

	if (this instanceof G_AND_2_1 || this instanceof G_OR_2_1) {
	    String gtype;
	    if (this instanceof AND_2_1) {
		System.out.print(Color.blue);
		gtype = new String("AND"); 
	    }
	    else {
		System.out.print(Color.green);
		gtype = new String("OR");
	    }
	    System.out.print(gtype + ": ");
	    if (inWireL.value == Wire.UNKNOWN_SIG) 
		System.out.print("(" + 
				 ((!inWireL.invd) ? 
				  (inWireL.lbl + ", " + inWireL.lbl.xor(Wire.R.shiftLeft(1).setBit(0))) :
				  (inWireL.lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ", " + inWireL.lbl))		      
				 + ") " + gtype + " ");
	    else
		System.out.print(inWireL.value + " " + gtype + " ");

	    if (inWireR.value == Wire.UNKNOWN_SIG)
		System.out.print("(" + 
				 ((!inWireR.invd) ? 
				  (inWireR.lbl + ", " + inWireR.lbl.xor(Wire.R.shiftLeft(1).setBit(0))) :
				  (inWireR.lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ", " + inWireR.lbl))
				 + ") = ");
	    else
		System.out.print(inWireR.value + " = ");

	    if (outWire.value == Wire.UNKNOWN_SIG)
		System.out.print("(" + 
				 ((!outWire.invd) ? 
				  (outWire.lbl + ", " + outWire.lbl.xor(Wire.R.shiftLeft(1).setBit(0))) :
				  (outWire.lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ", " + outWire.lbl))
				 + ").");
	    else
		System.out.print(outWire.value + ".");
	    System.out.println(Color.black);
	}
	else {
	    String gtype;
	    if (this instanceof AND_2_1) {
		System.out.print(Color.blue);
		gtype = new String("AND"); 
	    }
	    else {
		System.out.print(Color.green);
		gtype = new String("OR");
	    }
	    System.out.print(gtype + ": ");
	    if (inWireL.value == Wire.UNKNOWN_SIG) 
		System.out.print(inWireL.lbl + " " + gtype + " ");
	    else
		System.out.print(inWireL.value + " " + gtype + " ");

	    if (inWireR.value == Wire.UNKNOWN_SIG)
		System.out.print(inWireR.lbl + " = ");
	    else
		System.out.print(inWireR.value + " = ");

	    if (outWire.value == Wire.UNKNOWN_SIG)
		System.out.print(outWire.lbl + ". ");
	    else
		System.out.print(outWire.value + ". ");
	    System.out.println(Color.black);
	}
    }
}
