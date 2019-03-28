package com.mnn.society.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IKeyboardListener;
import com.mnn.society.input.IMouseListener;

/**
 * A GUI Button.
 * Quite satisfied with this. Not much that can be improved.
 * @author mickeman
 *
 */
public class Button implements IGUIObject {
	
	public 	Vector2f []		bounds;
	private Vector2f [] 	uv;
	private Vector2f []		parentBounds;
	private int 			callIndex = 0;
	private int 			texture;
	private String			caption;
	private IMouseListener 	listener;
	private boolean			visible = true;
	private float			z;
	
	public Button(Vector2f [] bounds, String caption, int texture, Vector2f [] uv, Vector2f [] parentBounds, float z) {
		this.bounds 		= bounds;
		this.uv 			= uv;
		this.texture 		= texture;
		this.parentBounds 	= parentBounds;
		this.caption		= caption;
		this.z				= z;
		
		generate();
	}
	
	private void generate() {
		// create one display list
		callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
		    glBegin(GL_TRIANGLES);
		    	glColor3f(1f, 1f, 1f);
		    	glTexCoord2f(uv[0].x, uv[0].y);    
		    	glVertex3f(0f, 0f, this.z);
			    
		    	glTexCoord2f(uv[1].x, uv[1].y);
			    glVertex3f(this.bounds[1].x, 0f, this.z);
			    
			    glTexCoord2f(uv[2].x, uv[2].y);
			    glVertex3f(this.bounds[1].x, this.bounds[1].y, this.z);
			    
			    glTexCoord2f(uv[0].x, uv[0].y);
			    glVertex3f(0f, 0f, this.z);
			    
			    glTexCoord2f(uv[2].x, uv[2].y);
			    glVertex3f(this.bounds[1].x, this.bounds[1].y, this.z);
			    
			    glTexCoord2f(uv[3].x, uv[3].y);
			    glVertex3f(0f, this.bounds[1].y, this.z);
		    glEnd();
		glEndList();
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(callIndex, 1);
	}
	
	public void render() {
		if (!this.visible) return;
		
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, this.texture);
		
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);	
			// draw the display list
			glCallList(callIndex);
		glPopMatrix();
		
		glDisable(GL_TEXTURE_2D);
	}

	public void setVisible (boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible () {
		return this.visible;
	}
	
	@Override
	public void addMouseListener(IMouseListener listener) {
		this.listener = listener;
	}

	@Override
	public int mouseEvent(float x, float y, int button) {
		if (!this.visible) return IGUIObject.DONT_BOTHER;
		
		//not sure i like this with the parentBounds thingy
		if (this.listener != null && 
			this.parentBounds[0].x + this.bounds[0].x <= x && 
			this.parentBounds[0].y + this.bounds[0].y <= y && 
			this.parentBounds[0].x + this.bounds[0].x + this.bounds[1].x >= x && 
			this.parentBounds[0].y + this.bounds[0].y + this.bounds[1].y >= y) {
			this.listener.onClick(this);
			return IGUIObject.CLICK;
		}
		
		return IGUIObject.DONT_BOTHER;
	}

	@Override
	public void addKeyboardListener(IKeyboardListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFocus(boolean status) {
		// TODO Auto-generated method stub
	}

	@Override
	public int keyboardEvent(char[] sequence) {
		// TODO Auto-generated method stub
		return 0;
	}
}
