package com.mnn.society.server.events;

import com.google.gson.Gson;
import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;

public class EventCommandParser {
	private EventBroadcaster broadcaster;
	
	public EventCommandParser(EventBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}
	
	/**
	 * This will take the raw input, parse it to a Request, and then return the Event associated with the Request
	 * @param from
	 * @param rawCommand
	 * @return
	 */
	public IClientEvent getEvent(UserClient from, String rawCommand) {
		//tokenize the command
        /*
        DEPRECATED, this was the old way to interpret commands
        StringTokenizer st = new StringTokenizer(rawCommand, ">/>");
        String[] args = null;
        
        try {
        	args = new String[st.countTokens()-1];
        }
        catch(Exception e) {
        	return null;
        }
        
        
        if(args.length <= 0)
        	return null;
        
        //skip the first token (which is the command itself)
        String command = st.nextToken();
        
        int i = 0;
        while (st.hasMoreTokens()) {
            args[i++] = st.nextToken();
        }
        */
        IClientEvent event = null;
        
        Request req = new Gson().fromJson(rawCommand, Request.class);
        
        if (req.request == RequestType.LOGIN) {
        	event = new LoginEvent();
        	//get the login and password as ["username","password"]
        	event.setArgs(from, this.broadcaster, new Gson().fromJson(req.wparam, String[].class));
        }
        else if (req.request == RequestType.WORKER_BAMUL_UPDATE) {
        	event = new BAMULUpdateEvent(RequestType.WORKER_BAMUL_UPDATE);
        	event.setArgs(from, this.broadcaster, new Gson().fromJson(req.wparam, String[].class));
        }
        else if (req.request == RequestType.WORKER_ABORT_SCRIPT) {
        	event = new BAMULUpdateEvent(RequestType.WORKER_ABORT_SCRIPT);
        	event.setArgs(from, this.broadcaster, new Gson().fromJson(req.wparam, String[].class));
        }
        else if (req.request == RequestType.USER_CREATE) {
        	event = new CreateUser();
        	event.setArgs(from, this.broadcaster, new Gson().fromJson(req.wparam, String[].class));
        }
        /*else if	(command.equalsIgnoreCase(COM_CREATEUSER)) {
        	event = new CreateUserEvent();
        	event.setArgs(from, args);
        }
        else if (command.equalsIgnoreCase(COM_AUTOMATCH)) {
        	event = new AutoMatch();
        	event.setArgs(from, args);
        }
        else if (command.equalsIgnoreCase(COM_MATCHREADY)) {
        	event = new MatchStatusChange();
        	event.setArgs(from, args);
        	
        }*/
		return event;
	}
}