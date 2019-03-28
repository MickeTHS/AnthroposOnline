package com.mnn.society.font;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.Face;
import com.mnn.society.graphics.Quad;
import com.mnn.society.graphics.VBOHandler;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;

/**
 * SINGLETON!!!!!!
 * New and improved bitmap font class. This one has an offset for each font char so it will look a bit better.
 * It also uses VBO! Thats good for performance.
 * Note: Even though i originally intended it to be Monospace (as the name suggests), its not
 * @author mickeman
 *
 */
public class MonoMNNFont {
	private static MonoMNNFont fontObject;
	
	public char [][] char_lookup = { 	{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'},
										{'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f'},
										{'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v'},
										{'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','['},
										{']','\\','&','-','.',',',';',':','_','|','<','>','^','*','~','\''},
										{'`','!','"','#','?','/','(',')','=','@','$','{','}','%',' '}};
	
	public float [] spacing = 			{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.8f, 1f, 1f, 1f, 1.2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1.2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.6f, 1f,   1f,   0.6f, 0.6f, 1f,   0.6f, 1.2f, 1f, 1f, 1f, 1f, 1f, 1f, 0.8f, 1f, 1f, 1.2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,  1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,  1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
	public static char [] char_index = 	{'A','B','C','D','E','F','G','H','I',  'J','K','L','M',  'N','O','P','Q','R','S','T','U','V','W',  'X','Y','Z','a','b','c','d','e','f',  'g',  'h',  'i',  'j',  'k',  'l',  'm',  'n','o','p','q','r','s','t',  'u','v','w',  'x','y','z','0','1','2','3','4','5','6','7','8','9','+','[',']','\\','&','-','.',',',';',':','_','|','<','>','^','*','~','\'','`','!','"','#','?','/','(',')','=','@','$','{','}','%',' '};
	public static float constspace = 14.0f;
	
	private float u_w = 0.0625f;
	private float u_h = 0.125f;
	
	private float u_w_offset = 0.000f;
	private float u_h_offset = 0.000f;
	
	public static float font_width = 16f;
	public static float font_height = 32f;
	
	public Map<String, Quad> fontChars = new HashMap<String, Quad>();
	
	/* because its a singleton, we do this */
	public static MonoMNNFont getInstance () {
		if (fontObject == null)
			fontObject = new MonoMNNFont ();
		return fontObject;
	}
	
	public float getSpacing(char chr) {
		
		for (int i = 0; i < MonoMNNFont.char_index.length; i++) {
			if (chr == MonoMNNFont.char_index[i])
				return spacing[i];
		}
		
		return 0f;
	}
	
	private MonoMNNFont() {
		//build all the characters
		int a,b;
		
		int tex = TextureLoader.loadSharedTexture("res\\small_font.png");
		
		for(int c_i = 0; c_i < char_lookup.length; c_i++) {
			for(int c_j = 0; c_j < char_lookup[c_i].length; c_j++) {
				a = c_i;
				b = c_j;
				
				float w = getSpacing(char_lookup[c_i][c_j]);
				
				w = w < 1f ? w : 1f;
				
				if(a >= 0 && b >= 0) {
					Face x = new Face(	new Vector3f [] { new Vector3f(0f, 0f, 0f), new Vector3f(w*font_width, font_height, 0f), new Vector3f(w*font_width, 0f, 0f)},
										new Vector3f [] { new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f)},
										new Vector3f [] { 
											new Vector3f(0.00f+(b*u_w), 0.00f+(a*u_h+u_h_offset),0f),
											new Vector3f(((u_w*w)+(b*u_w)), u_h+(a*u_h+u_h_offset),0f),
											new Vector3f(((u_w*w)+(b*u_w)), 0.00f+(a*u_h+u_h_offset),0f)
										});
					
					Face y = new Face(	new Vector3f [] { new Vector3f(0f, 0f, 0f), new Vector3f(0f, font_height, 0f), new Vector3f(w*font_width, font_height, 0f)},
										new Vector3f [] { new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f), new Vector3f(0f,0f,1f)},
										new Vector3f [] {
											new Vector3f(0.00f+(b*u_w), 0.00f+(a*u_h+u_h_offset),0f),
											new Vector3f(0.00f+(b*u_w), u_h+(a*u_h+u_h_offset),0f),
											new Vector3f(((u_w*w)+(b*u_w)), u_h+(a*u_h+u_h_offset),0f)
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
	
	/* call this function to actually print a character */
	public void renderCharAt(char chr, Vector3f pos, float size) {
		Quad q = null;
		try {
			q = this.fontChars.get(chr+"");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : MonoMNNFont : unable to print char: " + chr);
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