// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import java.util.*;

public class Wire extends TransitiveObservable {
    public static final int UNKNOWN_SIG = -1;

    // These four fields are for garbling
    public static int K = 0;
    public static Random rnd = new Random();
    public static int labelBitLength;    // 79 bit for label; 1 bit for encoded permutation

    public static BigInteger R;

    public final int serialNum;
    public int value = UNKNOWN_SIG;
    public BigInteger lbl;
    public boolean invd = false;

    public Wire() {
	serialNum = K++;
	lbl = new BigInteger(labelBitLength, rnd);
	// lbl = lbl.clearBit(0);
    }

    public void reset() {
	lbl = new BigInteger(labelBitLength, rnd);
	// lbl = lbl.clearBit(0);
    }

    public static BigInteger[] newLabelPair() {
	BigInteger[] res = new BigInteger[2];
	res[0] = new BigInteger(labelBitLength, rnd);
	// res[0] = res[0].clearBit(0); 
	res[1] = conjugate(res[0]);
	return res;
    }
    
    public static BigInteger conjugate(BigInteger label) {
	if (label == null)
	    return null;

	return label.xor(R.shiftLeft(1).setBit(0));
    }

    public void setLabel(BigInteger label) {
	lbl = label;
    }

    public void setReady() {
	setChanged();
 	notifyObservers();
    }

    public void connectTo(Wire[] ws, int idx) {
	Wire w = ws[idx];

	for (int i = 0; i < w.observers.size(); i++) {
	    TransitiveObserver ob = w.observers.get(i);
	    TransitiveObservable.Socket s = w.exports.get(i);
	    this.addObserver(ob, s);
	    s.updateSocket(this);
	}

	w.deleteObservers();
	ws[idx] = this;
    }

    public void fixWire(int v) {
	this.value = v;
	
	for (int i = 0; i < this.observers.size(); i++) {
	    Circuit c = (Circuit) this.observers.get(i);
	    c.inDegree--;
	    if (c.inDegree == 0) {
		c.compute();
		for (int j = 0; j < c.outDegree; j++)
		    c.outputWires[j].fixWire(c.outputWires[j].value);
	    }
	}
    }

    // protected static BigInteger getNonce(BigInteger lp) {
    // 	return lp.shiftRight(1);
    // }

    protected static int getLSB(BigInteger lp) {
	return lp.testBit(0) ? 1 : 0;
    }

    public static void printLabel(String msg, BigInteger lbl) {
	if (lbl == null)
	    System.out.println("[" + msg + "] null");
	else
	    System.out.println("[" + msg + "] " + lbl.toString(16));
    }

    public static void printConjuLabel(String msg, BigInteger lbl) {
	if (lbl == null)
	    System.out.println("[" + msg + "] null");
	else
	    System.out.println("[" + msg + "] " + conjugate(lbl).toString(16));
    }

    public static void printLabels(String msg, BigInteger[] lbs) {
	System.out.print("[" + msg + "] ");
	for (int i = 0; i < lbs.length; i++) {
	    if (lbs[i] == null)
		System.out.print("null");
	    else
		System.out.print(lbs[i].toString(16));
	    if (i != lbs.length-1)
		System.out.print(", ");
	    else
		System.out.println();
	}
    }

    public static void printConjuLabels(String msg, BigInteger[] lbs) {
	System.out.print("[" + msg + "] ");
	for (int i = 0; i < lbs.length; i++) {
	    if (lbs[i] == null)
		System.out.print("null");
	    else
		System.out.print(conjugate(lbs[i]).toString(16));
	    if (i != lbs.length-1)
		System.out.print(", ");
	    else
		System.out.println();
	}
    }
}
