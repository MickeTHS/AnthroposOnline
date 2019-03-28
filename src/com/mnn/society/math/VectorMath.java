package com.mnn.society.math;

import org.lwjgl.util.vector.Vector3f;

public class VectorMath {
	/**
	 * Compute the cross product (a vector) of two vectors.
	 * 
	 * @param v0, v1        Vectors to compute cross product between [x,y,z].
	 * @param crossProduct  Cross product of specified vectors [x,y,z].
	 */
	public static Vector3f cross(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0.y * v1.z - v0.z * v1.y, 
				  			v0.z * v1.x - v0.x * v1.z,
		  					v0.x * v1.y - v0.y * v1.x);
	}
	  
	/**
	 * Compute the dot product (a scalar) between two vectors.
	 * 
	 * @param v0, v1  Vectors to compute dot product between [x,y,z].
	 * @return        Dot product of given vectors.
	 */
	public static float dot(Vector3f v0, Vector3f v1) {
		return (v0.x * v1.x) + (v0.y * v1.y) + (v0.z * v1.z);
	}
	
	public static Vector3f sub(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0.x-v1.x, v0.y-v1.y, v0.z-v1.z);
	}
	
	public static Vector3f add(Vector3f v0, Vector3f v1) {
		return new Vector3f(v0.x+v1.x, v0.y+v1.y, v0.z+v1.z);
	}
	
	public static boolean pointInTriangle(Vector3f P, Vector3f D, Vector3f [] V) {
		for(int j = 2, i = 0; i < 3; j = i, i++) {
			Vector3f E = new Vector3f(V[i].x - V[j].x, V[i].y - V[j].y, V[i].z - V[j].z); // edge direction
			Vector3f H = new Vector3f(P.x - V[j].x, P.y - V[j].y, P.z - V[j].z); // edge start towards point
			Vector3f F = cross(E, D); // cross product. Normal of edge's half plane
			  
			float  d = dot(H, F); // dot   product. sign of point from edge's half plane

			// since D = -N from the Point in triangle function, 
			// we need to test for POSITIVE half planes.
			// point on positive half-plane (outside)
			if (d > 0.0f) {
				return false;
			}
		}
		return true;
	}
	
	public static Vector3f calculateNormal(Vector3f [] triangle) {
		Vector3f out = new Vector3f();
		
		Vector3f [] vectors = new Vector3f[2];
		
		vectors[0] = new Vector3f(triangle[1].x-triangle[0].x, triangle[1].y-triangle[0].y, triangle[1].z-triangle[0].z);
		vectors[1] = new Vector3f(triangle[2].x-triangle[0].x, triangle[2].y-triangle[0].y, triangle[2].z-triangle[0].z);
		
		out.x = (vectors[0].y * vectors[1].z) - (vectors[0].z * vectors[1].y);
		out.y = -((vectors[1].z * vectors[0].x) - (vectors[1].x * vectors[0].z));
		out.z = (vectors[0].x * vectors[1].y) - (vectors[0].y * vectors[1].x);
		
		float magnitude = (float)Math.sqrt((out.x * out.x) + (out.y * out.y) + (out.z * out.z));
		out.x /= -magnitude;
		out.y /= -magnitude;
		out.z /= -magnitude;
		
		return out;
	}
	
	public static String getVectorArrayInfo (Vector3f [] vec) {
		String ret = "";
		
		for (int i = 0; i < vec.length; i++) {
			ret += "[" + vec[i].x + ", " + vec[i].y + ", " + vec[i].z + "],";  
		}
		
		return ret;
	}
}