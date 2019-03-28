package com.mnn.society.server.events;

import com.mnn.society.server.connection.UserClient;

/**
 * When a Request has been sent to the server, and request must become an event.
 * A thread will be taken from the WorkQueue pool and be run as soon as resources
 * are availible.
 * @author mickeman
 *
 */
public interface IClientEvent {
	//Sets the input variables
	public void setArgs(UserClient from, EventBroadcaster broadcast, String [] args);
	//gets the output variables, the result of the Event, not really used
	public String[] getOutVars();
}