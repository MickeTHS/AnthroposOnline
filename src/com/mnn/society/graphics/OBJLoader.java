package com.mnn.society.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.server.utils.Logger;

/**
 * Aaaah yes, the OBJLoader.
 * 
 * For those who are not familiar with an OBJ, its basically a Mesh with material, animations,
 * uvs, normals and vertices defined in a file.
 * Problem is its not optimized and it doesnt even compress the file.
 * 
 * So if you got yourself a high polygon model with a few animations, the file would become
 * HUGE!
 * 
 * Thats why i use the MD3 format instead for everything.
 * 
 * This can be removed.
 * @author mickeman
 *
 */
public class OBJLoader {
    
	/**
     * Parse three floats from the given input String.  Ignore the
     * first token (the line type identifier, ie. "v", "vn", "vt").
     * Return array: float[3].
     * @param line  contains line from OBJ file
     * @return array of 3 float values
     */
	private static float[] read3Floats(String line) {
		try {
			StringTokenizer st = new StringTokenizer(line, " ");
			st.nextToken();   // throw out line marker (vn, vt, etc.)
			if (st.countTokens() == 2) { // texture uv may have only 2 values
				return new float[] {Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken()),
									0};
			}
			else {
				return new float[] {Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken())};
			}
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "OBJLoader.read3Floats(): error on line '" + line + "', " + e);
			return null;
		}
	}

	/**
	 * Read a face definition from line and return a Face object.
	 * Face line looks like: f 1/3/1 13/20/13 16/29/16
     * Three or more sets of numbers, each set contains vert/txtr/norm
     * references.  A reference is an index into the vert or txtr
     * or normal list.
	 * @param line   string from OBJ file with face definition
	 * @return       Face object
	 */
	private static Face readFace(String line, List<Vector3f> vertices, List<Vector3f> textureCoords, List<Vector3f> normals) {
        // throw out "f " at start of line, then split
        String[] triplets = line.substring(2).split(" ");
        int[] v = new int[triplets.length];
        int[] vt = new int[triplets.length];
        int[] vn = new int[triplets.length];
        for (int i = 0; i < triplets.length; i++) {
            // triplets look like  13/20/13  and hold
            // vert/txtr/norm indices.  If no texture coord has been
            // assigned, may be 13//13.  Substitute 0 so split works.
            String[] vertTxtrNorm = triplets[i].replaceAll("//", "/0/").split("/");
            if (vertTxtrNorm.length > 0) {
            	v[i] = convertIndex(vertTxtrNorm[0], vertices.size());
            }
            if (vertTxtrNorm.length > 1) {
                vt[i] = convertIndex(vertTxtrNorm[1], textureCoords.size());
            }
            if (vertTxtrNorm.length > 2) {
                vn[i] = convertIndex(vertTxtrNorm[2], normals.size());
            }
        }
        
        return new Face(v,vt,vn);
	}
	
	public static int convertIndex(String token, int numVerts) {
        int idx = Integer.valueOf(token).intValue(); // OBJ file index starts at 1
        idx = (idx < 0) ? (numVerts + idx) : idx-1;  // convert index to start at 0
        return idx;
    }
	
	public static Mesh getModelFromFile(String objFilename) {
    	//int objectDisplayList = glGenLists(1);
        Mesh m = null;
        
        try {
            m = loadModel(new File(objFilename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        
        return m;
    }
	
	private static Mesh loadModel(File f) throws FileNotFoundException, IOException {
        
    	BufferedReader reader = new BufferedReader(new FileReader(f));
        
    	List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Vector3f> uvs = new ArrayList<Vector3f>();
        List<Face> faces = new ArrayList<Face>();
    	
    	//Model m = null;
        
        String line;
        
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("v ")) {
                float [] verts = read3Floats(line);
            	vertices.add(new Vector3f(Math.round(verts[0]*1000.0f)/1000.0f,Math.round(verts[1]*1000.0f)/1000.0f,Math.round(verts[2]*1000.0f)/1000.0f));
                
            } else if (line.startsWith("vn ")) {
                float [] verts = read3Floats(line);
            	normals.add(new Vector3f(Math.round(verts[0]*1000.0f)/1000.0f,Math.round(verts[1]*1000.0f)/1000.0f,Math.round(verts[2]*1000.0f)/1000.0f));
            } 
            else if (line.startsWith("vt ")) {
            	float [] verts = read3Floats(line);
            	uvs.add(new Vector3f(Math.round(verts[0]*1000.0f)/1000.0f,Math.round(verts[1]*1000.0f)/1000.0f,Math.round(verts[2]*1000.0f)/1000.0f));
            }
            else if (line.startsWith("f ")) {
                faces.add(readFace(line, vertices, uvs, normals));
            }
        }
        reader.close();
        
        return new Mesh(vertices, uvs, normals, faces);
    }
}
