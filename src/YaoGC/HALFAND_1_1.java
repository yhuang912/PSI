// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;

import Cipher.*;
import Utils.*;

public abstract class HALFAND_1_1 extends Circuit {
    private static int S;

    protected BigInteger[] gtt;

    public HALFAND_1_1() {
	super(1, 1, "HALFAND");
    }

    public static HALFAND_1_1 newInstance() {
	if (Circuit.isForGarbling)
	    return new G_HALFAND_1_1();
	else
	    return new E_HALFAND_1_1();
    }

    public void build() throws Exception {
	createInputWires();
	createOutputWires();
    }

    protected void createInputWires() {
	super.createInputWires();

	inputWires[0].addObserver(this, new TransitiveObservable.Socket(inputWires, 0));
    }

    protected void createOutputWires() {
	outputWires[0] = new Wire();
    }

    protected void compute() {
	int input  = inputWires[0].value;

	outputWires[0].value = input & S;
    }

    protected void execute() {
	S = OSWAP_2L_2L.S;

	Wire inWire  = inputWires[0];
	Wire outWire = outputWires[0];

	if (inWire.value != Wire.UNKNOWN_SIG) {
	    compute();
	}
	else {
	    outWire.value = Wire.UNKNOWN_SIG;
	    outWire.invd = false;

	    execYao();
	}
	outWire.setReady();
    }

    protected abstract void execYao();

    protected void sendGTT() {
    	try {
	    int bytelength = (Wire.labelBitLength-1)/8 + 1;
	    Utils.writeBigInteger(gtt[1], bytelength, oos);
    		    
    	    oos.flush();
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }
    
    protected void receiveGTT() {
	try {
	    gtt = new BigInteger[2];

	    int bytelength = (Wire.labelBitLength-1)/8 + 1;
	    gtt[0] = BigInteger.ZERO;
	    gtt[1] = Utils.readBigInteger(bytelength, ois);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    protected void fillTruthTable() {
	Wire inWire  = inputWires[0];
	Wire outWire = outputWires[0];

    	BigInteger[] label = {inWire.lbl, Wire.conjugate(inWire.lbl)};
    	if (inWire.invd == true) {
    	    BigInteger tmp = label[0];
    	    label[0] = label[1];
    	    label[1] = tmp;
    	}
	    
    	int k = outWire.serialNum;

	gtt = new BigInteger[2];

	int cIn = inWire.lbl.testBit(0) ? 1 : 0;

	BigInteger[] lb = new BigInteger[2];
	lb[    cIn & S ] = Cipher.encrypt(label[cIn], k, BigInteger.ZERO);
	lb[1- (cIn & S)] = Wire.conjugate(lb[cIn & S]);

	// if (S == 0) {
	//     lb[0] = Cipher.encrypt(label[cIn], k, BigInteger.ZERO);
	//     lb[1] = Wire.conjugate(lb[0]);
	// }
	// else {
	//     lb[cIn] = Cipher.encrypt(label[cIn], k, BigInteger.ZERO);
	//     lb[1-cIn] = Wire.conjugate(lb[cIn]);
	// }
	outWire.lbl = lb[0];

	gtt[0 ^ cIn] = lb[0    ];
	gtt[1 ^ cIn] = lb[1 & S];

	// if (S == 0) {
	//     gtt[0 ^ cIn] = lb[0];
	//     gtt[1 ^ cIn] = lb[0];
	// }
	// else {
	//     gtt[0 ^ cIn] = lb[0];
	//     gtt[1 ^ cIn] = lb[1];
	// }
    }

    protected void encryptTruthTable() {
    	Wire inWire  = inputWires[0];
    	Wire outWire = outputWires[0];

    	BigInteger[] label = {inWire.lbl, Wire.conjugate(inWire.lbl)};
    	if (inWire.invd == true) {
    	    BigInteger tmp = label[0];
    	    label[0] = label[1];
    	    label[1] = tmp;
    	}
	    
    	int k = outWire.serialNum;

	int cIn = inWire.lbl.testBit(0) ? 1 : 0;

	// if (cIn == 1)
	//     gtt[1] = Cipher.encrypt(label[0], k, gtt[1]);
	// if (cIn == 0)
	//     gtt[1] = Cipher.encrypt(label[1], k, gtt[1]);

	gtt[1] = Cipher.encrypt(label[1 ^ cIn], k, gtt[1]);
    }
}