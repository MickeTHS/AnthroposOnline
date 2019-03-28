package com.mnn.society.server.grid;

public class WorkerData {
	public int		id;
	public String	bamul;
	public int		player_id;
	public int		x;
	public int		y;
	public int		facing;
	
	public WorkerData (int id, String bamul, int player_id, int x, int y, int facing) {
		this.id 		= id;
		this.bamul 		= bamul;
		this.player_id 	= player_id;
		this.x 			= x;
		this.y 			= y;
		this.facing 	= facing;
	}
}
