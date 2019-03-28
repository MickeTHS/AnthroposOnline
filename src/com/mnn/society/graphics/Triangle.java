package com.mnn.society.graphics;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

/**
 * A triangle... I'm tempted to remove this!
 * 
 * TODO: Refactor this class away!
 * @author mickeman
 *
 */
public class Triangle {
	
	public List<Vector3f> vertices;
	public List<Vector3f> textureCoordinates;
	public List<Vector3f> normals;
	
	public Triangle(List<Vector3f> verts, List<Vector3f> texCoords, List<Vector3f> norms) {
		this.vertices = verts;
		this.textureCoordinates = texCoords;
		this.normals = norms;
	}
}