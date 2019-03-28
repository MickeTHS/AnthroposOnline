package com.mnn.society.server.grid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.utils.ThreadLocker;
import com.mnn.society.math.PerlinNoise;;

public class TheGrid implements Runnable { //the digital frontier
	public static final int GRID_LOAD_WORKERS = 0, GRID_LOAD_RESOURCES = 1, GRID_LOAD_STRUCTURES = 2;
	public static final int GRID_STARTUP_PRIORITY = 5;
	public static final int GRID_NORTH = 0, GRID_EAST = 1, GRID_SOUTH = 2, GRID_WEST = 3, GRID_NO_DIR = -1;
	
	public static List<Worker> 		activeWorkers = new ArrayList<Worker>();
	public static List<Worker> 		inactiveWorkers = new ArrayList<Worker>();
	public static List<Resource> 	loadedResources = new ArrayList<Resource>();
	public static List<Structure> 	loadedStructures = new ArrayList<Structure>();
	public static Map<String, Worker> workerLookup = new HashMap<String, Worker>();
	
	public static long		workerCount = 0;
	
	private ThreadLocker	lock;
	
	public TheGrid () {
		this.lock = new ThreadLocker();
		//starts the maintanance thread, will save workers to the database (hopefully)
		Thread t = new Thread(this);
		t.start();
	}
	
	public void kill () {
		synchronized (this.lock) {
			this.lock.stop();
		}
	}
	
	/* fetch all active and inactive workers for the given player */
	public static WorkerData [] getAllWorkersForPlayer (int player_id) {
		List<Worker> listWorkers = new ArrayList<Worker>();
		Worker w = null;
		
		synchronized (TheGrid.activeWorkers) {
			for (int i = 0; i < TheGrid.activeWorkers.size(); i++) {
				if ((w = TheGrid.activeWorkers.get(i)).getPlayerID() == player_id)
					listWorkers.add(w);
			}
		}
		
		WorkerData [] all = new WorkerData [listWorkers.size()];
		
		int i = 0;
		for (Worker a : listWorkers) {
			a.setWorkerData();
			WorkerData wd = a.getWorkerData();
			a.resetWorkerData();
			
			all[i++] = wd;
		}
		
		return all;
	}
	
	/* loads all the workers in the grid */
	public void loadGrid (int flags, int priority) {
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : starting to load grid with priority " + TheGrid.GRID_STARTUP_PRIORITY);
		
		Worker w = new Worker(1,1,1,1);
		Logger.log(Logger.LOG_SERVER, "JSON: " + new Gson().toJson(w, Worker.class));
		
		Database db = new Database();
		
		ResultSet rs = null;
		
		try {
			rs = Queries.getGridWithPriority(db, priority);
			
			while (rs.next()) {
		        String data = rs.getString("data");
				
		        Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : GridObject string : " + data);
		        
		        GridObject obj = null;
		        
		        try {
		        	obj = new Gson().fromJson(data, Worker.class);
		        	
		        	((Worker)obj).setGridId(rs.getInt("id"));
		        	((Worker)obj).setPlayerID(rs.getInt("player_id"));
		        	((Worker)obj).x = rs.getInt("x");
		        	((Worker)obj).y = rs.getInt("y");
		        	((Worker)obj).jsonData = data; //we need to add this because of searchability in the database
		        	
		        	if (((Worker) obj).idle) {
		        		TheGrid.inactiveWorkers.add((Worker)obj);
			        }
			        else {
			        	Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : active worker added : " + ((Worker)obj).toString());
			        	/*Worker [] t2 = new Worker[1];
			        	t2[0] = (Worker)obj;
			        	
			        	Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : workers serialized : " + new Gson().toJson( t2 ));
			        	*/
			        	TheGrid.activeWorkers.add((Worker)obj);
			        	
			        	//TODO: check if worker has already had work initialized
			        	((Worker)obj).waitingForInitialize = true;
			        }
		        	
		        	synchronized (TheGrid.workerLookup) {
		        		//put in hashmap for fast lookup
			        	TheGrid.workerLookup.put(Integer.toString(((Worker)obj).id), (Worker)obj);
					}
		        		
		        	Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : loaded object is WORKER");
		        } catch (Exception e) {
		        	Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : loaded object is NOT worker");
		        }
			}
		} catch (SQLException ex) {
			Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : Exception when running main query: " + ex);
			ex.printStackTrace();
		}
		
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : done loading grid: ");
		
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : active workers : " + TheGrid.activeWorkers.size());
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : idle workers : " + TheGrid.inactiveWorkers.size());
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : loaded resources : " + TheGrid.loadedResources.size());
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : loaded structures : " + TheGrid.loadedStructures.size());
		
	}
	
	/* function that checks if the worker is allowed to move in the given direction, returns null if not allowed, else returns the new coordinates */
	public int [] validateWorkerDirection (Worker w, int direction) {
		//dont allow workers to go down into water
		int nextx = w.x;
		int nexty = w.y;
		
		if (direction == TheGrid.GRID_EAST) {
			nextx++;
		}
		else if (direction == TheGrid.GRID_WEST) {
			nextx--;
		}
		else if (direction == TheGrid.GRID_NORTH) {
			nexty--;
		}
		else if (direction == TheGrid.GRID_SOUTH) {
			nexty++;
		}
		
		//float target_height = PerlinNoise.getXYValue(nextx, nexty, PerlinNoise.FREQUENCY, PerlinNoise.AMPLITUDE, PerlinNoise.PERSISTANCE, PerlinNoise.OCTAVES);
		float target_height = 5f;
		
		/*if (target_height <= 3) //water
			return null;
		*/
		return new int [] { nextx, nexty };
	}
	
	/* this will create a Worker out of thin air with default/idle properties */
	public void createWorkerForPlayer (int player_id, int x, int y) {
		Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : creating worker for player " + player_id);
		
	}
	
	public void loadPlayerObjectsInGrid (int player_id, int flags) {
		
	}

	//this is the maintanance function, it will continuously save stuff every minute
	@Override
	public void run() {
		//Worker w = null;
		while (true) {
			Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : Saving workers...");
			synchronized (TheGrid.activeWorkers) {
				for (int i = 0; i < TheGrid.activeWorkers.size(); i++) {
					TheGrid.activeWorkers.get(i).save();
				}
			}
			Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : Done saving workers");
			
			synchronized (this.lock) {
				if (this.lock.isStopping()) return;
			}
			
			try {
				Thread.sleep(60000);
			}
			catch (InterruptedException ie) {
				Logger.log(Logger.LOG_SERVER, "SERVER : TheGrid : run() interrupted while sleeping ");
			}
		}
	}
}