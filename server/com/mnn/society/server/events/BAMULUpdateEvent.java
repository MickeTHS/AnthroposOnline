package com.mnn.society.server.events;

import java.sql.ResultSet;

import com.google.gson.Gson;
import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.database.Database;
import com.mnn.society.server.database.Queries;
import com.mnn.society.server.grid.TheGrid;
import com.mnn.society.server.grid.Worker;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.user.User;
import com.mnn.society.server.utils.Logger;


/**
 * When BAMUL is updated on a worker
 * 
 * @author mickeman
 *
 */
public class BAMULUpdateEvent implements Runnable, IClientEvent {
	
	private String 				args[];
	private String 				outVars[];
	private UserClient 			from;
	private EventBroadcaster 	broadcaster;
	private int					type;
	
	public BAMULUpdateEvent (int type) {
		this.type = type;
	}
	
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
		Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : thread running");
		
		if (this.from == null || this.from.user_id <= 0) {
			Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : ERROR: user is null or not set");
			return;
		}
		
		Worker w = TheGrid.workerLookup.get(args[0]);
		
		if (w == null) {
			Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : ERROR: could not find worker with id '" + args[0]+"' for player '"+this.from.user_id+"'");
			return;
		}
		
		if (this.type == RequestType.WORKER_ABORT_SCRIPT) {
			Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : requesting abort");
			if (!w.idle) { //if the worker isnt idle, we need to abort
				w.abortScript(true);
				this.from.addRequest(new Request(RequestType.WORKER_ABORT_SCRIPT_OK, new Gson().toJson( new String [] { w.id+"", "Stopping current BAMUL script..." } ), w.id));
			}
			else {
				this.from.addRequest(new Request(RequestType.WORKER_ABORT_SCRIPT_DONE, new Gson().toJson( new String [] { w.id+"", "No script running for worker" } ), w.id));
			}
		}
		else if (this.type == RequestType.WORKER_BAMUL_UPDATE) {
			if (this.args.length != 2) {
				Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : ERROR: invalid amount of arguments");
				return;
			}
			
			if (w.idle) {
				w.bamul = args[1];
				int bamulResult = w.initializeWork();
				
				Logger.log(Logger.LOG_SERVER, "SERVER : BAMULUpdateEvent : BAMUL program set '"+w.bamul+"' for worker '"+w.id+"' with result '"+bamulResult+"'");
				
				if (bamulResult > 0) {
					this.from.addRequest(new Request(RequestType.WORKER_BAMUL_UPDATE_OK, new Gson().toJson( new String [] { w.id+"", "BAMUL Program accepted" } ), w.id));
				}
				else {
					this.from.addRequest(new Request(RequestType.WORKER_BAMUL_INVALID, new Gson().toJson( new String [] { w.id+"", "Invalid BAMUL program" } ), w.id));
				}
			}
			else { //if the worker got scripts running
				this.from.addRequest(new Request(RequestType.WORKER_BUSY, new Gson().toJson( new String [] { w.id+"", "Worker has BAMUL script running" } ), w.id));
			}
		}
	}
}