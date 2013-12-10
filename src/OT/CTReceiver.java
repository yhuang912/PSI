package OT;

import java.math.*;
import java.io.*;

/* Concious Transfer receiver */
public class CTReceiver extends Receiver {
    public CTReceiver(int numOfChoices, ObjectInputStream in, ObjectOutputStream out) {
	super(numOfChoices, in, out);
    }

    public void execProtocol(BigInteger choices) throws Exception {
	super.execProtocol(choices);

	sendChoices();
	receiveMessages();
    }

    private void sendChoices() throws Exception {
	oos.writeObject(choices);
	oos.writeInt(numOfChoices);
	oos.flush();
    }

    private void receiveMessages() throws Exception {
	data = (BigInteger[]) ois.readObject();
    }
}
