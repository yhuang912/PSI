package OT;

import java.math.*;
import java.util.*;
import java.io.*;
import Cipher.Cipher;
import Utils.*;

public class NPOTSender extends Sender {

    private static Random rnd = new Random();
    private static final int certainty = 80;

    private final static int qLength = 128;
    private final static int pLength = 1024;

    private BigInteger p, q, g, C, r;
    private BigInteger Cr, gr;

    public NPOTSender(int numOfPairs, int msgBitLength,
			  ObjectInputStream in, ObjectOutputStream out) throws Exception {
	super(numOfPairs, msgBitLength, in, out);

	StopWatch.pointTimeStamp("right before NPOT public key generation");
	initialize();
	StopWatch.taskTimeStamp("NPOT public key generation");
    }

    public void execProtocol(BigInteger[][] msgPairs) throws Exception {
	super.execProtocol(msgPairs);

	step1();
    }

    private void initialize() throws Exception {
	File keyfile = new File("NPOTKey");
	if (keyfile.exists()) {
	    FileInputStream fin = new FileInputStream(keyfile);
	    ObjectInputStream fois = new ObjectInputStream(fin);
	    
	    C = (BigInteger) fois.readObject();
	    p = (BigInteger) fois.readObject();
	    q = (BigInteger) fois.readObject();
	    g = (BigInteger) fois.readObject();
	    gr = (BigInteger) fois.readObject();
	    r = (BigInteger) fois.readObject();
	    fois.close();

	    oos.writeObject(C);
	    oos.writeObject(p);
	    oos.writeObject(q);
	    oos.writeObject(g);
	    oos.writeObject(gr);
	    oos.writeInt(msgBitLength);
	    oos.flush();

	    Cr = C.modPow(r, p);
	}
	else {
	    BigInteger rr;

	    while (true) {
		q = new BigInteger(qLength, certainty, rnd);
		rr = new BigInteger(pLength - q.bitLength(), rnd);
		p = q.multiply(rr).add(BigInteger.ONE);
		if (p.isProbablePrime(certainty)) {
		    while (true) {
			g = (new BigInteger(qLength, rnd)).mod(q);
			if (g.modPow(BigInteger.valueOf(2), p).equals(BigInteger.ONE) == false) {
			    g = g.modPow(BigInteger.valueOf(2), p);
			    r = (new BigInteger(qLength, rnd)).mod(q);
			    gr = g.modPow(r, p);
			    BigInteger tmp = (new BigInteger(qLength, rnd)).mod(q);
			    C = tmp.modPow(tmp, p);

			    oos.writeObject(C);
			    oos.writeObject(p);
			    oos.writeObject(q);
			    oos.writeObject(g);
			    oos.writeObject(gr);
			    oos.writeInt(msgBitLength);
			    oos.flush();

			    Cr = C.modPow(r, p);

			    FileOutputStream fout = new FileOutputStream(keyfile);
			    ObjectOutputStream foos = new ObjectOutputStream(fout);
	    
			    foos.writeObject(C);
			    foos.writeObject(p);
			    foos.writeObject(q);
			    foos.writeObject(g);
			    foos.writeObject(gr);
			    foos.writeObject(r);

			    foos.flush();
			    foos.close();

			    return;
			}
		    }
		}
	    }

	}
    }

    private void step1() throws Exception {
	BigInteger[] pk0 = (BigInteger[]) ois.readObject();
	BigInteger[] pk1 = new BigInteger[numOfPairs];
	BigInteger[][] msg = new BigInteger[numOfPairs][2];

    	for (int i = 0; i < numOfPairs; i++) {
	    pk0[i] = pk0[i].modPow(r, p);
	    pk1[i] = Cr.multiply(pk0[i].modInverse(p)).mod(p);

	    msg[i][0] = Cipher.encrypt(pk0[i], msgPairs[i][0], msgBitLength);
	    msg[i][1] = Cipher.encrypt(pk1[i], msgPairs[i][1], msgBitLength);
	}

	oos.writeObject(msg);
	oos.flush();
    }
}