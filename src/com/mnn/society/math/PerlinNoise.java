package com.mnn.society.math;

import com.mnn.society.server.utils.Logger;

public class PerlinNoise {
	private static final int p[] = new int[512], permutation[] = { 
		10,20,127,251,160,80,1,171,235,250,34,68,105,90,224,118,
		162,174,120,114,247,72,193,42,138,62,7,187,49,61,69,244,
		121,218,226,186,239,215,39,172,234,46,67,143,81,59,97,54,
		237,102,6,110,73,58,25,173,219,240,40,2,148,106,109,152,
		203,222,221,233,112,22,229,86,242,236,175,98,255,24,113,88,
		60,111,248,227,13,100,12,177,252,75,164,154,35,78,87,82,
		130,158,108,169,199,182,47,51,168,198,57,15,225,91,89,153,
		63,253,27,217,213,181,185,65,246,48,3,220,125,155,179,41,
		116,188,245,64,189,241,16,184,96,18,183,147,132,53,14,101,
		149,45,76,197,195,145,83,144,230,77,85,249,139,122,207,231,
		194,79,70,21,170,141,115,209,190,32,126,104,214,92,176,117,
		238,232,151,5,36,191,9,23,180,19,200,0,211,167,204,192,
		208,212,142,124,165,30,17,163,66,146,56,4,205,103,50,135,
		71,202,33,156,8,31,28,223,107,94,128,137,201,178,26,206,
		140,95,136,134,157,243,29,74,44,38,228,150,84,129,11,52,
		216,37,196,161,210,254,99,131,93,43,166,133,123,55,159,119
	};
	
	static { for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i]; }
		
	static public double noise(double x, double y, double z) {
		int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
	        Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
	        Z = (int)Math.floor(z) & 255;
	    x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
	    y -= Math.floor(y);                                // OF POINT IN CUBE.
	    z -= Math.floor(z);
	    double u = fade(x),                                // COMPUTE FADE CURVES
	           v = fade(y),                                // FOR EACH OF X,Y,Z.
	           w = fade(z);
	    int A = p[X  ]+Y, AA = p[A]+Z, AB = p[A+1]+Z,      // HASH COORDINATES OF
	        B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,

	    return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ADD
	                                   grad(p[BA  ], x-1, y  , z   )), // BLENDED
	                           lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
	                                   grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
	                   lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
	                                   grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
	                           lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
	                                   grad(p[BB+1], x-1, y-1, z-1 ))));
	}
	
	private static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
	private static double lerp(double t, double a, double b) { return a + t * (b - a); }
	private static double grad(int hash, double x, double y, double z) {
		int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
	    double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
	           v = h<4 ? y : h==12||h==14 ? x : z;
	    return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
	}
	
	public static float max = -9999f, min = 9999f;
	
	public static float [][] perlinMesh (float zoomfactor, float [][] model, float frequency, float amplitude, int octaves, float persistance) {
    	double h = 0f; 
    	double m = 0f;
    	double m2 = 0f;
    	
    	float [][] mesh = new float[model.length][4];
    	
    	int i = 0;
    	
    	for (float vert [] : model) {
    		h = 0f;
    		double amp = amplitude; 
    		double freq = frequency;
    		
    		for(int a = 0; a < octaves-1; a++) {
    			h += noise((double)vert[0]*freq, (double)vert[1]*freq, (double)vert[2]*freq)*amp;
    	        amp *= persistance;
    	        freq *= 2.0f;
    	        
    	        if (h < 0) {
    	        	h = (-h)+0.1;
    	        }
    	        else {
    	        	h = h+0.1;
    	        }
    	        
    	        h = Math.abs(h);
    	    }
    		
    		m = Math.sqrt(vert[0]*vert[0]+vert[1]*vert[1]+vert[2]*vert[2]);
    		m2 = m + (((2.5f*(h*h)-0.75f*h+0.3f))*zoomfactor);
    		
    		mesh[i][3] = (float)(2.5f*(h*h)-0.75f*h+0.3f);
    		mesh[i][0] = (float)((vert[0] / m)*m2);
    		mesh[i][1] = (float)((vert[1] / m)*m2);
    		mesh[i][2] = (float)((vert[2] / m)*m2);
    		//h = h - 0.105f;
    		//mesh[i][3] = (float)((7.8f*(h*h))-(4.68f*h)+1.002f); //this should make 0.6 = 1.0
    		//mesh[i][3] = (float)h;
    		
    		
    		//Logger.log(Logger.LOG_CLIENT, "h: " + mesh[i][3]);
    		
    		if (max < h) max = (float)h;
    		if (min > h) min = (float)h;
    		
    		i++;
    	}
    	
    	return mesh;
    }
}