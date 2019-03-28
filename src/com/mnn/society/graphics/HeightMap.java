package com.mnn.society.graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;

/**
 * The HeightMap (duh)
 * This class consists of squares defined in size as segmentSize.
 * Amount of segments is defined on creation. Probably gonna stick around 32 in the end.
 * TODO: Been trying to get the light shader to look awesome, not going to well.
 * TODO: Refactor!!!!
 * @author mickeman
 *
 */
public class HeightMap {
	
	public float maxheight = 0f, minheight = 0f;
	public static float segmentSize = 0.80f;
	public static float space = 1.000f;
	public static float textureSize = 0.0625f;
	public static float textureFix = 0.001f;
	public Mesh m;
	public Vector2f pos; //just to keep track of where in the world the object is
	
	public int numSegements = 0;
    
    public static int grassTexture1 = 0;
    public static int grassTexture2 = 0;
    public static int sandTexture1 = 0;
    public static int mountainTexture1 = 0;
    
    public Shader textureMaps;
    public Shader lightShader;
    
    public HeightMap (int segments, short [] heights, float x, float y) {
    	try {
    		this.textureMaps = new Shader("screen","screen");
    	}
    	catch (Exception e) {
    		Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : unable to create shader for heightmap: " + e);
    	}
    	
    	this.numSegements = segments;
    	this.pos = new Vector2f(x, y);
    	m = new Mesh();
    	m.useMultitexture = false;
    	m.faces = new ArrayList<Face>();
    	m.textureFilename = "res\\textures2.bmp";
    	m.dataTexId = TextureLoader.loadSharedTexture("res\\grass_1.png");
    	
    	if (grassTexture1 == 0) {
			grassTexture1 = TextureLoader.loadSharedTexture("res\\grass_1.png");
		}

		if (grassTexture2 == 0) {
			grassTexture2 = TextureLoader.loadSharedTexture("res\\grass_2.png");
		}

		if (sandTexture1 == 0) {
			sandTexture1 = TextureLoader.loadSharedTexture("res\\sand.png");
		}

		if (mountainTexture1 == 0) {
			mountainTexture1 = TextureLoader.loadSharedTexture("res\\rock.png");
		}
		
		for (int i = 0; i < segments-1; i++) {
			for (int j = 0; j < segments-1; j++) {
				if (to3dheight((float)heights[segments*i+j+1]) < minheight || minheight == 0f) minheight = to3dheight((float)heights[segments*i+j+1]);
				if (to3dheight((float)heights[segments*i+j+1]) > maxheight || maxheight == 0f) maxheight = to3dheight((float)heights[segments*i+j+1]);
				
				m.faces.add(new Face(	new Vector3f [] { 	new Vector3f(i*segmentSize, to3dheight((float)heights[segments*i+j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[segments*(i+1)+j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize, to3dheight((float)heights[segments*i+j]), j*segmentSize)}, 
										new Vector3f [] { 	new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f) },
															new Vector3f [] { 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[segments*i+j+1])), 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[segments*(i+1)+j+1])), 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[segments*i+j])) } ));
															
				m.faces.add(new Face(	new Vector3f [] { 	new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[segments*(i+1)+j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[segments*(i+1)+j]), j*segmentSize),
															new Vector3f(i*segmentSize, to3dheight((float)heights[segments*i+j]), j*segmentSize)}, 
										new Vector3f [] { 	new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f) },
															new Vector3f [] { 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[segments*(i+1)+j+1])), 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[segments*(i+1)+j])), 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[segments*i+j])) } ));
			}
		}
		
		VBOHandler.setupVBO(m);
    }
    
    
	public HeightMap(int segments, float [][] heights, float x, float y) {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : creating from " + x + " to " + y);
		
		
		try {
			this.textureMaps = new Shader("screen", "screen");
			//this.lightShader = new Shader("", "light");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : unable to create shader for heightmap: " + e);
		}
		
		this.numSegements = segments;
		
		this.pos = new Vector2f(x, y);
		m = new Mesh();
		m.useMultitexture = false;
		m.faces = new ArrayList<Face>();
		m.textureFilename = "res\\textures2.bmp";
		m.dataTexId = TextureLoader.loadSharedTexture("res\\grass_1.png");
		
		if (grassTexture1 == 0) {
			grassTexture1 = TextureLoader.loadSharedTexture("res\\grass_1.png");
		}

		if (grassTexture2 == 0) {
			grassTexture2 = TextureLoader.loadSharedTexture("res\\grass_2.png");
		}

		if (sandTexture1 == 0) {
			sandTexture1 = TextureLoader.loadSharedTexture("res\\sand.png");
		}

		if (mountainTexture1 == 0) {
			mountainTexture1 = TextureLoader.loadSharedTexture("res\\rock.png");
		}
		
		
		for(int i = 0; i < segments-1; i++) {
			for(int j = 0; j < segments-1; j++) {
				int h = (int)heights[i][j];
				
				//spread the texture over 4 squares
				if (to3dheight((float)heights[i][j+1]) < minheight || minheight == 0f) minheight = to3dheight((float)heights[i][j+1]);
				if (to3dheight((float)heights[i][j+1]) > maxheight || maxheight == 0f) maxheight = to3dheight((float)heights[i][j+1]);
				
				m.faces.add(new Face(	new Vector3f [] { 	new Vector3f(i*segmentSize, to3dheight((float)heights[i][j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[i+1][j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize, to3dheight((float)heights[i][j]), j*segmentSize)}, 
										new Vector3f [] { 	new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f) },
															new Vector3f [] { 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[i][j+1])), 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[i+1][j+1])), 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[i][j])) } ));
															
				m.faces.add(new Face(	new Vector3f [] { 	new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[i+1][j+1]), j*segmentSize+segmentSize),
															new Vector3f(i*segmentSize+segmentSize, to3dheight((float)heights[i+1][j]), j*segmentSize),
															new Vector3f(i*segmentSize, to3dheight((float)heights[i][j]), j*segmentSize)}, 
										new Vector3f [] { 	new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f),
															new Vector3f(0f,1f,0f) },
															new Vector3f [] { 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f, heightPercent((float)heights[i+1][j+1])), 
																	new Vector3f((i % 4) * 0.25f + 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[i+1][j])), 
																	new Vector3f((i % 4) * 0.25f, (j % 4) * 0.25f + 0.25f, heightPercent((float)heights[i][j])) } ));
			}
		}
		
		VBOHandler.setupVBO(m);
	}
	
	/* used for the 'w' value of the texture map to determine interpolation intensity */
	public static float heightPercent(float height) {
		return height/10f;
	}
	
	/* bit stupid, but it does make it easier */
	public static float to3dheight(float height) {
		return height/2f;
	}
	
	public void render(FloatBuffer mouseBuffer, float time, FloatBuffer resolution) {
		if (this.textureMaps != null) this.textureMaps.preRender();
		
		int sampler01 = ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "sampler01");
		if(sampler01 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : sampler01 failed"); System.exit(0); }
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, grassTexture1);
		ARBShaderObjects.glUniform1iARB(sampler01, 0);

		int sampler02=ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "sampler02");
		if(sampler02 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : sampler02 failed"); System.exit(0); }
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, grassTexture2);
		ARBShaderObjects.glUniform1iARB(sampler02, 1);
		
		int sampler03=ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "sampler03");
		if(sampler03 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : sampler03 failed"); System.exit(0); }
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, sandTexture1);
		ARBShaderObjects.glUniform1iARB(sampler03, 2);
		
		int sampler04=ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "sampler04");
		if(sampler04 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : HeightMap : sampler04 failed"); System.exit(0); }
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mountainTexture1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		/* THIS WORKS */
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "my_value");
		ARBShaderObjects.glUniform1fARB(my_value_loc, 0.1f);
		
		/*int mousex_loc = ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "mousex");
		ARBShaderObjects.glUniform1fARB(mousex_loc, mouseBuffer.x);
		
		int mousey_loc = ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "mousey");
		ARBShaderObjects.glUniform1fARB(mousey_loc, mouseBuffer.y);
		
		*/
		
		int mouseloc = ARBShaderObjects.glGetUniformLocationARB(this.textureMaps.programId, "mouse");
		ARBShaderObjects.glUniform2ARB(mouseloc, mouseBuffer);
		
		if (this.lightShader != null) this.lightShader.preRender();
		VBOHandler.renderVBO(m, 0);
		
		
		Shader.clearAllShaders();
	}
}