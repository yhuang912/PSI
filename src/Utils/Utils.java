// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>
package Utils;

import java.math.*;
import java.util.*;
import java.io.*;

public class Utils {

    private Utils(){}

    public static BigInteger packInts(int[] numbers, int unitLength, int effLength) {
	BigInteger result = BigInteger.ZERO;
	BigInteger mask = BigInteger.ONE.shiftLeft(effLength).subtract(BigInteger.ONE);

	for (int i = 0; i < numbers.length; i++) {
	    result = result.shiftLeft(unitLength);
	    result = result.or(BigInteger.valueOf(numbers[i]).and(mask));
	}

	return result;
    }

    public static int[] unpackInts(BigInteger packedNum, int count, int unitLength, int effLength) {
	int[] result = new int[count];
	BigInteger mask = BigInteger.ONE.shiftLeft(effLength-1).subtract(BigInteger.ONE);

	for (int i = 0; i < count; i++) {
	    BigInteger tmp = packedNum.and(mask);
	    result[count-1-i] = tmp.intValue();
	    packedNum = packedNum.shiftRight(unitLength);
	}

	return result;
    }

    public static BigInteger packNumbers(BigInteger[] numbers, int unitLength, int effLength) {
	BigInteger result = BigInteger.ZERO;
	BigInteger mask = BigInteger.ONE.shiftLeft(effLength).subtract(BigInteger.ONE);

	for (int i = 0; i < numbers.length; i++) {
	    result = result.shiftLeft(unitLength);
	    result = result.or(numbers[i].and(mask));
	}

	return result;
    }

    public static BigInteger[] unpackNumbers(BigInteger packedNum, int count, int unitLength, int effLength) {
	BigInteger[] result = new BigInteger[count];
	BigInteger mask = BigInteger.ONE.shiftLeft(effLength-1).subtract(BigInteger.ONE);

	for (int i = 0; i < count; i++) {
	    result[count-1-i] = packedNum.and(mask);
	    packedNum = packedNum.shiftRight(unitLength);
	}

	return result;
    }

    /* 
     * Write the least significant n bytes of the BigInteger m to os. (Serialization)
     */
    public static void writeBigInteger(BigInteger m, int n, OutputStream os) {
	byte[] temp = new byte[n];
	BigInteger mask = BigInteger.valueOf(0xFF);

	for (int j = 0; j < n; j++) {
	    temp[j] = (byte) m.and(mask).intValue();
	    m = m.shiftRight(8);
	}

	try {
	    os.write(temp);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /* 
     * Read a BigInteger of n bytes from "is", which is written with writeBigInteger. (Deserialization)
     */
    public static BigInteger readBigInteger(int n, InputStream is) {
	DataInputStream dis = new DataInputStream(is);
	BigInteger ret = BigInteger.ZERO;

	byte[] temp = new byte[n];
	try {
	    dis.readFully(temp, 0, n);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	for (int j = n-1; j >= 0; j--) {
	    ret = ret.or(BigInteger.valueOf(0xFF & temp[j]));
	    ret = ret.shiftLeft(8);
	}
	ret = ret.shiftRight(8);

	return ret;
    }


    public static void writeBigInteger(BigInteger m, ObjectOutputStream oos) throws Exception {
	byte[] bytes = m.toByteArray();
	oos.writeInt(bytes.length);
	oos.write(bytes);
    }

    /* 
     * Read a BigInteger of n bytes from "is", which is written with writeBigInteger. (Deserialization)
     */
    public static BigInteger readBigInteger(ObjectInputStream ois) throws Exception {
	DataInputStream dis = new DataInputStream(ois);
	int length = ois.readInt();
	byte[] bytes = new byte[length];
	dis.readFully(bytes, 0, length);

	return new BigInteger(bytes);
    }

    public static void writeBigIntegerArray(BigInteger[] arr, int n, ObjectOutputStream oos) throws Exception {
	int length = arr.length;
	oos.writeInt(length);

	for (int i = 0; i < length; i++) 
	    Utils.writeBigInteger(arr[i], n, oos);

	oos.flush();
    }

    public static BigInteger[] readBigIntegerArray(int n, ObjectInputStream ois) throws Exception {
	int length = ois.readInt();
	BigInteger[] ret = new BigInteger[length];

	for (int i = 0; i < length; i++)
	    ret[i] = Utils.readBigInteger(n, ois);

	return ret;
    }
}
