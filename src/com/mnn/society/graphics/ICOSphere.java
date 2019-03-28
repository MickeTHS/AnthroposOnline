package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.math.PerlinNoise;
import com.mnn.society.server.utils.Logger;

public class ICOSphere {
	private int callIndex = 0;
	private Vector3f position;
	private static float X = 0.525731f;
	private static float Z = 0.850650f;
	
	private float min = 99999f, max = -99999f;
	
	private float amplitude = 0f, frequency = 0f, persistance = 0f;
	private int octaves = 0;
	private boolean displace;
	private float scale;
	private float zoomFactor = 1f;
	
	private List<float []> allVertices = new ArrayList<float []>();
	private List<float []> unmodifiedVertices = new ArrayList<float []>();
	private float [] allTextureCoords = new float [128000];
	private int verticeIndex = 0;
	
	public static float vdata[][] = {    
		   {-X, 0.0f, Z}, 	{X, 0.0f, Z}, 	{-X, 0.0f, -Z}, {X, 0.0f, -Z},    
		   {0.0f, Z, X}, 	{0.0f, Z, -X}, 	{0.0f, -Z, X}, 	{0.0f, -Z, -X},    
		   {Z, X, 0.0f}, 	{-Z, X, 0.0f}, 	{Z, -X, 0.0f}, 	{-Z, -X, 0.0f} 
		};
	
	private static int tindices[][] = { 
		   {0,4,1}, 	{0,9,4}, 	{9,5,4}, 	{4,5,8}, 	{4,8,1},    
		   {8,10,1}, 	{8,3,10}, 	{5,3,8}, 	{5,2,3}, 	{2,7,3},    
		   {7,10,3}, 	{7,6,10}, 	{7,11,6}, 	{11,0,6}, 	{0,1,6}, 
		   {6,1,10}, 	{9,0,11}, 	{9,11,2}, 	{9,2,5}, 	{7,2,11} };
	
	public ICOSphere (Vector3f position, float scale, boolean displace, float frequency, float amplitude, int octaves, float persistance) {
		this.position 	= position;
		this.displace 	= displace;
		this.amplitude 	= amplitude;
		this.frequency 	= frequency;
		this.octaves 	= octaves;
		this.persistance = persistance;
		this.scale		= scale;
		generate(null);
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : ICOSphere : min '"+min+"' max '"+max+"'");
	}
	
	public void setZoomFactor (float zoomFactor) {
		this.zoomFactor = zoomFactor;
		this.scale = zoomFactor;
		generate(null);
	}
	
	private void drawtriangle (float [] v1, float [] v2, float [] v3, float [] uv) {
		glBegin(GL_TRIANGLES);
			glTexCoord1f(uv[0]); glNormal3f(v1[0], v1[1], v1[2]); glVertex3f(v1[0], v1[1], v1[2]);
			glTexCoord1f(uv[1]); glNormal3f(v2[0], v2[1], v2[2]); glVertex3f(v2[0], v2[1], v2[2]);
			glTexCoord1f(uv[2]); glNormal3f(v3[0], v3[1], v3[2]); glVertex3f(v3[0], v3[1], v3[2]);
		glEnd();
	}
	
	private void generatetriangle(float [] v1, float [] v2, float [] v3) { 
		float [][] verts = PerlinNoise.perlinMesh(zoomFactor, new float [] [] {v1, v2, v3}, this.frequency, this.amplitude, this.octaves, this.persistance);
		
		this.unmodifiedVertices.add(v1);
		this.unmodifiedVertices.add(v2);
		this.unmodifiedVertices.add(v3);
		
		this.allVertices.add(displace ? verts[0] : v1);
		this.allVertices.add(displace ? verts[1] : v2);
		this.allVertices.add(displace ? verts[2] : v3);
		
		if (min > verts[0][3]) min = verts[0][3];
		if (max < verts[0][3]) max = verts[0][3];
		
		this.allTextureCoords[verticeIndex++] = verts[0][3];
		this.allTextureCoords[verticeIndex++] = verts[1][3];
		this.allTextureCoords[verticeIndex++] = verts[2][3];
	}
	
	void normalize(float [] v) {    
		float d = (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]); 
		if (d == 0.0) {
			return;
		}
		
		v[0] /= d; v[1] /= d; v[2] /= d; 
	}
	
	private void subdivide(float [] v1, float [] v2, float [] v3, long depth) {
		float [] v12 = new float[3];
		float [] v23 = new float[3];
		float [] v31 = new float[3];
		
		int i;
		if (depth == 0) {
			generatetriangle(v1, v2, v3);
			return;
		}
	   
		for (i = 0; i < 3; i++) {
			v12[i] = v1[i]+v2[i];
			v23[i] = v2[i]+v3[i];
			v31[i] = v3[i]+v1[i];
		}
		
		normalize(v12);
		normalize(v23);
		normalize(v31);
		subdivide(v1, v12, v31, depth-1);
		subdivide(v2, v23, v12, depth-1);
		subdivide(v3, v31, v23, depth-1);
		subdivide(v12, v23, v31, depth-1);
	}
	
	private float [] addVectors (float [] v1, float [] v2) {
		return new float [] { (v1[0] / 10.0f) + v2[0], (v1[1] / 10.0f) + v2[1], (v1[2] / 10.0f) + v2[2] };
	}
	
    private void generate (Vector2f [] uv) {
		callIndex = glGenLists(1);
		
		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
			for (int i = 0; i < 20; i++) {    
				subdivide(vdata[tindices[i][0]], vdata[tindices[i][1]], vdata[tindices[i][2]],5); 
			}
			
			//apply mountains:
			for (int i = 0; i < this.verticeIndex; i+=3) {
				float [] v1 = this.unmodifiedVertices.get(i);
				float [] v2 = this.unmodifiedVertices.get(i+1);
				float [] v3 = this.unmodifiedVertices.get(i+2);
				
				float [][] verts = PerlinNoise.perlinMesh(zoomFactor, new float [][] { v1, v2, v3 }, 0.56f, 0.1f, 5, 1.8f);
				
				this.allTextureCoords[i] 	= verts[0][3];
				this.allTextureCoords[i+1] 	= verts[1][3];
				this.allTextureCoords[i+2] 	= verts[2][3];
				this.allVertices.set(i, addVectors(verts[0], this.allVertices.get(i)));
				this.allVertices.set(i+1, addVectors(verts[0], this.allVertices.get(i+1)));
				this.allVertices.set(i+2, addVectors(verts[0], this.allVertices.get(i+2)));
			}
			
			
			//now draw it
			for (int i = 0; i < this.verticeIndex; i+=3) {
				drawtriangle(this.allVertices.get(i), this.allVertices.get(i+1), this.allVertices.get(i+2), new float [] { this.allTextureCoords[i], this.allTextureCoords[i+1], this.allTextureCoords[i+2] });
			}
		glEndList();
	}
    
    public void setScale (float scale) {
    	this.scale = scale;
    }
    
    public void render () {
    	glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			glScalef(this.scale, this.scale, this.scale);
			glPushMatrix();
				glCallList(callIndex);
			glPopMatrix();
		glPopMatrix();
		
    }
}