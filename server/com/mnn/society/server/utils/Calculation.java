package com.mnn.society.server.utils;

import com.mnn.society.world.TheGrid;

public class Calculation {
	public static long WORKER_ROTATION_TIME = 2000;
	public static long WORKER_MOVEMENT_TIME = 2000;
	public static long WORKER_A_ANALYZE_TIME = 5000;
	public static long WORKER_B_ANALYZE_TIME = 10000;
	public static long WORKER_DRILL_TIME = 6000;
	public static long WORKER_PICKUP_TIME = 1000;
	public static long WORKER_MOVE_TIME = 1000;
	public static long WORKER_CONSTRUCTION_BASE_TIME = 1000;
	public static long WORKER_REPAIR_TIME = 1000;
	
	public static long [] rotationTimeStamps (long startTimestamp, long rotationTime, int facing, int direction) {
		long rotationTimestamps [] = null;
		
		if (direction != facing) { //if were not facing the right direction, we need to rotate before we move
			if (   ((facing == TheGrid.GRID_SOUTH || facing == TheGrid.GRID_NORTH) && (direction == TheGrid.GRID_EAST || direction == TheGrid.GRID_WEST))
			    || ((facing == TheGrid.GRID_WEST || facing == TheGrid.GRID_EAST) && (direction == TheGrid.GRID_NORTH || direction == TheGrid.GRID_SOUTH))) {
				//we will rotate 90 degrees
				rotationTimestamps = new long [] { startTimestamp, startTimestamp + rotationTime };
			}
			else {
				//we will rotate 180 degrees
				rotationTimestamps = new long [] { startTimestamp, startTimestamp + rotationTime * 2 };
			}
		}
		
		return rotationTimestamps;
	}
	
	public static long [] movementTimeStamps (boolean rotate, long rotationEndTimestamp, long startTimestamp, long movementTime) {
		return new long [] { rotate ? rotationEndTimestamp : startTimestamp, (rotate ? rotationEndTimestamp : startTimestamp) + movementTime};
	}
}
