package com.mnn.society.server.connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.mnn.society.server.events.EventBroadcaster;
import com.mnn.society.server.events.EventCommandParser;
import com.mnn.society.server.events.IClientEvent;
import com.mnn.society.server.execution.WorkQueue;
import com.mnn.society.server.messaging.ClientMessageSender;
import com.mnn.society.server.utils.Logger;

/**
 * TODO: This could probably be used by both client and server
 * Will start the ClientMessageSender (the output message queue sender)
 * will listen for input from the client and pass it along to the WorkQueue
 * @author mickeman
 *
 */
class ConnectionHandler implements Runnable {
	private Socket 				socket;
	private WorkQueue 			queue;
	private EventCommandParser 	parser;
	private UserClient 			client;
	private ClientMessageSender sender;
	private EventBroadcaster 	broadcaster;
	
	public ConnectionHandler(Socket socket, UserClient client, WorkQueue queue, EventBroadcaster broadcaster) {
		this.socket			= socket;
		this.queue 			= queue;
		this.client 		= client;
		this.broadcaster 	= broadcaster;
		this.parser 		= new EventCommandParser(broadcaster);
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		String line;
		
		try {
			// Read a message sent by client application
			BufferedReader d = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        //PrintStream out = new PrintStream(socket.getOutputStream());
	        this.sender = new ClientMessageSender(this.client, new PrintStream(socket.getOutputStream()));
	        Thread t = new Thread(this.sender);
	        t.start();
	        
	        Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : waiting for message from client...");
	       
	        while((line = d.readLine()) != null && !line.equals(".")) {
	        	//input=input + line;
	        	Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : Message received from client: " + line);
	        	line = line.trim();
	        	//the event enters as raw data, we need to parse it and then add it to the worker queue
	        	IClientEvent event = this.parser.getEvent(this.client, line);
	        	if(event != null) {
	        		this.queue.execute((Runnable)event);
	        	}
	        	else {
	        		Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : unrecognized command");
	        	}
	        }
	        // Now write to the client
	        Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : socket closed");
	        this.socket.close();
			this.sender.requestStop();
		} catch (IOException e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : ERROR : IOException : " + e);
		} catch (Exception e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : ConnectionHandler : ERROR : Exception : " + e);
		}
	}
}