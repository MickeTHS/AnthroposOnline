package com.mnn.society.font;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import java.awt.image.DataBufferByte;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.opengl.ARBBufferObject.glGenBuffersARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import org.newdawn.slick.util.ResourceLoader;

import com.mnn.society.graphics.Face;
import com.mnn.society.graphics.Quad;
import com.mnn.society.graphics.VBOHandler;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;

/**
 * Handles a bitmap font. Pulls all the bitmaps into a Quad for each font character. When renderCharAt is called, that quad is looked up and rendered at the given position.
 * 
 * This class is not actually used, made a new one called MonoMNNFont
 * @author mickeman
 *
 */
public class Font {
		
	public char [][] char_lookup = { 	{'A','B','C','D','E','F','G','H','I','J'},
										{'K','L','M','N','O','P','Q','R','S','T'},
										{'U','V','W','X','Y','Z','a','b','c','d'},
										{'e','f','g','h','i','j','k','l','m','n'},
										{'o','p','q','r','s','t','u','v','w','x'},
										{'y','z','0','1','2','3','4','5','6','7'},
										{'8','9','+','[',']','\\','%','&','-','.'},
										{',',';',':','_','|','<','>','^','*','~'},
										{'\'','`','!','"','#','?','/','(',')','='},
										{'@','$','{','}',' '}};
								   
	private float u_w = 0.0977f;
	private float u_h = 0.08f;
	
	public static float font_width = 20f;
	public static float font_height = 30f;
	public Map<String, Quad> fontChars = new HashMap<String, Quad>();
	
	public void buildFontPlane() {
		
	}
	
	public Font() {
/*		int size = 0;
		int elemementSize = VBOModel.verticeSize+VBOModel.normalSize+VBOModel.textureSize; // size of element is vertice normal and texture
		int dataTexId = -1;
		
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(2*9*9*6);

		//v1
		vBuffer.put( 0.00f).put(-1.00f).put(-1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put( 0.00f);
		vBuffer.put( 1.00f).put( 0.00f);
		
		//v2
		vBuffer.put( 0.00f).put( 1.00f).put(-1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put( 0.00f);
		vBuffer.put( 1.00f).put( 1.00f);
		
		//v3
		vBuffer.put( 0.00f).put( 1.00f).put( 1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put( 0.00f);
		vBuffer.put( 0.00f).put( 1.00f);
		
		//f2 
		
		//v4
		vBuffer.put( 0.00f).put(-1.00f).put(-1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put( 0.00f);
		vBuffer.put( 1.00f).put( 0.00f);
		
		//v5
		vBuffer.put(-0.00f).put( 1.00f).put( 1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put(-0.00f);
		vBuffer.put( 0.00f).put( 1.00f);
		
		vBuffer.put( 0.00f).put(-1.00f).put( 1.00f);
		vBuffer.put( 1.00f).put( 0.00f).put(-0.00f);
		vBuffer.put( 0.00f).put( 0.00f);
		
	    vBuffer.flip();
	    size = 2*3; //triangles times vertices
	    IntBuffer nBuffer = BufferUtils.createIntBuffer(1);

	    glGenBuffersARB(nBuffer);
	    int vHandle = nBuffer.get(0);
	    
	    //load the texture
	    
	    System.out.println("renderVBO: texture not loaded, will load " + bitmapFile);
	    dataTexId = VBOModel.loadTexture(bitmapFile);
	    */
		
		//build all the characters
		int a,b;
		
		//int dataTexId = VBOModel.loadTexture(bitmapFile);
		//_alphaTextureEnv.setAll(GL10.GL_TEXTURE_ENV_MODE, GL10.GL_ADD);
		
		int tex = TextureLoader.loadSharedTexture("res\\hires.png");
		
		for(int c_i = 0; c_i < char_lookup.length; c_i++) {
			for(int c_j = 0; c_j < char_lookup[c_i].length; c_j++) {
				a = c_i;
				b = c_j;
				
				if(a >= 0 && b >= 0) {
					/*vBuffer.put( 0.00f).put( 0.00f).put( 0.00f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					//vBuffer.put( 1.00f).put( 0.00f);
					vBuffer.put(uv.get(0).x).put(uv.get(0).y);
					
					//v2
					vBuffer.put( 0.00f).put( 0.50f).put( 0.50f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					vBuffer.put(uv.get(1).x).put(uv.get(1).y);
					
					//v3
					vBuffer.put( 0.00f).put( 0.50f).put( 0.00f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					vBuffer.put(uv.get(2).x).put(uv.get(2).y);
					
					//v4
					vBuffer.put( 0.00f).put( 0.00f).put( 0.00f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					vBuffer.put(uv.get(3).x).put(uv.get(3).y);
					
					//v5
					vBuffer.put( 0.00f).put( 0.00f).put( 0.50f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					vBuffer.put(uv.get(4).x).put(uv.get(4).y);
					
					vBuffer.put( 0.00f).put( 0.50f).put( 0.50f);
					vBuffer.put( 0.00f).put( 0.00f).put( 1.00f);
					vBuffer.put(uv.get(5).x).put(uv.get(5).y);*/
					
			
					Face x = new Face(	new Vector3f [] { new Vector3f(0f, 0f, 0f), new Vector3f(font_width, font_height, 0f), new Vector3f(font_width, 0f, 0f)},
										new Vector3f [] { new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f)},
										new Vector3f [] { 
											new Vector3f(0.00f+(b*u_w+0.0015f), 0.00f+(a*u_h+0.0055f),0f),
											new Vector3f(u_w+(b*u_w+0.0015f), u_h+(a*u_h+0.0055f),0f),
											new Vector3f(u_w+(b*u_w+0.0015f), 0.00f+(a*u_h+0.0055f),0f)
										});
					
					Face y = new Face(	new Vector3f [] { new Vector3f(0f, 0f, 0f), new Vector3f(0f, font_height, 0f), new Vector3f(font_width, font_height, 0f)},
										new Vector3f [] { new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f)},
										new Vector3f [] {
											new Vector3f(0.00f+(b*u_w+0.0015f), 0.00f+(a*u_h+0.0055f),0f),
											new Vector3f(0.00f+(b*u_w+0.0015f), u_h+(a*u_h+0.0055f),0f),
											new Vector3f(u_w+(b*u_w+0.0015f), u_h+(a*u_h+0.0055f),0f)
					});
					
					Quad q = new Quad();
					q.a = x;
					q.b = y;
					q.dataTexId = tex;
					
					VBOHandler.setupVBO(q);
					
					this.fontChars.put(char_lookup[c_i][c_j]+"", q);
				}
			}
		}
	}
	
	public void render() {
		/*glPushMatrix(); {
			glBegin(GL_QUADS);
				glVertex4f(-1, -1, 0, 0);
				glVertex4f(-1, 1, 0, 0);
				glVertex4f(1, 1, 0, 0);
				glVertex4f(1, -1, 0, 0);
			glEnd();
		}*/
		
		//VBOHandler.renderVBO(m)
	}
	
	/* just a check to see if the character is defined in the font */
	public boolean validate (char chr) {
		try {
			Quad q = this.fontChars.get(chr+"");
			if (q.b != null) return true;
		}
		catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public void renderCharAt (char chr, Vector3f pos, float size) {
		Quad q = null;
		try {
			q = this.fontChars.get(chr+"");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : Font : unable to print char: " + chr);
			return;
		}
		
		glPushMatrix();
			glTranslatef(pos.x, pos.y, pos.z);
			glScalef(size, size, size);
			q.render();
		glPopMatrix();
	}
	
	
	/**
	 * Returns a clone(Plane with texture mapped to the character) to the master 
	 * character font, use this to add to your string on the GUI
	 * */
	public Quad getBitmapChar(char c) {
		//return this.objChars[(int)c].clone();
		return null;
	}
}