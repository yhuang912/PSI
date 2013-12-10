// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package Test;

import java.util.*;
import java.math.*;
import java.io.*;

import jargs.gnu.CmdLineParser;

import YaoGC.Wire;
import Utils.*;
import Program.*;

class TestSetIntersectionServer {
    static int N, L, nComm;
    static BigInteger[] x, y;
    static Boolean u;
    static Random rnd = new Random();

    private static void printUsage() {
	System.out.println("Usage: java TestSetIntersectionServer [{-L, --bit-length} L] [{-n} n] [{-j, --joint-size} j] [{-w, --wire-bitlength} w] [{-u, --use-data-file}]");
    }

    private static void process_cmdline_args(String[] args) {
	CmdLineParser parser = new CmdLineParser();
	CmdLineParser.Option optionL = parser.addIntegerOption('L', "bit-length");
	CmdLineParser.Option optionN = parser.addIntegerOption('n', "set-size");
	CmdLineParser.Option optionNComm = parser.addIntegerOption('j', "jont-size");
	CmdLineParser.Option optionWireBL = parser.addIntegerOption('w', "wire-bitlength");
	CmdLineParser.Option optionData = parser.addBooleanOption('u', "use-data-file");

	try {
	    parser.parse(args);
	}
	catch (CmdLineParser.OptionException e) {
	    System.err.println(e.getMessage());
	    printUsage();
	    System.exit(2);
	}

	N = ((Integer) parser.getOptionValue(optionN, new Integer(4))).intValue();
	L = ((Integer) parser.getOptionValue(optionL, new Integer(5))).intValue();
	nComm = ((Integer) parser.getOptionValue(optionNComm, new Integer(2))).intValue();
	Wire.labelBitLength = ((Integer) parser.getOptionValue(optionWireBL, new Integer(80))).intValue();
	u = (Boolean) parser.getOptionValue(optionData, new Boolean(false));
    }

    static boolean isDuplicated(BigInteger[] x, int k) {
	for (int i = 0; i < k; i++)
	    if (x[i].equals(x[k]))
		return true;

	return false;
    }

    static boolean isDuplicated(BigInteger[] x, int end, BigInteger v) {
	for (int i = 0; i < end; i++)
	    if (x[i] == v)
		return true;

	return false;
    }

    static void generateData() throws Exception {
    	x = new BigInteger[N];
	y = new BigInteger[N];
    	for (int i = 0; i < N; i++) {
	    x[i] = new BigInteger(L, rnd);
	    y[i] = new BigInteger(L, rnd);
	    while (isDuplicated(x, i)) {
		x[i] = new BigInteger(L, rnd);
	    }
	    while (isDuplicated(y, i)) {
		y[i] = new BigInteger(L, rnd);
	    }
    	}

	System.out.println("Intersection: ");
	for (int i = 0; i < nComm; i++) {
	    BigInteger val;
	    do {
		val = new BigInteger(L, rnd);
	    } while (isDuplicated(x, N, val) || isDuplicated(y, N, val));

	    x[i] = val;
	    y[i] = val;
	    System.out.print(val + " ");
	}

	// //shuffling
	// for (int i = 2; i < N; i++) {
	//     int k = rnd.nextInt(i-1);
	//     BigInteger tmp = x[k]; x[k] = x[i]; x[i] = tmp;
	// }
	System.out.println();
    }

    static void readDataFile() throws Exception {
	FileInputStream ifs = new FileInputStream("data");
	Scanner s = new Scanner(ifs);
	N = s.nextInt();
	System.out.println(N);
    	x = new BigInteger[N];
	y = new BigInteger[N];

	for (int i = 0; i < N; i++) {
	    x[i] = s.nextBigInteger();
	    y[i] = s.nextBigInteger();

	    System.out.print(x[i] + ", ");
	    System.out.println(y[i]);
	}
    }

    static void localSorting() {
    	Arrays.sort(x);
    	Arrays.sort(y);
	for (int i = 0; i < N/2; i++) {
	    y[i] = y[i].xor(y[N-1-i]); 
	    y[N-1-i] = y[i].xor(y[N-1-i]);
	    y[i] = y[i].xor(y[N-1-i]);
	}

	// System.out.println(Arrays.toString(x));
	// System.out.println(Arrays.toString(y));
    }

    public static void main(String[] args) throws Exception {
	StopWatch.pointTimeStamp("Starting program");
	process_cmdline_args(args);

	if (u == false)
	    generateData();
	else {
	    readDataFile();
	}

	localSorting();

	SetIntersectionServer siserver = new SetIntersectionServer(N, L, x, y);
	siserver.run();
    }
}