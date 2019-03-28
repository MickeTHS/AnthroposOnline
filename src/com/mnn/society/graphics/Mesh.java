package com.mnn.society.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

/**
 * A mesh containing alot of faces
 * 
 * Its really just a storage point of VBO objects
 * AND also when i define my own triangles, bit stupid i have to admit.
 * Should only use VBO! And then this should be renamed to VBOModel
 * @author mickeman
 *
 */
public class Mesh {
	public List<Face> faces = new ArrayList<Face>();
	
	public int tSize;
	public int elementSize;
	public int dataVboId;
	public int dataTexId = -1;
	public int vHandle;
	public FloatBuffer vBuffer;
	public IntBuffer nBuffer;
	
	public String textureFilename;
	
	public List<Triangle> triangles = new ArrayList<Triangle>();
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public boolean useTriangles = false;
	public boolean useMultitexture = false;
	
	public Mesh() {
		this.useTriangles = false;
	}
	
	public Mesh(List<Vector3f> verts, List<Vector3f> txtrs, List<Vector3f> norms, List<Face> faces) {
    	this.useTriangles = true;
		this.vertices = verts;
		
		//we need to invert the polarity of UVs (because blender thinks up is down)
		for (int i = 0; i < txtrs.size(); i++) {
			txtrs.get(i).y = 1f - txtrs.get(i).y;
		}
		
        for (int i = 0; i < faces.size(); i++) {
        	Face face = (Face) faces.get(i);
            
        	List<Vector3f> _vertices = new ArrayList<Vector3f>();
        	List<Vector3f> _uvs = new ArrayList<Vector3f>();
        	List<Vector3f> _normals = new ArrayList<Vector3f>();
        	
        	_uvs.add(txtrs.get(face.textureIDs[0]));
	        _uvs.add(txtrs.get(face.textureIDs[1]));
	        _uvs.add(txtrs.get(face.textureIDs[2]));
        	
	        _normals.add(norms.get(face.normalIDs[0]));
	        _normals.add(norms.get(face.normalIDs[1]));
	        _normals.add(norms.get(face.normalIDs[2]));
	        
	        _vertices.add(verts.get(face.vertexIDs[0]));
	        _vertices.add(verts.get(face.vertexIDs[1]));
	        _vertices.add(verts.get(face.vertexIDs[2]));
	        
        	triangles.add(new Triangle(_vertices, _uvs, _normals));
        }
        //TODO: Rebuild?? (mesh.rebuild())
    }
}