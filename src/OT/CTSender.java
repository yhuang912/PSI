package OT;

import java.math.*;
import java.io.*;

public class CTSender extends Sender {

    public CTSender(int numOfPairs, int msgBitLength, 
		  ObjectInputStream in, ObjectOutputStream out) {
	super(numOfPairs, msgBitLength, in, out);
    }

    public void execProtocol(BigInteger[][] msgPairs) throws Exception {
	super.execProtocol(msgPairs);

	// receive choices
	BigInteger choices = (BigInteger) ois.readObject();
	int numOfChoices = ois.readInt();

	if (numOfChoices != numOfPairs)
	    throw new Exception("Unmatched transfer sender and receiver: " + 
				numOfChoices + " != " + numOfPairs);

	// send messages
	BigInteger[] toSend = new BigInteger[numOfChoices];
	for (int i = 0; i < numOfChoices; i++)
	    toSend[i] = msgPairs[i][choices.testBit(i)? 1 : 0];
	oos.writeObject(toSend);
	oos.flush();
    }
}
