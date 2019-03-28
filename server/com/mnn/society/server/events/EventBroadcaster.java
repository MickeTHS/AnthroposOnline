package com.mnn.society.server.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mnn.society.server.messaging.Message;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Logger;

/**
 * Class responsible for outgoing events. Events are added to the queue and then broadcaster out to the users who need it.
 * 
 * @author mickeman
 *
 */
public class EventBroadcaster {
	
	//public List<User> connectedUsers = new ArrayList<User>();
	public Map<String, User> playersConnected = new HashMap<String, User>();
	
	public EventBroadcaster () {}
	
	/* when a user is connected, it needs to be added here */
	public void addPlayer (int player_id, User user) {
		System.out.println("Adding player " + player_id + " user " + (user != null ? user.user_id : "null"));
		this.playersConnected.put(Integer.toString(player_id), user);
	}
	
	public void removePlayer (int player_id) {
		this.playersConnected.remove(Integer.toString(player_id));
	}
	
	/* just get the user for player_id */
	public User getUserForPlayer (int player_id) {
		return this.playersConnected.get(Integer.toString(player_id));
	}
	
	/* sends data directly to a user */
	public void sendTo (int player_id, String data) {
		synchronized (this.playersConnected) {
			User u = this.playersConnected.get(player_id);
			
			if (u == null) return; //if the user is not online
			
			try {
				u.client.addRequest( new Request(player_id, data, 0) );
			}
			catch (Exception e) {
				Logger.log(Logger.LOG_SERVER, "SERVER : EventBroadcaster : exception in sendTo: " + e);
			}
		}
	}
	
	/**
	 * Will send the data string to all players within the radius, except for the source_player
	 */
	public void sendToRadius (int source_player, String data, int x, int y, int radius) {
		synchronized (this.playersConnected) {
			Request req = new Request(source_player, data, 0);
			
			for (int i = 0; i < this.playersConnected.size(); i++) {
				User u = this.playersConnected.get(i);
				
				//see if the user is within the coordinate
				if (u.actual_x >= x-radius && u.actual_x <= x+radius && u.actual_y >= y-radius && u.actual_y <= y+radius) {
					try {
						u.client.addRequest(req); //will add the request to the outgoing buffer
					}
					catch (Exception e) {
						Logger.log(Logger.LOG_SERVER, "SERVER : EventBroadcaster : exception in sendToRadius: " + e);
					}
				}
			}
		}
	}
}
