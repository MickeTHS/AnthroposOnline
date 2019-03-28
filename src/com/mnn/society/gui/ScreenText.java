package com.mnn.society.gui;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.font.MonoMNNFont;
import com.mnn.society.graphics.Shader;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;


public class ScreenText {
	private static MonoMNNFont 	font;
	private String 				text;
	private float 				size;
	private Vector4f 			color;
	private Shader 				colorShader;
	private int					textureId;
	public Vector2f [] 			bounds;
	public float				width;
	private float				z;
	
	public ScreenText(String text, float size, Vector4f color, Vector2f [] bounds, float z) {
		this.text 		= text;
		this.color 		= color;
		this.size 		= size;
		this.bounds 	= bounds;
		this.width 		= 0f;
		this.z			= z;
		
		ScreenText.font = MonoMNNFont.getInstance();
		
		for (int i = 0; i < this.text.length(); i++) {
			if (!ScreenText.font.fontChars.containsKey(this.text.charAt(i)+""))
				this.text = this.text.replace(this.text.charAt(i), '?');
		}
		
		try {
			this.colorShader = new Shader("fontcolor", "fontcolor");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ScreenText : unable to create color shader: " + e);
		}
		
		for (int i = 0; i < this.text.length(); i++) {
			this.width += this.size * ScreenText.font.getSpacing(this.text.charAt(i)) * MonoMNNFont.constspace;
		}
	}
	
	public boolean empty () {
		return text == null || text.length() == 0;
	}
	
	public int size () {
		return text.length();
	}
	
	public String getText () {
		return this.text;
	}
	
	public void render() {
		if (this.colorShader != null) { 
			this.colorShader.preRender();
			
			int my_color = ARBShaderObjects.glGetUniformLocationARB(this.colorShader.programId, "my_color");
			if (my_color < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : ScreenText : my_color failed"); }
			ARBShaderObjects.glUniform4fARB(my_color, this.color.x, this.color.y, this.color.z, this.color.w);

			int sampler01 = ARBShaderObjects.glGetUniformLocationARB(this.colorShader.programId, "my_color_texture");
			if(sampler01 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : ScreenText : sampler01 failed"); }
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
			ARBShaderObjects.glUniform1iARB(sampler01, 0);
		}

		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, z);
			
			float x = this.bounds[0].x;
			
			for (int i = 0; i < this.text.length(); i++) {
				//x+=this.size*ScreenText.font.getSpacing( i +1 < this.text.length() ? this.text.charAt(i+1) : 'a')*MonoMNNFont.constspace;
				x+=this.size* (i == 0 ? 0f : ScreenText.font.getSpacing( this.text.charAt(i-1)))*MonoMNNFont.constspace;
				
				ScreenText.font.renderCharAt(this.text.charAt(i), new Vector3f(x, 0f, 0f), this.size);
			}
		
		glPopMatrix();
		
		Shader.clearAllShaders();
	}
	
	public void setText (String text) {
		this.text = text;
		this.width = 0f;
		
		for (int i = 0; i < this.text.length(); i++) {
			this.width += this.size * ScreenText.font.getSpacing(this.text.charAt(i)) * MonoMNNFont.constspace;
		}
	}
	
	public void addText(String text) {
		if (!ScreenText.font.validate(text.charAt(0))) return;
		Logger.log(Logger.LOG_CLIENT, "CLIENT : ScreenText : char added : " + text);
		this.text += text;
		this.width += this.size * ScreenText.font.getSpacing(text.charAt(0)) * MonoMNNFont.constspace;
	}
	
	public boolean removeLastChar() {
		if (this.text.length() > 0) {
			this.width -= this.size * ScreenText.font.getSpacing(text.charAt(this.text.length()-1)) * MonoMNNFont.constspace;
			this.text = this.text.substring(0, this.text.length()-1);
			return true;
		}
		return false;
	}
	
	public void clear () {
		this.text = "";
		this.width = 0f;
	}
	
	public void setmarker(boolean setmarker) {}
}
