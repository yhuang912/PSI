// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Cipher;

import java.security.*;
import java.math.BigInteger;
import YaoGC.Wire;

public final class Cipher {
    private static final int unitLength = 160;   // SHA-1 has 160-bit output.

    private static final BigInteger mask = BigInteger.ONE.
	shiftLeft(Wire.labelBitLength).subtract(BigInteger.ONE);

    private static MessageDigest sha1 = null;

    static {
	try {
	    sha1 = MessageDigest.getInstance("SHA-1");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public static BigInteger encrypt(BigInteger lp0, BigInteger lp1, 
				     int k, BigInteger m) {
	BigInteger ret = getPadding(lp0, lp1, k);
	ret = ret.xor(m);

	return ret;
    }

    public static BigInteger decrypt(BigInteger lp0, BigInteger lp1, 
				     int k, BigInteger c) {
	BigInteger ret = getPadding(lp0, lp1, k);
	ret = ret.xor(c);

	return ret;
    }

    // this padding generation function is dedicated for encrypting garbled tables.
    private static BigInteger getPadding(BigInteger lp0, BigInteger lp1, int k) {
	sha1.update(lp0.toByteArray());
	sha1.update(lp1.toByteArray());
	sha1.update(BigInteger.valueOf(k).toByteArray());
	return (new BigInteger(sha1.digest())).and(mask);
    }


    // the following three functions are dedicated for implementing negation gate
    public static BigInteger encrypt(BigInteger lp, int k, BigInteger m) {
	BigInteger ret = getPadding(lp, k);
	ret = ret.xor(m);

	return ret;
    }

    public static BigInteger decrypt(BigInteger lp, int k, BigInteger c) {
	BigInteger ret = getPadding(lp, k);
	ret = ret.xor(c);

	return ret;
    }

    private static BigInteger getPadding(BigInteger lp, int k) {
	sha1.update(lp.toByteArray());
	sha1.update(BigInteger.valueOf(k).toByteArray());
	return (new BigInteger(sha1.digest())).and(mask);
    }


    public static BigInteger encrypt(BigInteger key, BigInteger msg, int msgLength) {
	return msg.xor(getPaddingOfLength(key, msgLength));
    }

    public static BigInteger decrypt(BigInteger key, BigInteger cph, int cphLength) {
	return cph.xor(getPaddingOfLength(key, cphLength));
    }

    private static BigInteger getPaddingOfLength(BigInteger key, int padLength) {
	sha1.update(key.toByteArray());
	BigInteger pad = BigInteger.ZERO;
	byte[] tmp = new byte[unitLength / 8];
	for (int i = 0; i < padLength / unitLength; i++) {
	    System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength/8);
	    pad = pad.shiftLeft(unitLength).xor(new BigInteger(1, tmp));
	    sha1.update(tmp);
	}
	System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength/8);
	pad = pad.shiftLeft(padLength % unitLength).
	    xor((new BigInteger(1, tmp)).
		shiftRight(unitLength - (padLength % unitLength)));
	return pad;
    }

    public static BigInteger encrypt(int j, BigInteger key, BigInteger msg, int msgLength) {
	return msg.xor(getPaddingOfLength(j, key, msgLength));
    }

    public static BigInteger decrypt(int j, BigInteger key, BigInteger cph, int cphLength) {
	return cph.xor(getPaddingOfLength(j, key, cphLength));
    }

    private static BigInteger getPaddingOfLength(int j, BigInteger key, int padLength) {
	sha1.update(BigInteger.valueOf(j).toByteArray());
	sha1.update(key.toByteArray());
	BigInteger pad = BigInteger.ZERO;
	byte[] tmp = new byte[unitLength / 8];
	for (int i = 0; i < padLength / unitLength; i++) {
	    System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength/8);
	    pad = pad.shiftLeft(unitLength).xor(new BigInteger(1, tmp));
	    sha1.update(tmp);
	}
	System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength/8);
	pad = pad.shiftLeft(padLength % unitLength).
	    xor((new BigInteger(1, tmp)).shiftRight(unitLength - (padLength % unitLength)));
	return pad;
    }


    private static BigInteger mark = new BigInteger((new String("GOOD")).getBytes());

    public static BigInteger encrypt(BigInteger key1, BigInteger key2, BigInteger msg, int msgLength) {
	msg = msg.shiftLeft(mark.bitLength()).or(mark);
	return msg.xor(getPaddingOfLength(key1.shiftLeft(key2.bitLength()+1).or(key2), msgLength + mark.bitLength()));
    }

    public static DecryptOutput decrypt(BigInteger key1, BigInteger key2, BigInteger cph, int msgLength) {
	BigInteger result = cph.xor(getPaddingOfLength(key1.shiftLeft(key2.bitLength()+1).or(key2), msgLength+mark.bitLength()));
	BigInteger mask = BigInteger.ONE.shiftLeft(mark.bitLength()).subtract(BigInteger.ONE);
	boolean s = (mask.and(result).equals(mark)) ? true : false;

	if (s) {
	    return new Cipher.DecryptOutput(result.shiftRight(mark.bitLength()), true);
	}
	else
	    return new Cipher.DecryptOutput(null, false);
    }

    public static class DecryptOutput {
	public BigInteger plaintext;
	public boolean succeed;

	public DecryptOutput(BigInteger p, boolean s) {
	    plaintext = p;
	    succeed = s;
	}
    }
}
