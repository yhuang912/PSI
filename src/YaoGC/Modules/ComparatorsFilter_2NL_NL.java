// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC.Modules;

import YaoGC.*;

public class ComparatorsFilter_2NL_NL implements Module {
    int N, L;
    Circuit spcmp3;
    Circuit spcmp2;

    public ComparatorsFilter_2NL_NL(int n, int l) {
	N = n;
	L = l;

	spcmp3 = new SPCMP_3L_L(l);
	spcmp2 = new SPCMP_2L_L(l);
	try{
	    spcmp3.build();
	    spcmp2.build();
	}
	catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public State[] execute(State[] numbers) {
	assert numbers.length == 2*N : "Mismatch on length of number array";

	State[] res = new State[N];
	for (int i = 0; i < 2*N-2; i+=2) {
	    State in = State.fromConcatenation(numbers[i+1], numbers[i]);
	    in = State.fromConcatenation(numbers[i+2], in);
	    State out = spcmp3.startExecuting(in);
	    
	    res[i/2] = State.extractState(out, 0, L);
	}
	State in = State.fromConcatenation(numbers[2*N-1], numbers[2*N-2]);
	res[N-1] = spcmp2.startExecuting(in);

	return res;
    }

    public State execute(State s) { return null; }
}