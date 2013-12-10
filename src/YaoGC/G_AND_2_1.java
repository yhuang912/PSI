// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class G_AND_2_1 extends AND_2_1 {
    public G_AND_2_1() {
	super("G_AND_2_1");
    }

    protected void execYao() {
	fillTruthTable();
	encryptTruthTable();
	// permuteTruthTable();
	sendGTT();
	gtt = null;
    }
}
