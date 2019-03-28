package com.mnn.society.server.events;

import java.sql.ResultSet;

import com.google.gson.Gson;
import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Logger;

/**
 * Create user
 * 
 * @author mickeman
 *
 */
public class CreateUser implements Runnable, IClientEvent {
	
	private String [] 			args;
	private String []			outVars;
	private UserClient 			from;
	private EventBroadcaster 	broadcaster;
	
	@Override //username, password, color, x, y
	public void setArgs (UserClient from, EventBroadcaster broadcaster, String[] args) {
		this.args 			= args;
		this.from 			= from;
		this.broadcaster 	= broadcaster;
	}
	
	@Override
	public String[] getOutVars () {
		return this.outVars;
	}
	
	@Override
	public void run () {
		Logger.log(Logger.LOG_SERVER, "SERVER : CreateUser : thread running");
		
		Database db = new Database();
		
		ResultSet rs = null;
		
		try {
			String username = args[0];
			String password = args[1];
			String color	= args[2];
			int x			= Integer.parseInt(args[3]);
			int y			= Integer.parseInt(args[4]);
			
			rs = Queries.fetchUser(db, args[0]);
			
			while (rs.next ()) {
				int user_id = rs.getInt("user_id");
				if (user_id > 0) return;
			}
			String serial = "AL" + String.format("%-5s", (++User.max_user_id)).replace(' ', '0');
			int id = Queries.createUser(db, username, password, color, x, y, serial);
			Queries.updateVariable (db, "AL_ID", "", User.max_user_id);
			Logger.log(Logger.LOG_SERVER, "SERVER : CreateUser : created user '"+username+"', id '"+id+"'");
			
			this.from.addRequest(new Request(RequestType.USER_CREATE_SUCCESS, new Gson().toJson( new String [] { "success create", Integer.toString(id), username, serial } ), 0));
			
			rs.close();
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : CreateUser : SQL Exception: " + e);
			this.from.addRequest(new Request(RequestType.USER_CREATE_FAIL, new Gson().toJson( new String [] { "unable to create" } ), 0));
		}
		
		db.close();
	}
}