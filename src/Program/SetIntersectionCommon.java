// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;
import java.util.*;
import java.io.*;

import Utils.*;
import YaoGC.*;
import YaoGC.Modules.SetIntersection_2NL_NL;

public class SetIntersectionCommon extends ProgCommon {
    static int N, L;

    static void initCircuits() {
	mod = new SetIntersection_2NL_NL(N, L);
    }

    public static State[] execCircuit(BigInteger[] Xlbs, BigInteger[] Ylbs) throws Exception {
	State[] inputs = new State[2*N];
	for (int i = 0; i < N; i++) {
	    inputs[i  ] = State.fromLabels(Xlbs, i*L, i*L+L);
	    inputs[i+N] = State.fromLabels(Ylbs, i*L, i*L+L);
	}
	
	State[] outputs = mod.execute(inputs);

	StopWatch.taskTimeStamp("circuit executing");

	return outputs;
    }
}