package com.mnn.society.input;

import java.io.*;
import java.util.LinkedList;

import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.utils.ThreadLocker;

/**
 * This is just a test to see if it will convert the input to stream to localized input AND if this works in a threading enviroment.
 * If this doesnt work then Im not sure how to solve localization easily.
 * 
 * Update: well, didnt work. scrap this.
 * @author mickeman
 *
 */
public class KeyboardStream implements Runnable {
	private ThreadLocker lock = new ThreadLocker();
	private LinkedList<String> bufferedKeys = new LinkedList<String>();
	
	/* starts a keyboard stream thread */
	public KeyboardStream () {
		Thread t = new Thread(this);
		t.start();
	}
	
	/* if a character has been input, we return it, else null */
	public String getNextChar() {
		synchronized (this.lock) {
			if (this.bufferedKeys.size() == 0)
				return null;
			return this.bufferedKeys.removeFirst();
		}
	}
	
	/* will gracefully stop the thread */
	public void stop () {
		synchronized (this.lock) {
			this.lock.stop();
		}
	}
	
	/* reads from the input buffer */
	public void run () {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : KeyboardStream : starting thread ... ");
		BufferedReader reader; 
		
        reader = new BufferedReader(new InputStreamReader(System.in));
        
        char [] cbuf = new char [1]; 
		
		while (true) {
	        try {
	        	reader.read(cbuf); //in theory, it will read until the end of time if the user closes the program and doesnt input anything on the keyboard
	        	//but once the user has input something in another window, this thread will wake up and throw an exception, and finally die
	        	synchronized (this.lock) {
	        		bufferedKeys.add(Character.toString(cbuf[0]));
	        		Logger.log(Logger.LOG_CLIENT, "CLIENT : KeyboardStream : got input : " + Character.toString(cbuf[0]));
	        	}
	        }
	        catch (IOException ioe){
	        	Logger.log(Logger.LOG_CLIENT, "CLIENT : KeyboarStream : exception : " + ioe);
	        }
	        catch (Exception e) {
	        	Logger.log(Logger.LOG_CLIENT, "CLIENT : KeyboardStream : exception : " + e);
	        }
	        
	        synchronized (this.lock) {
	        	if (this.lock.isStopping() || Thread.interrupted()) {
	        		Logger.log(Logger.LOG_CLIENT, "CLIENT : KeyboardStream : stopping thread");
	        		return;
	        	}
	        }
		}
	}
}