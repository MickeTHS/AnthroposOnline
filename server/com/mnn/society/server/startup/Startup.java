package com.mnn.society.server.startup;

import com.google.gson.Gson;
import com.mnn.society.server.connection.SocketServer;
import com.mnn.society.server.events.EventBroadcaster;
import com.mnn.society.server.execution.WorkerExecution;
import com.mnn.society.server.grid.TheGrid;
import com.mnn.society.server.grid.Worker;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.time.ServerTime;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Calculation;
import com.mnn.society.server.utils.Logger;

/**
 * The main startup for the server. This will probably be broken down a bit more.
 * Right now:
 * 
 * 1. Load the grid (all the workers, resources and structures)
 * 2. Starting the SocketServer (as a thread)
 * 3. Initializing the EventBroadcaster (is pretty empty at first, but will get all the connected 'PLAYERS')
 * 4. Starting the WorkerExecutor (as a thread, when a worker command is valid, this thread will execute it)
 * 5. Loops through all active workers
 * 6. Determines the next command
 * 7. Initializes the work if not initialized
 * 8. Validate the command, set the appropriate results of the action and then add it to the WorkerExecutor
 * 9. Repeats from 5.
 * @author mickeman
 *
 */
public class Startup {
	public static final String version = "0.020";
	
	public Startup() {}
	
	private static int counter = 0;
	
	public static void main(String [] args) {
		Logger.log(Logger.LOG_SERVER, "SERVER : Startup : no-named mmo server v"+Startup.version+" starting up....");
		
		//set the global max id
		User.loadMaxID();
		
		TheGrid grid = new TheGrid();
		grid.loadGrid(0, 5); //loads grid with objects having priority lower or equal to 5
		
		Logger.log(Logger.LOG_SERVER, "SERVER : Startup : initializing SocketServer...");
		
		EventBroadcaster broadcaster = new EventBroadcaster();
		
		SocketServer server = new SocketServer(broadcaster);
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		Logger.log(Logger.LOG_SERVER, "SERVER : Startup : Socket Thread started");
		
		WorkerExecution executor = new WorkerExecution(broadcaster);
		
		boolean running = true;
		
		Logger.log(Logger.LOG_SERVER, "SERVER : Startup : Worker execution thread starting up...");
		/* start the worker execute thread, as soon as a BAMUL command has been verified, it will be picked up by the thread and executed */
		Thread t = new Thread(executor);
		t.start();
		Logger.log(Logger.LOG_SERVER, "SERVER : Startup : Worker execution thread started");
		
		while (running) {
			//System.out.println("main loop: active workers: " + TheGrid.activeWorkers.size());
			
			/* loop through all the active workers */
			for ( int i = 0; i < TheGrid.activeWorkers.size(); i++ ) {
				Worker w = TheGrid.activeWorkers.get(i);
				
				if (w.waitingForInitialize) {
					Logger.log(Logger.LOG_SERVER, "SERVER : Startup : work initialized");
					if (w.initializeWork() < 0) { 
						Logger.log(Logger.LOG_SERVER, "SERVER : Startup : failure to initialize work for worker " + w.id);
						continue;
					}
				}
				
				if (w.running && w.previousTaskDone) {
					Logger.log(Logger.LOG_SERVER, "SERVER : Startup : doing work for worker : " + w.id);
					
					String workresult = "";
					
					if ((workresult = w.parseBAMUL()) != "done") {
						
						Logger.log(Logger.LOG_SERVER, "SERVER : Startup : work result : " + workresult);
						
						/* TODO: Move this shit to somewhere else! */
						if (w.requested_direction != TheGrid.GRID_NO_DIR) {
							int [] res = grid.validateWorkerDirection(w, w.requested_direction);
							
							if (res != null) {
								//direction is ok
								
								long start = ServerTime.getLocalTimeInMillis() + 100;
								
								long rotationTimestamps [] = Calculation.rotationTimeStamps(start, Calculation.WORKER_ROTATION_TIME, w.facing, w.requested_direction);
								
								//determine the movement timestamps
								long movementTimestamps [] = Calculation.movementTimeStamps(rotationTimestamps == null ? false : true, rotationTimestamps == null ? 0L : rotationTimestamps[1], start, Calculation.WORKER_MOVEMENT_TIME);
								
								w.departure = start;
								w.arrival = movementTimestamps[1];
								w.previousTaskDone = false;
								executor.addWork(w);
								Logger.log(Logger.LOG_SERVER, "SERVER : Startup : WORKER_ACTION_BEGIN : movement : departure '"+w.departure+"', arrival '" + w.arrival + "' ");
								User u = broadcaster.getUserForPlayer(w.getPlayerID());
								//broadcast string [] { id, bamulcommand, actiontype, param1, param2 ... }
								if (u != null)
									u.client.addRequest(new Request(RequestType.WORKER_ACTION_BEGIN, new Gson().toJson( new String [] { Integer.toString(w.id), workresult, "move", Integer.toString(w.requested_direction), Long.toString(w.departure), w.currentBAMULOpCode } ), 0));
							}
						} else if (w.currentBAMULOpCode != "" && w.currentBAMULOpCode != null) { 
							executor.initializeBAMULAction(w.currentBAMULOpCode, w);
						}
						
						Logger.log(Logger.LOG_SERVER, "SERVER : Startup : work cycle done for worker " + w.id);
					}
					else {
						Logger.log(Logger.LOG_SERVER, "SERVER : Startup : Worker "+w.id+" has no more work to do");
						w.running = false;
						w.idle = true;
						w.abortScript(false);
						
						User u = broadcaster.getUserForPlayer(w.getPlayerID());
						//broadcast string [] { id, bamulcommand, actiontype, param1, param2 ... }
						if (u != null)
							u.client.addRequest(new Request(RequestType.WORKER_ABORT_SCRIPT_DONE, new Gson().toJson( new String [] { Integer.toString(w.id) } ), 0));
					}
				}
			}
			
			try {
				Thread.sleep(1000);
				Startup.counter = Startup.counter >= Logger.MAX_INT_COUNTER ? 0 : Startup.counter+1;
				if (Startup.counter % 600 == 0) //log once every 10 minutes
					Logger.log(Logger.LOG_SERVER, "SERVER : Startup : STILL RUNNING");
			}
			catch (InterruptedException ie) {
				Logger.log(Logger.LOG_SERVER, "SERVER : Startup : sleep interrupted in main thread");
			}
		}
	}
}
