package com.mnn.society.server.messaging;

/**
 * This was originally a bit more advanced when i was coding the iPhoneServer.
 * I needed a message format that filled the string with the exact amount of 
 * characters and ended with \n, or else it wouldnt be read by the iPhone app.
 * Now, i just add \n to the end. So this class is overkill more or less.
 * @author mickeman
 *
 */
public class Message {
	private String messageToSend;
	
	public Message(String messageToSend) {
		this.messageToSend = messageToSend;
	}
	
	@Override
	public String toString() {
		return this.messageToSend + "\n";
		//return ConnUtils.fillBuffer(this.messageToSend);
	}
}