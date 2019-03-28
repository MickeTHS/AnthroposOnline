package com.mnn.society.networkinput;

import java.util.LinkedList;

import com.mnn.society.server.messaging.Request;

/**
 * This class will recieve alot of Requests, parse each event one by one by using getNextRequest()
 * @author mickeman
 *
 */
public class NetworkInput {
	public LinkedList<Request> queue = new LinkedList<Request>();
	
	public NetworkInput () {
		
	}
	
	public void addRequest (Request r) {
		synchronized(this.queue) { 
			this.queue.addLast(r);
			this.queue.notify();
		}
	}
	
	public Request getNextRequest () {
		synchronized(this.queue) {
			if (this.queue.size() > 0) {
				return this.queue.removeFirst();
			}
			return null;
		}
	}
}
