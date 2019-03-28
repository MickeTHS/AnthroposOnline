package com.mnn.society.server.database;

/**
 

These tables are the old tables used with the iPhoneServer
Since I removed the Queries for the iPhoneServer i might as well remove these as well
but I'll keep em for now
 create table users (
    user_id int primary key auto_increment, 
    username varchar(64),
    password varchar(32),
    cdate datetime,
    adate datetime
);

create table matches (
    match_id int primary key auto_increment,
    host_user int,
    client_user int,
    cdate datetime,
    host_status varchar(20),
    client_status varchar(20),
    current_question int,
    categories varchar(50),
    rules int,
    timeout datetime,
    winner int,
    host_match_points int,
    client_match_points int,
    status varchar(20)
);
 
 
 MMORTS SCRIPTS:
 
 These are the ones were actually using atm:
 
 create database mmorts;
 use mmorts;
 
 create table mmorts_user (user_id int primary key auto_increment, 
    username varchar(200),
    color varchar(6),
    cdate datetime, 
    adate datetime,
    x int,
    y int);
  
  insert into mmorts_user (user_id, username, color, cdate, adate, x, y) values (0, 'mickeman','0000FF',NOW(),NOW(),0,0);
    
  delimiter $$

  CREATE TABLE `mmorts_griddata` (
    `x` int(11) NOT NULL,
    `y` int(11) NOT NULL,
    `data` varchar(512) NOT NULL,
    `player_id` int(11) DEFAULT NULL,
    `priority` int(11) NOT NULL,
    KEY `ix_griddata_xy` (`x`,`y`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1$$

 */

import java.sql.*;

import com.mnn.society.server.utils.Logger;

public class Database {
	private Connection conn;
	
	public Database() {
		try {
	    	String userName = "root";
	        String password = "hojhoj17";
	        String url = "jdbc:mysql://localhost:3306/mmorts";
	        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
	        conn = DriverManager.getConnection (url, userName, password);
	        Logger.log(Logger.LOG_SERVER, "SERVER : Database: Database connection established");
	    }
	    catch (Exception e) {
	    	Logger.log(Logger.LOG_SERVER, "SERVER : Database: ERROR : Cannot connect to database server: " + e);
	    }
	}
	
	/* gets the active connection */
	public Connection getConnection() {
		return conn;
	}
	
	/* when running select queries */
	public ResultSet query(String query) throws SQLException {
		Statement s = conn.createStatement();
		//TODO: Check for injections
		s.executeQuery (query);
		ResultSet rs = s.getResultSet();
		
		return rs;
	}
	
	/* when doing inserts and updates */
	public void update(String query) throws SQLException {
		Statement s = conn.createStatement();
		//TODO: Check for injections
		s.executeUpdate(query);
	}
	
	/* dont forget to close the connection! */
	public void close() {
		if (conn != null) {
			try {
				conn.close ();
				Logger.log(Logger.LOG_SERVER, "SERVER : Database: Database connection terminated");
			}
			catch (Exception e) { /* ignore close errors */ }
		}
	}
}