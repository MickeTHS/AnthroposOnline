package com.mnn.society.server.messaging;

/**
 * This is the first class that is interpreted by the client. Not sure I will keep it.
 * @author mickeman
 *
 */
public class RequestGameState {
	public String serverName;
	public int popTotal;
	public int popCurrent;
	public int serverStatus;
	public String motd;
}