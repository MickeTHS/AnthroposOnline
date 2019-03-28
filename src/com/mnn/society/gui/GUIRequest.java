package com.mnn.society.gui;

/**
 * When the GUI wants to change something outside of the GUI scope, it needs to request it through this class
 * @author mickeman
 *
 */
public class GUIRequest {
	public static final int GUI_UPDATE_BAMUL 	= 1;
	public static final int GUI_VALIDATE_BAMUL 	= 2;
	public static final int GUI_STOP_WORKER 	= 3;
	public static final int GUI_GENERATE_SPHERE = 4;
	
	public int 		type;
	public String	param1;
	public int		param2;
	
	public GUIRequest (int type, String param1, int param2) {
		this.type = type;
		this.param1 = param1;
		this.param2 = param2;
	}
}
