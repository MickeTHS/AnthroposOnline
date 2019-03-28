package com.mnn.society.server.connection;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mnn.society.server.messaging.Message;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.user.User;

/**
 * This class ties together a User (the player) with the socket.
 * Requests are added and will be sent to the client. 
 * When a client connects to the server, this object will be tied to that connection.
 * When a user logs in successfully, this.user will be set to the player
 * @author mickeman
 *
 */
public class UserClient {
	
	private List<Message> messagesToSend;
	
	//private ConnectionHandler conn;
	
	public int user_id;
	private String username;
	
	public User user;
	
	public UserClient() {
		//this.conn = conn;
		this.messagesToSend = new ArrayList<Message>();
	}
	
	//get the user from the database
	public void setUser(int user_id, String username) {
		this.user_id = user_id;
		this.username = username;
	}
	
	/**
	 * The Request object added will automatically be JSONed and added to the output buffer
	 * @param req
	 */
	public void addRequest(Request req) {
		addMessage(new Message(new Gson().toJson(req, Request.class)));
	}
	
	/**
	 * DO NOT USE THIS, use addRequest instead
	 * @param msg
	 */
	public void addMessage(Message msg) {
		synchronized(this.messagesToSend) {
			this.messagesToSend.add(msg);
		}
	}
	
	public Message getNextMessage() {
		synchronized(this.messagesToSend) {
			try {
				return this.messagesToSend.remove(0);
			}
			catch(Exception e) {
				return null;
			}
		}
	}
}