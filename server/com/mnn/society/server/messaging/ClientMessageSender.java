package com.mnn.society.server.messaging;
import java.io.PrintStream;

import com.mnn.society.server.connection.UserClient;
import com.mnn.society.server.startup.Startup;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.utils.ThreadLocker;

public class ClientMessageSender implements Runnable {
	private int counter = 0;
	private ThreadLocker running = new ThreadLocker();
	private UserClient client;
	private PrintStream output;
	
	public ClientMessageSender(UserClient client, PrintStream output) {
		this.client = client;
		this.output = output;
	}

	public void requestStop() {
		synchronized (running) {
			running.stop();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			synchronized(running) {
				if(running.isStopping()) {
					Logger.log(Logger.LOG_SERVER, "SERVER : ClientMessageSender : gracefully stopping ClientMessageSender");
					return;
				}
			}
			try {
				Message m = null;
				while((m = client.getNextMessage()) != null) {
					synchronized(output) { //this synchronization may be overkill
						try {
							output.print(m.toString());
							output.flush();
							Logger.log(Logger.LOG_SERVER, "SERVER : ClientMessageSender : Message: " + m.toString() + " successfully sent");
						}
						catch(Exception e) {
							Logger.log(Logger.LOG_SERVER, "SERVER : ClientMessageSender : Unable to send message : " + m.toString());
						}
					}
				}
				Thread.sleep(200);
				this.counter = this.counter >= Logger.MAX_INT_COUNTER ? 0 : this.counter+1;
				if (this.counter % 3000 == 0) //log once every 10 minutes
					Logger.log(Logger.LOG_SERVER, "SERVER : ClientMessageSender : STILL RUNNING : user_id : " + (this.client == null ? "none" : this.client.user_id));
			}
			catch(InterruptedException ignored) {}
			catch(Exception e) { Logger.log(Logger.LOG_SERVER, "SERVER : ClientMessageSender : Serious exception : " + e);}
		}
	}
}