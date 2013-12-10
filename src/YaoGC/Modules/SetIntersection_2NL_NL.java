// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC.Modules;

import java.math.BigInteger;
import YaoGC.*;

public class SetIntersection_2NL_NL implements Module {
    int N, L;
    Module[] md = new Module[3];
    final static int SRT = 0;
    final static int FLT = 1;
    final static int SWP = 2;

    public SetIntersection_2NL_NL(int n, int l) {
	N = n;
	L = l;

	md[SRT] = new BitonicSorter_NL_NL(2*n, l);
	md[FLT] = new ComparatorsFilter_2NL_NL(n, l);
	md[SWP] = new WaksmanPermutationNetwork_NL_NL(n, l);
    }

    public State[] execute(State[] numbers) {
	assert numbers.length == 2*N : "Mismatch on length of number array: " + numbers.length + " != " + 2*N;

	State[] states;
	states = md[SRT].execute(numbers);

	// Utils.StopWatch.taskTimeStamp("SORTING");
	
	State[] tmp;
	tmp = md[FLT].execute(states);

	// Utils.StopWatch.taskTimeStamp("COMPARISON");

	states = md[SWP].execute(tmp);

	return states;
    }

    public State execute(State s) { return null; }
}