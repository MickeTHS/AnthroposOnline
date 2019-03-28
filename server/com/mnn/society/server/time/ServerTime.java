package com.mnn.society.server.time;

import org.lwjgl.Sys;

/**
 * This is the server/client time syncher. When wanting to get a timestamp, we should really get it from this function.
 * @author mickeman
 *
 */
public class ServerTime {
	public static long diff;
	
	/* gets local time on the current machine */
	public static long getLocalTimeInMillis () {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/* gets the server time */
	public static long getSyncedTimeInMillis () {
		return ServerTime.getLocalTimeInMillis() - ServerTime.diff;
	}
	
	/* sets the time difference */
	public static void setClientTimeDiff (long diff) {
		ServerTime.diff = diff;
	}
}