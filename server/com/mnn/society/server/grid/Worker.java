package com.mnn.society.server.grid;

import java.sql.SQLException;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.mnn.society.server.bamul.cpu.Core;
import com.mnn.society.server.bamul.parse.BAMUL;
import com.mnn.society.server.bamul.parse.Parser;
import com.mnn.society.server.bamul.utils.BAMULOps;
import com.mnn.society.server.bamul.utils.HardwareInterface;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.utils.Logger;

public class Worker implements GridObject, HardwareInterface {
	public static long milliseconds_per_grid = 10000;
	
	private boolean	saved = true;
	
	private WorkerData		data;
	
	public int 		id;
	public String 	bamul;
	public boolean 	idle;
	
	private int 	player_id;
	private Parser 	parser;
	private BAMUL 	bamulProgram;
	private Core 	core;
	
	public String	jsonData; //we need to add this because of searchability in the database
	
	//all actual values
	public int 		x;
	public int		y; //movement
	
	//all task completed values
	private int		target_x, target_y; //movement
	public int		requested_direction = TheGrid.GRID_NO_DIR;
	public long		arrival;
	public long		departure;
	public boolean 	previousTaskDone = true;
	public boolean	waitingForInitialize = false;
	public boolean	running = false;
	public int		facing;
	
	public String	currentBAMULOpCode;
	private boolean	abortRequested = false;
	private int 	grid_id;
	public int		x_registry = 0;
	public int		y_registry = 0;
	
	/* grid_id is the global grid ID that is shared between all instances in the grid. its universally unique. */
	public Worker (int grid_id, int x, int y, int player_id) {
		this.grid_id = grid_id;
		this.x = x;
		this.y = y;
		this.target_x = this.x;
		this.target_y = this.y;
		this.player_id = player_id;
	}
	
	public void setGridId (int grid_id) {
		this.grid_id = grid_id;
	}
	
	/* will request the script to be aborted */
	public void abortScript (boolean abort) {
		this.abortRequested = abort;
	}
	
	/* is abort requested? */
	public boolean getAbortRequest () {
		return this.abortRequested;
	}
	
	/* workaround for JSON, or else the JSON serializer enters an infinite loop for some reason */
	public void setWorkerData () {
		this.data = new WorkerData(this.id, this.bamul, this.player_id, this.x, this.y, this.facing);
	}
	
	/* workaround for JSON, or else the JSON serializer enters an infinite loop for some reason */
	public void resetWorkerData () {
		this.data = null;
	}
	
	/* workaround for JSON, or else the JSON serializer enters an infinite loop for some reason */
	public WorkerData getWorkerData () {
		return this.data;
	}
	
	public void setPlayerID (int player_id) {
		this.player_id = player_id;
	}
	
	public int getPlayerID () {
		return this.player_id;
	}
	
	/* when the task has completed, this function must be called to join the target variables with the actual variables */
	public void join () {
		this.x = this.target_x;
		this.y = this.target_y;
		this.previousTaskDone = true;
		this.saved = false;
		this.requested_direction = TheGrid.GRID_NO_DIR;
	}
	
	/* responsible for saving the worker to the database */
	public void save () {
		if (!this.saved) return; //if the worker hasnt changed we dont save it
		
		Logger.log(Logger.LOG_SERVER, "SERVER : Worker : save : saving worker '"+this.id+"', belonging to '"+this.player_id+"' to grid database");
		
		this.setWorkerData();
		Database db = new Database();
		
		try {
			String d = new Gson().toJson(this.data, WorkerData.class);
			Queries.updateGrid(db, this.grid_id, this.x, this.y, d, this.player_id, 1);
			this.jsonData = d;
			this.saved = true;
		}
		catch (SQLException sqle) {
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : save() : SQLException : " + sqle);
		}
		
		this.resetWorkerData();
	}
	
	/* basically teleports the worker to given x, y 
	 * TODO: broadcast this to players? */
	public void forcePosition (int x, int y) {
		this.x = x;
		this.y = y;
		this.saved = false;
	}
	
	public void setDestination (int x, int y, long arrival) {
		this.target_x = x;
		this.target_y = y;
		this.arrival = arrival;
	}
	
	/* this should be called then the worker is idle to jumpstart the BAMUL script 
	 * return 1 success
	 * return -1 error
	 * */
	public int initializeWork () {
		Logger.log(Logger.LOG_SERVER, "SERVER : Worker : worker " + this.id + " starting work '"+this.bamul+"'");
		this.parser = new Parser();
		try {
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Parsing BAMUL string...");
			this.bamulProgram = this.parser.parse(this.bamul);
			
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Loading script into Core...");
			this.core = Core.newCoreInstance();
			this.core.loadScript(this.bamulProgram);
			
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Loading hardware connection...");
		
			this.core.connectHardwareInterface(this);
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Unable to initialize work for Worker " + this.id + " : " + e);
			this.parser = null;
			this.bamulProgram = null;
			this.core = null;
			return -1;
		}
		
		this.waitingForInitialize 	= false;
		this.running 				= true;
		this.previousTaskDone 		= true;
		this.idle 					= false;
		
		return 1;
	}
	
	/* when BAMUL script has been initialized, this call is 1 clockcycle */
	public String parseBAMUL () {
		if (!this.core.isEof() && !this.abortRequested) {
			this.core.executeOp();
			return this.core.getCoreDump();
		}
		else 
			return "done";
	}
	
	@Override
	public String toString() {
		return "worker id:" + this.id + ", with bamul \"" + this.bamul + "\"";
	}

	
	/* this is when the action is being requested by the hardware, before it has been executed */
	@Override
	public LinkedList<Integer> hardwareActionCall(int opCode, int target) {
		if (opCode == BAMULOps.HARDWARE_CALL_W) {
			this.requested_direction 	= TheGrid.GRID_WEST;
			this.currentBAMULOpCode 	= "W";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests direction WEST");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_E) {
			this.requested_direction 	= TheGrid.GRID_EAST;
			this.currentBAMULOpCode 	= "E";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests direction EAST");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_S) {
			this.requested_direction 	= TheGrid.GRID_SOUTH;
			this.currentBAMULOpCode 	= "S";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests direction SOUTH");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_N) {
			this.requested_direction 	= TheGrid.GRID_NORTH;
			this.currentBAMULOpCode 	= "N";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests direction NORTH");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_A) { //analyze
			this.currentBAMULOpCode		= "A";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests ANALYZE action");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_B) { //deep analyze
			this.currentBAMULOpCode		= "B";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests DEEP ANALYZE action");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_C) { //construction
			this.currentBAMULOpCode		= "C";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests CONSTRUCTION action");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_D) { //drill
			this.currentBAMULOpCode		= "D";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests DRILL action");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_M) { //item move
			this.currentBAMULOpCode		= "M";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests ITEM MOVE action");
		}
		else if (opCode == BAMULOps.HARDWARE_CALL_R) { //repair
			this.currentBAMULOpCode		= "R";
			Logger.log(Logger.LOG_SERVER, "SERVER : Worker : Worker " + this.id + " requests REPAIR action");
		}
		
		//Logger.log(Logger.LOG_SERVER, "worker : " + this.id + " hardware call : " + opCode);
		return null;
	}
}