package com.mnn.society.server.database;

import java.sql.*;

/**
 * holds ALL the SQL queries that needs to be executed on the server.
 * shouldnt be any other source code file with queries other than this one.
 * @author mickeman
 */
public class Queries {
	
	/* loads all the grid stuff owned by the player (do we need this?) */
	public static ResultSet getPlayerGrid (Database db, int player_id) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT * FROM mmorts_griddata WHERE player_id = ?");
		ps.setInt(1, player_id);
		
		return ps.executeQuery();
	}
	
	/* loads the grid objects with priority less or equal than given value */
	public static ResultSet getGridWithPriority (Database db, int priority) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT * FROM mmorts_griddata WHERE priority <= ?");
		ps.setInt(1, priority);
		
		return ps.executeQuery();
	}
	
	/* updates objects in the grid using x,y,data as key */
	public static int updateGrid (Database db, int id, int x, int y, String data, int player_id, int priority) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"UPDATE mmorts_griddata SET x = ?, y = ?, data = ?, player_id = ?, priority = ? WHERE id = ?");
		
		ps.setInt(1, x);
		ps.setInt(2, y);
		ps.setString(3, data);
		ps.setInt(4, player_id);
		ps.setInt(5, priority);
		ps.setInt(6, id);
		
		return ps.executeUpdate();
	}
	
	/* adds a new record to the grid */
	public static int addToGrid (Database db, int x, int y, String data, int player_id, int priority) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"INSERT INTO mmorts_griddata (id, x, y, data, player_id, priority) VALUES (0, ?, ?, ?, ?, ?)");
		
		ps.setInt(1, x);
		ps.setInt(2, y);
		ps.setString(3, data);
		ps.setInt(4, player_id);
		ps.setInt(5, priority);
		
		return ps.executeUpdate();
	}
	
	/* creates the user, returns > 0 if success */
	public static int createUser (Database db, String username, String password, String color, int x, int y, String serial_prefix) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"INSERT INTO mmorts_user (user_id, username, password, color, x, y, serialno, cdate, adate) VALUES (0, ?, ?, ?, ?, ?, ?, NOW(), NOW())");
		ps.setString(1, username);
		ps.setString(2, password);
		ps.setString(3, color);
		ps.setInt(4, x);
		ps.setInt(5, y);
		ps.setString(6, serial_prefix);
		
		return ps.executeUpdate();
	}
	
	/* returns the user with given username */
	public static ResultSet fetchUser (Database db, String username) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT * FROM mmorts_user WHERE username = ?");
		ps.setString(1, username);
		return ps.executeQuery();
	}
	
	/* gets a count of the user, if > 0 then it exists */
	public static ResultSet countUser (Database db, String username) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT COUNT(*) as count_users FROM mmorts_user WHERE username = ?");
		ps.setString(1, username);
		return ps.executeQuery();
	}
	
	/* gets the max user id */
	public static ResultSet getMaxUserID (Database db) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT MAX(user_id) AS max_user FROM mmorts_user");
		return ps.executeQuery();
	}
	
	/* sets the last activity date for the user */
	public static int setActivityDate (Database db, String username) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"UPDATE mmorts_user SET adate = NOW() WHERE username = ?");
		ps.setString(1, username);
		
		return ps.executeUpdate();
	}
	
	/* gets a specific variable stored in the database */
	public static ResultSet getVariable (Database db, String var_id) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"SELECT var_id, str, ivar FROM mmorts_variables WHERE var_id = ?");
		ps.setString(1, var_id);
		return ps.executeQuery();
	}
	
	/* just updates a variable */
	public static int updateVariable (Database db, String var_id, String str, int ivar) throws SQLException {
		PreparedStatement ps = db.getConnection().prepareStatement(
				"UPDATE mmorts_variables SET str = ?, ivar = ? WHERE var_id = ?");
		ps.setString(1, str);
		ps.setInt(2, ivar);
		ps.setString(3, var_id);
		
		return ps.executeUpdate();
	}
	
	/*
	
	create table mmorts_variables (
    var_id varchar(20) not null,
    str text,
    ivar int
	);
	
	create table mmorts_user (
    user_id int primary key auto_increment,
    username varchar(30) not null,
    password varchar(65),
    color varchar(8),
    cdate datetime,
    adate datetime,
    x int,
    y int, 
    serialno varchar(20)
	);
	
	create table mmorts_griddata (
	id int primary key auto_increment, 
	x int, 
	y int, 
	data text,
	priority int,
	player_id int
	);
	
	insert into mmorts_variables (var_id, ivar) values ('AL_ID', 1);
	*/
}