package com.mnn.society.server.utils;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * I should replace all System.out.println() calls to use this instead
 * @author mickeman
 *
 */
public class Logger {
	public static ThreadLocker lock = new ThreadLocker();
	public static final int LOG_SERVER = 0, LOG_CLIENT = 1;
	public static final int MAX_INT_COUNTER = Integer.MAX_VALUE-1;
	
	public static void log (int target, String str) {
		try {
			synchronized (Logger.lock) {
				FileWriter fstream = new FileWriter((target == Logger.LOG_CLIENT ? "client_log" : "server_log" ) + ".txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				
				Calendar calendar = new GregorianCalendar();
	
				int year       = calendar.get(Calendar.YEAR);
				int month      = calendar.get(Calendar.MONTH); 
				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // Jan = 0, not 1
				
				int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY); // 24 hour clock
				int minute     = calendar.get(Calendar.MINUTE);
				int second     = calendar.get(Calendar.SECOND);
				
				out.write(year + "-" + month + "-" + dayOfMonth + " " + hourOfDay + ":" + minute + ":" + second + " : " + str + "\n");
				
				out.close();
			}
		} catch (Exception e) { 
		}
	}
}
