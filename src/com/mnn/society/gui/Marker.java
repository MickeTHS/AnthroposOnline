package com.mnn.society.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;
import com.mnn.society.server.time.ServerTime;

public class Marker {
	public 	Vector2f 	pos;
	private int 		callIndex;
	private boolean 	state;
	private long 		last;
	public Vector2f []	bounds;
	
	public Marker(Vector2f [] bounds) {
		this.bounds = bounds;
		this.last 	= ServerTime.getSyncedTimeInMillis();
		
		generate();
	}
	
	private void generate() {
		// create one display list
		this.callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(this.callIndex, GL_COMPILE);
		    glBegin(GL_QUADS);
		    	glVertex3f(this.bounds[0].x, this.bounds[0].y, 0f);
		    	glVertex3f(this.bounds[1].x, this.bounds[0].y, 0f);
		    	glVertex3f(this.bounds[1].x, this.bounds[1].y, 0f);
		    	glVertex3f(this.bounds[0].x, this.bounds[1].y, 0f);
		    glEnd();
		glEndList();
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(this.callIndex, 1);
	}
	
	public void render() {
		//glTranslatef(this.pos.x, this.pos.y, 0f);
		
		long now = ServerTime.getSyncedTimeInMillis();
		
		if (this.last + 500 < now) {
			this.state = !this.state;
			this.last = ServerTime.getSyncedTimeInMillis();
		}
		
		glPushMatrix();
			if (this.state)
				glColor3f(1f, 1f, 1f);
			else
				glColor3f(0f, 0f, 0f);
				
			// draw the display list
			glCallList(this.callIndex);
		glPopMatrix();
	}
}
