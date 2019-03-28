package com.mnn.society.server.connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.mnn.society.server.events.EventBroadcaster;
import com.mnn.society.server.execution.WorkQueue;
import com.mnn.society.server.startup.Startup;
import com.mnn.society.server.utils.Logger;

public class SocketServer implements Runnable {
	private int timeoutMilliseconds = 10000;
	private int counter = 0;
	private ServerSocket server;
	private int port = 9898;
	//holds all the connections
	//TODO: Cleanup timeout connections
	private List<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();
	private WorkQueue queue;
	private EventBroadcaster broadcaster;
	
	public SocketServer(EventBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
		try {
			this.server = new ServerSocket(port);
			queue = new WorkQueue(40);
		} catch (IOException e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : SocketServer : Exception : ");
			e.printStackTrace();
		}
	}
	
	public void handleConnection() {
		Logger.log(Logger.LOG_SERVER, "SERVER : SocketServer : Waiting for client message...");

		// The server do a loop here to accept all connection initiated by the
		// client application
		// TODO: Should probably not run forever?
		while (true) {
			try {
				this.server.setSoTimeout(timeoutMilliseconds);
				Socket socket = this.server.accept();
				
				Logger.log(Logger.LOG_SERVER, "SERVER : SocketServer : got connection");
				//at this point, we dont have an actual User, we must log in first
				UserClient client = new UserClient();
				
				ConnectionHandler conn = new ConnectionHandler(socket, client, this.queue, this.broadcaster);
				this.connections.add(conn);
			} catch (SocketTimeoutException ste) {
				this.counter = this.counter >= Logger.MAX_INT_COUNTER ? 0 : this.counter+1;
				if (this.counter % 60 == 0) //log once every 10 minutes (roughly)
					Logger.log(Logger.LOG_SERVER, "SERVER : SocketTimeoutException : " + ste);
			} catch (IOException e) { 
				Logger.log(Logger.LOG_SERVER, "SERVER : SockerServer : ERROR : IOException : " + e);
			}
	    }
	}

	@Override
	public void run() {
		handleConnection();
	}
}

