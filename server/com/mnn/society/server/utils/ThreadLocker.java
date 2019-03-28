package com.mnn.society.server.utils;

/**
 * This is not needed really. Probably using it somewhere though. 
 * Its basically just a thread safe approach to stopping a thread.
 * @author mickeman
 *
 */
public class ThreadLocker {
	private boolean stopme = false;
	
	public boolean isStopping() {
		return stopme;
	}
	
	public void stop() {
		stopme = true;
	}
}