package com.mnn.society.gui.windows;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.gui.Button;
import com.mnn.society.gui.ScreenText;
import com.mnn.society.server.utils.Logger;

/**
 * A simple window that pops up with some information and a close button
 * @author mickeman
 *
 */
public class MessageBox {
	public Button			closeButton;
	private Vector2f []		bounds;
	private float			width = 300f;
	private float			height = 120f;
	private String 			information;
	private int				callIndex;
	private Vector4f		color = new Vector4f(0.2f, 0.2f, 0.3f, 0.9f);
	private Vector4f		borderColor = new Vector4f(0.1f, 0.5f, 0.6f, 1f);
	private Vector4f		fontColor = new Vector4f(1f, 1f, 0f, 0f);
	private ScreenText []	texts = new ScreenText [4]; //max 4 rows of text
	
	public MessageBox (String information) {
		this.information 	= information;
		this.bounds 		= new Vector2f [] { new Vector2f(Display.getWidth()/2f - this.width/2f, Display.getHeight()/2f - this.height/2f), new Vector2f(this.width, this.height) };
		this.closeButton 	= new Button(new Vector2f [] { new Vector2f(this.bounds[1].x/2f - 52f/2f, this.bounds[1].y-30f), new Vector2f(52f, 25f) }, "mbCloseButton", TextureLoader.loadSharedTexture("res\\textures2.png"), 
			new Vector2f [] { 
				new Vector2f(0f, 0.444f), 
				new Vector2f(0.099609375f, 0.444f),
				new Vector2f(0.099609375f, 0.488f),
				new Vector2f(0f, 0.488f) }, this.bounds, -0.6f);
		Logger.log(Logger.LOG_CLIENT, "CLIENT : MessageBox : bounds : " + this.bounds[0].x + " " + this.bounds[0].y + " " + this.bounds[1].x + " " + this.bounds[1].y);
		
		int maxChars = 40;
		float fontsize = 0.5f;
		
		for (int i = 0; i <= (this.information.length()-1) / maxChars; i++) {
			this.texts[i] = new ScreenText(this.information.substring(i*maxChars, i*maxChars+maxChars > this.information.length()-1 ? this.information.length() : i*maxChars+maxChars), fontsize, fontColor, new Vector2f [] { new Vector2f(2f, 6f+(i*fontsize*30f)), new Vector2f(this.bounds[1].x, this.bounds[1].y/4) }, -0.6f);
		}
		
		generate();
	}
	
	private void generate () {
		// create one display list
		this.callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
			glBegin(GL_QUADS);
		    	glColor4f(this.color.x, this.color.y, this.color.z, this.color.w);
		    	glVertex3f(1f, 1f, -0.5f);
		    	glVertex3f(this.bounds[1].x-1, 1f, -0.5f);
		    	glVertex3f(this.bounds[1].x-1, this.bounds[1].y-1, -0.5f);
		    	glVertex3f(1f, this.bounds[1].y-1, -0.5f);
		    	glNormal3f(0f, 0f, -1f);
		    	
		    	glColor4f(this.borderColor.x, this.borderColor.y, this.borderColor.z, this.borderColor.w);
		    	glVertex3f(0f, 0f, -0.5f);
		    	glVertex3f(this.bounds[1].x, 0f, -0.5f);
		    	glVertex3f(this.bounds[1].x, this.bounds[1].y, -0.5f);
		    	glVertex3f(0f, this.bounds[1].y, -0.5f);
		    	glNormal3f(0f, 0f, -1f);
	    	glEnd();
		glEndList();
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(this.callIndex, 1);
	}
	
	public void showCloseButton (boolean visible) {
		this.closeButton.setVisible(visible);
	}
	
	public void render() {
		glPushMatrix();
		
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			glPushMatrix();
				glCallList(this.callIndex);
			glPopMatrix();
			
			//draw all the characters
			for (int i = 0; i < this.texts.length && this.texts[i] != null; i++) {
				this.texts[i].render();
			}
			
			this.closeButton.render();
		glPopMatrix();
	}
}
