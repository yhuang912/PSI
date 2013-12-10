// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.BigInteger;
import Cipher.Cipher;

class E_HALFAND_1_1 extends HALFAND_1_1 {
    public E_HALFAND_1_1() {}

    protected void execYao() {
	Wire inWire  = inputWires[0];
	Wire outWire = outputWires[0];

	receiveGTT();

	int i0 = Wire.getLSB(inWire.lbl);
	i0 = inWire.invd ? (1 - i0) : i0;
	
	BigInteger out = Cipher.decrypt(inWire.lbl, 
					outWire.serialNum, gtt[i0]);
	
	outWire.setLabel(out);
    }
}