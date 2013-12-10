// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class G_HALFAND_1_1 extends HALFAND_1_1 {
    public G_HALFAND_1_1() {}

    protected void execYao() {
	fillTruthTable();
	encryptTruthTable();
	sendGTT();
	gtt = null;
    }
}