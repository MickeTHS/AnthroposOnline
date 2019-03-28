package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * A Plane, as display list
 * 
 * I made this class because i wasnt happy with the Face class, still arent.
 * I wanted a simple class that would print out a display list of easy
 * to read coordinates.
 * 
 * So far i think i only use this for the water "blanket"
 * @author mickeman
 *
 */
public class Plane {
	public Vector3f position;
	public float size;
	public int texture;
	
	private int callIndex;
	
	public Plane (Vector3f pos, float size, Vector2f [] uv, int texture) {
		this.position = pos;
		this.size = size;
		this.texture = texture;
		
		generate(uv);
	}
	
	private void generate (Vector2f [] uv) {
		callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
		    glBegin(GL_TRIANGLES);
		    	//glColor4f(0.2f, 0.4f, 1f, 0.5f);
		    	
		    	if (this.texture != -1) glTexCoord2f(uv[0].x, uv[0].y);    
		    	glVertex3f(-size/2, 0f, -size/2);
			    
		    	if (this.texture != -1) glTexCoord2f(uv[1].x, uv[1].y);
			    glVertex3f(size/2, 0f, -size/2);
			    
			    if (this.texture != -1) glTexCoord2f(uv[2].x, uv[2].y);
			    glVertex3f(size/2, 0f, size/2);
			    
			    if (this.texture != -1) glTexCoord2f(uv[0].x, uv[0].y);
			    glVertex3f(-size/2, 0f, -size/2);
			    
			    if (this.texture != -1) glTexCoord2f(uv[2].x, uv[2].y);
			    glVertex3f(size/2, 0f, size/2);
			    
			    if (this.texture != -1) glTexCoord2f(uv[3].x, uv[3].y);
			    glVertex3f(-size/2, 0f, size/2);
		    glEnd();
		glEndList();
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(callIndex, 1);
	}
	
	public void render() {
		glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			
			if (this.texture != -1) {
				glEnable(GL_TEXTURE_2D);
				glBindTexture(GL_TEXTURE_2D, this.texture);
			}
			
			glPushMatrix();
				glCallList(callIndex);
			glPopMatrix();
			
			if (this.texture != -1)
				glDisable(GL_TEXTURE_2D);
		glPopMatrix();
	}
}