package com.mnn.society.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import com.mnn.society.graphics.textures.Texture;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Vertex Buffer Object handler
 * Speediest way to render meshes! Use this method as much as humanly possible.
 * (display lists work as well if using static meshes)
 * @author mickeman
 *
 */
public class VBOHandler {
	public static int intSize = Integer.SIZE/8;
	public static int floatSize = Float.SIZE/8;
	public static int doubleSize = Double.SIZE/8;
	
	public static int verticeSize = 3*floatSize;
	public static int normalSize = 3*floatSize;
	public static int colorSize = 4*floatSize;
	public static int textureSize = 3*floatSize;
	public static int verticeAndNormalSize = verticeSize+normalSize;	
	
	/* bleh! refactored, might still need it though! darnit! */
	public static int loadTexture(String pathname) {
    	/*BufferedImage img = null;
    	try {
    		img = ImageIO.read(new File(pathname));
    	} catch(IOException e) {
    		throw new RuntimeException(e);
    	}
    	
    	int bitdepth = img.getColorModel().getPixelSize();
    	
    	//depending on the bitsize, we will get an exception here
    	byte[] src = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
    	byte temp;
    	for(int i=0; i<src.length; i+=bitdepth/8) {
    		temp = src[i];
    		src[i] = src[i+(bitdepth/8)-1];
    		src[i+(bitdepth/8)-1] = temp;
    	}
    	ByteBuffer pixels = (ByteBuffer)BufferUtils.createByteBuffer(src.length).put(src, 0, src.length).flip();
    	IntBuffer textures = BufferUtils.createIntBuffer(1);
    	glGenTextures(textures);
    	glBindTexture(GL_TEXTURE_2D, textures.get(0));
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	System.out.println("texture: " + pathname + " width: " + img.getWidth() + " " + img.getHeight());
    	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
    	glBindTexture(GL_TEXTURE_2D, 0);
    	*/
    	TextureLoader loader = new TextureLoader();
    	try {
    		Texture tex = loader.getTexture(pathname, GL_TEXTURE_2D, GL_RGBA, GL_NEAREST, GL_NEAREST);
    		return tex.textureID;
    	}
    	catch (Exception e) {
    		Logger.log(Logger.LOG_CLIENT, "CLIENT : VBOHandler : unable to load texture: " + pathname);
    	}
    	
    	//return textures.get(0);
    	return -1;
    }
	
	/*this is called when VBO should render */
	public static void renderVBO(Mesh m, int id) {
		glEnable(GL_TEXTURE_2D);
	    glEnable(GL_DEPTH_TEST);
        
	    glBindTexture(GL_TEXTURE_2D, m.dataTexId);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glEnableClientState(GL_NORMAL_ARRAY);
	    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	    
	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, m.vHandle);
	    glBufferDataARB(GL_ARRAY_BUFFER_ARB, m.vBuffer, GL_STATIC_DRAW_ARB);
	    
	    glVertexPointer(3, GL_FLOAT, m.elementSize, 0L);
	    glNormalPointer(GL_FLOAT, m.elementSize, verticeSize);
	    glTexCoordPointer(3, GL_FLOAT, m.elementSize, verticeAndNormalSize);
	    glDrawArrays(GL_TRIANGLES, 0, m.tSize);

	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);

	    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	    glDisableClientState(GL_NORMAL_ARRAY);
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
	    glDisable(GL_TEXTURE_2D);
	}
	
	/*this is called when VBO should render */
	public static void renderVBO(Quad m) {
		glEnable(GL_TEXTURE_2D);
	    glEnable(GL_DEPTH_TEST);
        
	    glBindTexture(GL_TEXTURE_2D, m.dataTexId);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glEnableClientState(GL_NORMAL_ARRAY);
	    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	    
	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, m.vHandle);
	    glBufferDataARB(GL_ARRAY_BUFFER_ARB, m.vBuffer, GL_STATIC_DRAW_ARB);
	    
	    glVertexPointer(3, GL_FLOAT, m.elementSize, 0L);
	    glNormalPointer(GL_FLOAT, m.elementSize, verticeSize);
	    glTexCoordPointer(3, GL_FLOAT, m.elementSize, verticeAndNormalSize);
	    glDrawArrays(GL_TRIANGLES, 0, m.tSize);

	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);

	    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	    glDisableClientState(GL_NORMAL_ARRAY);
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
	    glDisable(GL_TEXTURE_2D);
	}
	
	public static void setupVBO(Quad q) {
		q.elementSize = verticeSize+normalSize+textureSize;
		
		q.vBuffer = BufferUtils.createFloatBuffer(2*9*9*9);
		
		q.vBuffer.put( q.a.vertices[0].x	).put( q.a.vertices[0].y	).put( q.a.vertices[0].z	);
    	q.vBuffer.put( q.a.normals[0].x		).put( q.a.normals[0].y		).put( q.a.normals[0].z		);
    	q.vBuffer.put( q.a.texCoords[0].x	).put( q.a.texCoords[0].y	).put( q.a.texCoords[0].z 	);
    	
    	q.vBuffer.put( q.a.vertices[1].x	).put( q.a.vertices[1].y	).put( q.a.vertices[1].z	);
    	q.vBuffer.put( q.a.normals[1].x		).put( q.a.normals[1].y		).put( q.a.normals[1].z		);
    	q.vBuffer.put( q.a.texCoords[1].x	).put( q.a.texCoords[1].y	).put( q.a.texCoords[1].z 	);
    	
    	q.vBuffer.put( q.a.vertices[2].x	).put( q.a.vertices[2].y	).put( q.a.vertices[2].z	);
    	q.vBuffer.put( q.a.normals[2].x		).put( q.a.normals[2].y		).put( q.a.normals[2].z		);
    	q.vBuffer.put( q.a.texCoords[2].x	).put( q.a.texCoords[2].y	).put( q.a.texCoords[2].z 	);
    	
    	q.vBuffer.put( q.b.vertices[0].x	).put( q.b.vertices[0].y	).put( q.b.vertices[0].z	);
    	q.vBuffer.put( q.b.normals[0].x		).put( q.b.normals[0].y		).put( q.b.normals[0].z		);
    	q.vBuffer.put( q.b.texCoords[0].x	).put( q.b.texCoords[0].y	).put( q.b.texCoords[0].z 	);
    	
    	q.vBuffer.put( q.b.vertices[1].x	).put( q.b.vertices[1].y	).put( q.b.vertices[1].z	);
    	q.vBuffer.put( q.b.normals[1].x		).put( q.b.normals[1].y		).put( q.b.normals[1].z		);
    	q.vBuffer.put( q.b.texCoords[1].x	).put( q.b.texCoords[1].y	).put( q.b.texCoords[1].z 	);
    	
    	q.vBuffer.put( q.b.vertices[2].x	).put( q.b.vertices[2].y	).put( q.b.vertices[2].z	);
    	q.vBuffer.put( q.b.normals[2].x		).put( q.b.normals[2].y		).put( q.b.normals[2].z		);
    	q.vBuffer.put( q.b.texCoords[2].x	).put( q.b.texCoords[2].y	).put( q.b.texCoords[2].z 	);
    	
    	q.tSize = 6;
    	
    	q.vBuffer.flip();
	    
	    q.nBuffer = BufferUtils.createIntBuffer(1);

	    glGenBuffersARB(q.nBuffer);
	    q.vHandle = q.nBuffer.get(0);
	}
	
	/* WORKING VERTEX, NORMAL, TEXTURE VBO */
	public static void setupVBO(Mesh m) {
		m.elementSize = verticeSize+normalSize+textureSize; // size of element is vertice normal and texture
		
	    if(m.useTriangles) {
	    	m.vBuffer = BufferUtils.createFloatBuffer(m.triangles.size()*9*9*9);
	    	
	    	for(Triangle t : m.triangles) {
		    	m.vBuffer.put(t.vertices.get(0).x).put(t.vertices.get(0).y).put(t.vertices.get(0).z);
		    	m.vBuffer.put(t.normals.get(0).x).put(t.normals.get(0).y).put(t.normals.get(0).z);
		    	m.vBuffer.put(t.textureCoordinates.get(0).x).put(t.textureCoordinates.get(0).y).put(t.textureCoordinates.get(0).z);
		    	
		    	m.vBuffer.put(t.vertices.get(1).x).put(t.vertices.get(1).y).put(t.vertices.get(1).z);
		    	m.vBuffer.put(t.normals.get(1).x).put(t.normals.get(1).y).put(t.normals.get(1).z);
		    	m.vBuffer.put(t.textureCoordinates.get(1).x).put(t.textureCoordinates.get(1).y).put(t.textureCoordinates.get(1).z);
		    	
		    	m.vBuffer.put(t.vertices.get(2).x).put(t.vertices.get(2).y).put(t.vertices.get(2).z);
		    	m.vBuffer.put(t.normals.get(2).x).put(t.normals.get(2).y).put(t.normals.get(2).z);
		    	m.vBuffer.put(t.textureCoordinates.get(2).x).put(t.textureCoordinates.get(2).y).put(t.textureCoordinates.get(2).z);
		    }
	    	m.tSize = m.triangles.size()*3;
	    }
	    else {
	    	m.vBuffer = BufferUtils.createFloatBuffer(m.faces.size()*9*9*9);
	    	
	    	for(Face f : m.faces) {
		    	m.vBuffer.put( f.vertices[0].x	).put( f.vertices[0].y	).put( f.vertices[0].z	);
		    	m.vBuffer.put( f.normals[0].x	).put( f.normals[0].y	).put( f.normals[0].z	);
		    	m.vBuffer.put( f.texCoords[0].x	).put( f.texCoords[0].y	).put( f.texCoords[0].z );
		    	
		    	m.vBuffer.put( f.vertices[1].x	).put( f.vertices[1].y	).put( f.vertices[1].z	);
		    	m.vBuffer.put( f.normals[1].x	).put( f.normals[1].y	).put( f.normals[1].z	);
		    	m.vBuffer.put( f.texCoords[1].x	).put( f.texCoords[1].y	).put( f.texCoords[1].z );
		    	
		    	m.vBuffer.put( f.vertices[2].x	).put( f.vertices[2].y	).put( f.vertices[2].z	);
		    	m.vBuffer.put( f.normals[2].x	).put( f.normals[2].y	).put( f.normals[2].z	);
		    	m.vBuffer.put( f.texCoords[2].x	).put( f.texCoords[2].y	).put( f.texCoords[2].z );
		    }
	    	m.tSize = m.faces.size()*3;
	    }

	    m.vBuffer.flip();
	    
	    m.nBuffer = BufferUtils.createIntBuffer(1);

	    glGenBuffersARB(m.nBuffer);
	    m.vHandle = m.nBuffer.get(0);
	    
	    //load the texture
	    /*if(m.dataTexId == -1) {
	    	System.out.println("renderVBO: texture not loaded, will load " + m.textureFilename);
	    	m.dataTexId = loadTexture(m.textureFilename);
	    }*/
	}
	
	public static void cleanup(Quad m) {
		// cleanup VBO handles
	    m.nBuffer.put(0, m.vHandle);
	    glDeleteBuffersARB(m.nBuffer);
	}
	
	public static void cleanup(Mesh m) {
		// cleanup VBO handles
	    m.nBuffer.put(0, m.vHandle);
	    glDeleteBuffersARB(m.nBuffer);
	}
}