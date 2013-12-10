// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC.Modules;

import YaoGC.*;

public class WaksmanPermutationNetwork_NL_NL implements Module {

    int N, L;
    Circuit oswap;

    public WaksmanPermutationNetwork_NL_NL(int n, int l) {
	N = n;
	L = l;

	oswap = new OSWAP_2L_2L(l);
	try{
	    oswap.build();
	}
	catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public State[] execute(State[] numbers) {
	assert numbers.length == N : "Mismatch on length of number array";

	State[] nbs = numbers;
	State[] newNbs = new State[nbs.length];
	for (int k = N; k >= 2; k /= 2) {
	    for (int j = 0; j < N/k; j++) {
		for (int i = 0; i < k; i += 2) {
		    State in = State.fromConcatenation(nbs[j*k + i], nbs[j*k + i+1]);
		    State out = oswap.startExecuting(in);
		    newNbs[j*k + i/2      ] = State.extractState(out, L, 2*L);
		    newNbs[j*k + i/2 + k/2] = State.extractState(out, 0,   L);
		}
	    }
	    nbs = newNbs;
	    newNbs = new State[nbs.length];
	}

	for (int k = 4; k <= N; k *= 2) {
	    for (int j = 0; j < N/k; j++) {
		for (int i = 0; i < k; i += 2) {
		    if (i == 0) {
			newNbs[j*k    ] = nbs[j*k];
			newNbs[j*k + 1] = nbs[j*k + k/2];
		    }
		    else {
			State in = State.fromConcatenation(nbs[j*k + i/2], nbs[j*k + i/2 + k/2]);
			State out = oswap.startExecuting(in);
			newNbs[j*k + i  ] = State.extractState(out, L, 2*L);
			newNbs[j*k + i+1] = State.extractState(out, 0,   L);
		    }
		}
	    }
	    nbs = newNbs;
	    newNbs = new State[nbs.length];
	}

	return nbs;
    }

    public State execute(State s) { return null; }
}