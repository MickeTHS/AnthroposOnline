package com.mnn.society.world;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.HeightMap;
import com.mnn.society.math.PerlinNoise;

/**
 * Functions to lookup locations and coordinates within the grid
 * @author mickeman
 *
 */
public class TheGrid {
	public static final int GRID_NORTH = 0, GRID_EAST = 1, GRID_SOUTH = 2, GRID_WEST = 3, GRID_NO_DIR = -1;
	
	public TheGrid () {
		
	}
	
	/* determine the average height of the grid square */
	/*public static float heightInGrid (int x, int y) {
		float f1 	= PerlinNoise.getXYValue(x,  y, PerlinNoise.FREQUENCY, PerlinNoise.AMPLITUDE, PerlinNoise.PERSISTANCE, PerlinNoise.OCTAVES);
		float f2 	= PerlinNoise.getXYValue(x,  y+1, PerlinNoise.FREQUENCY, PerlinNoise.AMPLITUDE, PerlinNoise.PERSISTANCE, PerlinNoise.OCTAVES);
		float f3 	= PerlinNoise.getXYValue(x+1,y+1, PerlinNoise.FREQUENCY, PerlinNoise.AMPLITUDE, PerlinNoise.PERSISTANCE, PerlinNoise.OCTAVES);
		float f4	= PerlinNoise.getXYValue(x+1,y, PerlinNoise.FREQUENCY, PerlinNoise.AMPLITUDE, PerlinNoise.PERSISTANCE, PerlinNoise.OCTAVES);
		
		return (f1 + f2 + f3 + f4) / 4f;
	}
	*/
	
	/* convert the 3D location to grid coordinates */
	public static int [] vectorsToCoordinates (Vector2f loc) {
		return null;
	}
	
	/* convert grid coordinates to 3D vector, TODO: Use the spherical calculations */
	public static Vector3f coordinatesToVector (int [] coords) {
		return new Vector3f(coords[0]*HeightMap.segmentSize, 0f, coords[1]*HeightMap.segmentSize);
	}
}
