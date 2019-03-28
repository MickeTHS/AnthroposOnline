package com.mnn.society.server.messaging;

import com.google.gson.Gson;

/**
 * This is the data that is being sent back and forth between the server.
 * wparam is a JSONed string with the important data.
 * request is the actual RequestType (defined in the java class RequestType)
 * lparam is usually the source player, maybe should change this
 * 
 * @author mickeman
 *
 */
public class Request {
	public int request;
	public String wparam; //jsoned string 
	public long lparam;
	
	public Request () { } //empty constructor for JSON
	
	public Request (int request, String wparam, long lparam) {
		this.request = request;
		this.wparam = wparam;
		this.lparam = lparam;
	}
	
	public String toString () {
		return new Gson().toJson(this, Request.class);
	}
}