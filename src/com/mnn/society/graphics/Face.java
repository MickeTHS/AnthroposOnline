package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.math.TriangleIntersection;
import com.mnn.society.math.VectorMath;

/**
 * A face has 1 triangle! -- No shit, Sherlock!
 * 
 * This is a wee bit of a mess. Old class that has been reused for the mmo purpose.
 * Should refactor this to use only one constructor, thats the main problem.
 * @author mickeman
 */
public class Face implements Collidable {
    public Vector3f [] vertices;
    public Vector3f [] normals;
    public Vector3f translation;
	public Vector3f [] texCoords;
    
	public int[] vertexIDs; //used for VBO?
	public int[] textureIDs;
	public int[] normalIDs;
	
	//need 3 verts
	public Face(Vector3f [] verts, Vector3f [] normals) {
		this.vertices = verts;
		this.normals = normals;
		this.translation = new Vector3f(0f,0f,0f);
		this.texCoords = new Vector3f [] { new Vector3f(0f, 0f, 0f), new Vector3f(1f, 0f, 0f), new Vector3f(0f, 1f, 0f) };
	}
	
	public Face(Vector3f [] verts, Vector3f [] normals, Vector3f [] texCoords) {
		this.vertices = verts;
		this.normals = normals;
		this.translation = new Vector3f(0f,0f,0f);
		this.texCoords = texCoords;
	}
	
	public Face(int[] vertIDs, int[] txtrIDs, int[] normIDs) {
		vertexIDs = new int[vertIDs.length];
		textureIDs = new int[vertIDs.length];
		normalIDs = new int[vertIDs.length];
	
		if (vertIDs != null)
			System.arraycopy(vertIDs, 0, vertexIDs, 0, vertIDs.length);
		if (txtrIDs != null)
			System.arraycopy(txtrIDs, 0, textureIDs, 0, txtrIDs.length);
		if (normIDs != null)
			System.arraycopy(normIDs, 0, normalIDs, 0, normIDs.length);
	}

	@Override
	public boolean intersects(Collidable other) {
		if(other instanceof Face) {
			Face theOther = (Face)other;
			int res = -1;
			if ( (res = TriangleIntersection.tr_tri_intersect3D (
					new float [] { (float)(this.translation.x+this.vertices[0].x), (float)(this.translation.y+this.vertices[0].y), (float)(this.translation.z+this.vertices[0].z) }, 
					new float [] { (float)(this.translation.x+this.vertices[1].x), (float)(this.translation.y+this.vertices[1].y), (float)(this.translation.z+this.vertices[1].z) }, 
					new float [] { (float)(this.translation.x+this.vertices[2].x), (float)(this.translation.y+this.vertices[2].y), (float)(this.translation.z+this.vertices[2].z) },
					new float [] { (float)(theOther.translation.x+theOther.vertices[0].x), (float)(theOther.translation.y+theOther.vertices[0].y), (float)(theOther.translation.z+theOther.vertices[0].z) },
					new float [] { (float)(theOther.translation.x+theOther.vertices[1].x), (float)(theOther.translation.y+theOther.vertices[1].y), (float)(theOther.translation.z+theOther.vertices[1].z) },
					new float [] { (float)(theOther.translation.x+theOther.vertices[2].x), (float)(theOther.translation.y+theOther.vertices[2].y), (float)(theOther.translation.z+theOther.vertices[2].z) }
					)) != 0) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public void render(long delta) {
		// TODO Auto-generated method stub
		glPushMatrix();
		glBegin(GL_TRIANGLES);
			glNormal3f(this.normals[0].x, this.normals[0].y, this.normals[0].z);
			glVertex3f(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z);
			glVertex3f(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z);
			glVertex3f(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z);
		glEnd();
		glPopMatrix();
	}
	
	@Override
	public String toString() {
		return  "new float [] { "+(this.translation.x+this.vertices[0].x)+", "+(this.translation.y+this.vertices[0].y)+", "+(this.translation.z+this.vertices[0].z)+" }, \n"+ 
				"new float [] { "+(this.translation.x+this.vertices[1].x)+", "+(this.translation.y+this.vertices[1].y)+", "+(this.translation.z+this.vertices[1].z)+" }, \n"+ 
				"new float [] { "+(this.translation.x+this.vertices[2].x)+", "+(this.translation.y+this.vertices[2].y)+", "+(this.translation.z+this.vertices[2].z)+" } \n";
	}
}