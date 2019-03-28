package com.mnn.society.server.utils;

/**
 * This is not used in the mmorts. This was originally used in the iPhoneServer.
 * @author mickeman
 *
 */
public class ConnUtils {
	public static String fillBuffer(String strToFill) {
		String filler = strToFill;
		while(filler.length() < 128) {
			filler += " ";
		}
		
		return filler;
	}
}