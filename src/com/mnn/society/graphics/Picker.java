package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

/**
 * Just a simple object used with Picking
 * @author mickeman
 *
 */
public class Picker {
	public static final float size = 0.1f;
	
	public Vector3f 	position;
	private int 		callIndex;
	
	public Picker (Vector3f position) {
		this.position = position;
		generate();
	}
	
	private void generate () {
		callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
		    glBegin(GL_TRIANGLES);
		    	glColor4f(0.2f, 0.4f, 1f, 0.5f);
		    	//top
		    	glVertex3f(-Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, +Picker.size/2);
			    
			    //bottom
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, +Picker.size/2);
			    
			    //front
			    glVertex3f(-Picker.size/2, -Picker.size/2, +Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, +Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, +Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, +Picker.size/2);
			    
			    //back
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, -Picker.size/2);
			    
			    //left
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(-Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(-Picker.size/2, -Picker.size/2, +Picker.size/2);
			    
			    //right
			    glVertex3f(+Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, -Picker.size/2);
			    glVertex3f(+Picker.size/2, +Picker.size/2, +Picker.size/2);
			    glVertex3f(+Picker.size/2, -Picker.size/2, +Picker.size/2);
		    glEnd();
		glEndList();
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(callIndex, 1);
	}
	
	/* gets a bounding box translated to the position */
	public Vector3f [] getBoundingBox () {
		return new Vector3f [] { 
			new Vector3f(this.position.x - Picker.size/2, this.position.y - Picker.size/2, this.position.z - Picker.size/2),
			new Vector3f(this.position.x + Picker.size/2, this.position.y - Picker.size/2, this.position.z - Picker.size/2),
			new Vector3f(this.position.x - Picker.size/2, this.position.y - Picker.size/2, this.position.z + Picker.size/2),
			new Vector3f(this.position.x + Picker.size/2, this.position.y - Picker.size/2, this.position.z + Picker.size/2),
			
			new Vector3f(this.position.x - Picker.size/2, this.position.y + Picker.size/2, this.position.z - Picker.size/2),
			new Vector3f(this.position.x + Picker.size/2, this.position.y + Picker.size/2, this.position.z - Picker.size/2),
			new Vector3f(this.position.x - Picker.size/2, this.position.y + Picker.size/2, this.position.z + Picker.size/2),
			new Vector3f(this.position.x + Picker.size/2, this.position.y + Picker.size/2, this.position.z + Picker.size/2),
		};
	}
	
	public void render() {
		//System.out.println("CLIENT : Picker : pos " + this.position);
		
		/*glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			
			glPushMatrix();
				glCallList(callIndex);
			glPopMatrix();
		glPopMatrix();*/
	}
}
