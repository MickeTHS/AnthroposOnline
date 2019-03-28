package com.mnn.society.server.user;

import java.sql.ResultSet;

import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.utils.Logger;

public class User {
	//this is the focus point of the user, if events are close to these coordinates, it will be broadcasted to the user
	public int focused_x 	= 0;
	public int focused_y 	= 0;
	public int actual_x 	= 0;
	public int actual_y 	= 0;
	public int user_id 		= 0;
	public String username 	= "";
	
	public static final int BROADCAST_RANGE = 20;
	public static int max_user_id = 0;
	
	//the socket client connection
	public UserClient client;
	
	public User (int user_id, String username, int fx, int fy, int ax, int ay) {
		this.focused_x 	= fx;
		this.focused_y 	= fy;
		this.actual_x 	= ax;
		this.actual_y 	= ay;
		this.user_id 	= user_id;
		this.username 	= username;
	}
	
	/* this should be called only one */
	public static void loadMaxID () {
		Database db = new Database();
		
		ResultSet rs = null;
		
		User.max_user_id = 0;
		
		try {
			rs = Queries.getVariable(db, "AL_ID");
			
			while (rs.next ()) {
				User.max_user_id = rs.getInt("ivar");
				return;
			}
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : User : No users when loading max_user_id, set to 0");
		}
		
		Logger.log(Logger.LOG_SERVER, "SERVER : User : MAX User set to '"+User.max_user_id+"' ");
	}
}
