package com.mnn.society.server.events;

import java.sql.ResultSet;

import com.google.gson.Gson;
import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.grid.TheGrid;
import com.mnn.society.server.grid.WorkerData;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.grid.Worker;


/**
 * The Login event, takes username and password as arguments
 * 
 * @author mickeman
 *
 */
public class LoginEvent implements Runnable, IClientEvent {
	
	private String []			args;
	private String []			outVars;
	private UserClient 			from;
	private EventBroadcaster 	broadcaster;
	
	@Override
	public void setArgs(UserClient from, EventBroadcaster broadcaster, String[] args) {
		this.args 			= args;
		this.from 			= from;
		this.broadcaster 	= broadcaster;
	}
	
	@Override
	public String[] getOutVars() {
		return this.outVars;
	}
	
	@Override
	public void run() {
		Logger.log(Logger.LOG_SERVER, "SERVER : LoginEvent : thread running");
		
		int user_id = 0;
		int x = 0, y = 0;
		String username = "";
		String password = ""; //TODO: add password authentication (need some encryption)
		Database db = new Database();
		
		ResultSet rs = null;
		
		try {
			rs = Queries.fetchUser(db, args[0]);
			
			int count = 0;
			
			while (rs.next ()) {
				user_id = rs.getInt("user_id");
				username = rs.getString("username");
				x = rs.getInt("x");
				y = rs.getInt("y");
				
				++count;
				
				break;
			}
			
			if (count == 0 || !(args[0].compareTo("") != 0 && args[0].compareTo(username) == 0)) {
				Logger.log(Logger.LOG_SERVER, "SERVER : LoginEvent : unable to find user '" + count + "' '" + username + "' '" + args[0]+"'");
				user_id = 0;
				username = "";
				password = "";
			}
			else {
				password = "";
				
				//update the activity date
				Queries.setActivityDate(db, username);
				Logger.log(Logger.LOG_SERVER, "SERVER : LoginEvent : login by '" + from.toString() + "' for user '" + username + "'");
				this.from.user_id = user_id;
				this.from.user = new User(user_id, username, x, y, x, y);
				this.from.user.client = this.from; //this looks a bit fucked up, but its all good; we need the player to be linked to the stream, and the stream to be linked to the player
				
				synchronized (this.broadcaster) {
					this.broadcaster.addPlayer(user_id, this.from.user);
				}
			}
			
			rs.close();
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : LoginEvent : SQL Exception: " + e);
		}
		
		db.close();
		
		if(user_id != 0) {
			this.outVars = new String[2];
			this.outVars[0] = Integer.toString(user_id);
			this.outVars[1] = username;

			this.from.addRequest(new Request(RequestType.LOGIN_SUCCESS, new Gson().toJson( new String [] { "Successful login", Integer.toString(user_id), username } ), 0));
			//send information about the workers
			WorkerData [] workers = TheGrid.getAllWorkersForPlayer(user_id);
			
			Logger.log(Logger.LOG_SERVER, "SERVER : LoginEvent : Amount of workers for player : " + workers.length);
			this.from.addRequest(new Request(RequestType.WORKER_OWNER_LIST, new Gson().toJson( workers ), 0));
			
		}
		else {
			this.from.addRequest(new Request(RequestType.LOGIN_FAILURE, new Gson().toJson( new String [] { "Unable to login: Invalid username or password" } ), 0));
		}
	}
}