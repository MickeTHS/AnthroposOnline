package com.mnn.society.clientsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.mnn.society.networkinput.NetworkInput;
import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.messaging.ClientMessageSender;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.utils.ThreadLocker;

public class ClientConnection implements Runnable {
	private Socket sock;
	public ClientMessageSender sender;
	public ThreadLocker lock;
	public UserClient user;
	public NetworkInput queue;
	
	public ClientConnection() {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : establishing connection to server...");
		
		// Create a socket with a timeout
		try {
		    //InetAddress addr = InetAddress.getByName("java.sun.com");
			InetAddress addr = InetAddress.getByName("127.0.0.1");
		    int port = 9898;
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);
		    
		    // Create an unbound socket
		    this.sock = new Socket();

		    // This method will block no more than timeoutMs.
		    // If the timeout occurs, SocketTimeoutException is thrown.
		    int timeoutMs = 2000;   // 2 seconds
		    this.sock.connect(sockaddr, timeoutMs);
		    this.user = new UserClient();
		    
		    this.queue = new NetworkInput();
		    
		    this.sender = new ClientMessageSender(this.user, new PrintStream(this.sock.getOutputStream()));
	        //output thread
		    Thread t = new Thread(this.sender);
	        t.start();
	        
	        //input thread
	        Thread t2 = new Thread(this);
	        t2.start();
	        
	        Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : connected");
		} catch (UnknownHostException e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : ERROR : unknown host:" + e);
		} catch (SocketTimeoutException e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : ERROR : timeout: " + e);
		} catch (IOException e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : ERROR : io: " + e);
		}
	}
	
	public void send(String str) {
		
	}
	
	public void send(Request req) {
		this.user.addRequest(req);
	}
	
	public void run() {
		String line;
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : starting to listen for server data");
		
		try {
			BufferedReader d = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
	        
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : waiting for data from server...");
	        
	        
	        while((line = d.readLine()) != null && !line.equals(".")) {
	        	//input=input + line;
	        	Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : Message received from server: " + line);
	        	line = line.trim();
	        	
	        	try {
	        		this.queue.addRequest(new Gson().fromJson(line, Request.class));
	        	}
	        	catch (Exception e) {
	        		Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : improperly formatted string from server");
	        	}
	        	//the event enters as raw data, we need to parse it and then add it to the worker queue
	        }
	        Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : socket closed");
	        this.sock.close();
		} catch (IOException e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ClientConnection : ERROR: " + e);
		}
	}
}
