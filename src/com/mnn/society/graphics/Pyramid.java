package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * Its a pyramid, rendered without VBO or display lists! Its a nightmare!
 * Remove this from my sight!
 * @author mickeman
 *
 */
public class Pyramid implements Collidable {
	public float size;
	
	public Pyramid(float size) {
		this.size = size;
	}

	@Override
	public boolean intersects(Collidable other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(long dt) {
		
		glColor4f(0.0f, 0.5f, 0.5f, 0.5f);
		glPushMatrix();
		
		glBegin(GL_TRIANGLES);
			
			//slice
			glNormal3f(0f, +1f, +1f);
			glVertex3f(-size/2, -size/2, -size/2);
			glVertex3f(+size/2, -size/2, -size/2);
			glVertex3f(+size/2, +size/2, +size/2);
			
			glNormal3f(0f, +1f, +1f);
			glVertex3f(+size/2, +size/2, +size/2);
			glVertex3f(-size/2, +size/2, +size/2);
			glVertex3f(-size/2, -size/2, -size/2);
			
			//back
			glNormal3f(0f, 0f, -1f);
			glVertex3f(+size/2, +size/2, -size/2);
			glVertex3f(-size/2, +size/2, -size/2);
			glVertex3f(-size/2, -size/2, -size/2);
			
			glNormal3f(0f, 0f, -1f);
			glVertex3f(-size/2, -size/2, -size/2);
			glVertex3f(+size/2, -size/2, -size/2);
			glVertex3f(+size/2, +size/2, -size/2);
			
			//bottom
			glNormal3f(0f, -1f, 0f);
			glVertex3f(-size/2, +size/2, +size/2);
			glVertex3f(+size/2, +size/2, +size/2);
			glVertex3f(+size/2, +size/2, -size/2);
			
			glNormal3f(0f, -1f, 0f);
			glVertex3f(-size/2, +size/2, -size/2);
			glVertex3f(+size/2, +size/2, -size/2);
			glVertex3f(-size/2, +size/2, +size/2);
			
		glEnd();
		glPopMatrix();
	}
}
