// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC.Modules;

import YaoGC.*;

public class BitonicSorter_NL_NL implements Module {

    int N, L;
    Circuit sorter;

    public BitonicSorter_NL_NL(int n, int l) {
	N = n;
	L = l;

	sorter = new SORTER_2L_2L(l);
	try{
	    sorter.build();
	}
	catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public State[] execute(State[] numbers) {
	assert numbers.length == N : "Mismatch on length of number array";
	
	for (int step = N/2; step >= 1; step /= 2) {
	    for (int groups = 0; groups < N/2/step; groups++) {
		for (int i = 0; i < step; i++) {
		    State in = State.fromConcatenation(numbers[i + groups*step*2], numbers[i + groups*step*2 + step]);

		    State out = sorter.startExecuting(in);
		    
		    numbers[i + groups*step*2       ] = State.extractState(out, L, 2*L);  // min
		    numbers[i + groups*step*2 + step] = State.extractState(out, 0, L);    // max
		}
	    }
	}

	return numbers;
    }

    public State execute(State s) { return null; }
}