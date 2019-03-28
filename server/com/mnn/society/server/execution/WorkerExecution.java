package com.mnn.society.server.execution;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mnn.society.server.events.EventBroadcaster;
import com.mnn.society.server.grid.Worker;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.startup.Startup;
import com.mnn.society.server.time.ServerTime;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Calculation;
import com.mnn.society.server.utils.Logger;

/**
 * TODO: this should probably move to another package
 * Responible for executing the work by workers. exeWorkers holds all the workers that
 * are waiting to do work, when worker.arrival has been reached, the work will be done.
 * @author mickeman
 *
 */
public class WorkerExecution implements Runnable {
	private int counter = 0;
	//this list is a list of workers that need to have their work done right now
	public List<Worker> exeWorkers = new ArrayList<Worker>();
	public EventBroadcaster broadcaster;
	public Boolean stop = false;
	
	public WorkerExecution (EventBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}
	
	/* only way to make workers actually execute their work is to add them to this queue */
	public void addWork (Worker w) {
		synchronized (this.exeWorkers) {
			this.exeWorkers.add(w);
		}
	}

	/* this thread makes sure that the workers do the work they have been told to do */
	@Override
	public void run() {
		while (true) {
			synchronized (this.stop) {
				if (this.stop) { 
					Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : asked to stop, quitting...");
					return;
				}
			}
			
			synchronized (this.exeWorkers) {
				if (this.exeWorkers.size() != 0) {
					//all workers that are done will be added to this array
					Worker [] done = new Worker[this.exeWorkers.size()];
					for (int i = 0, j = 0; i < this.exeWorkers.size(); i++, j++) {
						Worker w = this.exeWorkers.get(i);
						
						
						//////////////////////////////////////////////////////////
						/*********** WHEN THE ACTION IS COMPLETED ***************/
						//////////////////////////////////////////////////////////
						if (w.arrival <= ServerTime.getLocalTimeInMillis()) {
							Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : running worker " + w.id);
							String code = w.currentBAMULOpCode;
							w.join(); //this sets the values of the action
							done[j] = w;
							
							Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : sending new information about worker to owner : w:'" + w.id + "' p:'" + w.getPlayerID() + "'");
							
							User user = this.broadcaster.getUserForPlayer(w.getPlayerID());
							w.setWorkerData();
							
							//check if offline or not, null checks are good enough for now
							if (user != null && user.client != null) {
								user.client.addRequest(new Request(RequestType.WORKER_ACTION_COMPLETED, new Gson().toJson( new String [] { Integer.toString(w.id), code }), 0));
							}
							
							//TODO: Send to the rest of the players near it
							w.resetWorkerData();
							
							//JSON it and send it to the player controlling the Worker
							//this.broadcaster.sendTo(w.player_id, "this is a string that needs to be sent to that player"); //the player might not be online
							//this.broadcaster.sendToRadius(w.player_id, "broadcasted data to nearby players", w.x, w.y, 50);
							Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : '"+w.id+"' done and result broadcasted to users");
						}
					}
					
					//remove all the workers that are done
					for ( int i = 0; i < done.length; i++) {
						if (done[i] != null) {
							this.exeWorkers.remove(done[i]);
						}
					}
				}	
			}
		
			try {
				Thread.sleep(1000);
				this.counter = this.counter >= Logger.MAX_INT_COUNTER ? 0 : this.counter+1;
				if (this.counter % 600 == 0) //log once every 10 minutes
					Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : STILL RUNNING");
			}
			catch (InterruptedException ie) {
				//thread interrupted, kill it
				Logger.log(Logger.LOG_SERVER, "SERVER : ERROR: WorkerExecution : thread interrupted! " + ie);
				return;
			}
		}
	}
	
	/* Initializing the action, basically the start of the action */
	public void initializeBAMULAction (String opCode, Worker w) {
		long start = ServerTime.getLocalTimeInMillis() + 100;
		
		w.departure = start;
		
		switch (opCode) {
			case "A" : w.arrival = start + Calculation.WORKER_A_ANALYZE_TIME; 	break;
			case "B" : w.arrival = start + Calculation.WORKER_B_ANALYZE_TIME; 	break;
			case "D" : w.arrival = start + Calculation.WORKER_DRILL_TIME; 		break;
			case "P" : w.arrival = start + Calculation.WORKER_PICKUP_TIME; 		break;
			case "M" : w.arrival = start + Calculation.WORKER_MOVE_TIME; 		break;
			case "C" : w.arrival = start + Calculation.WORKER_CONSTRUCTION_BASE_TIME; break;
			case "R" : w.arrival = start + Calculation.WORKER_REPAIR_TIME; 		break;
			default : break;
		}
		
		w.previousTaskDone = false;
		
		Logger.log(Logger.LOG_SERVER, "SERVER : WorkerExecution : WORKER_ACTION_BEGIN : '"+opCode+"' : departure '"+w.departure+"', arrival '" + w.arrival + "' ");
		this.addWork(w);
		User u = this.broadcaster.getUserForPlayer(w.getPlayerID());
		if (u != null)
			u.client.addRequest(new Request(RequestType.WORKER_ACTION_BEGIN, new Gson().toJson( new String [] { Integer.toString(w.id), "", w.currentBAMULOpCode, Integer.toString(0), Long.toString(w.departure), Long.toString(w.arrival), Integer.toString(w.x_registry), Integer.toString(w.y_registry)} ), 0));
	}
}
