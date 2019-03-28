package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.server.utils.Logger;

/**
 * Its a graphical ring with a texture on it
 * @author mickeman
 *
 */
public class Ring {
	private int callIndex;
	private float radius;
	private float height = 0.1f;
	private int segments = 16;
	public Vector3f position = new Vector3f(0f, 0f, 0f);
	private int texture;
	
	private Vector3f [] vec; //helper
	private Vector3f [] tex; //helper
	
	private float rotation = 0f;
	private float rotationSpeed = 0f;
	
	public Ring (float radius, Vector2f [] uv, int texture) {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Ring : Calculating vertices...");
		this.radius = radius;
		this.texture = texture;
		generate(uv);
	}
	
	private void generate (Vector2f [] uv) {
		callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
		    glBegin(GL_QUADS);
		    	//glColor4f(0.2f, 0.4f, 1f, 0.5f);
		    	for (int i = 0; i < this.segments; i++) {
					setTexturedRingQuad (this.segments, i, this.radius, uv);
					
					for (int j = 0; j < 4; j++) { //loop through quad set by setTexturedRingQuad
						glTexCoord2f(this.tex[j].x, this.tex[j].y);
						glVertex3f(this.vec[j].x, this.vec[j].y, this.vec[j].z);
					}					
				}
		    glEnd();
		glEndList();
	}
	
	/* helper function to make one segment of the ring */
	public void setTexturedRingQuad (int segments, int segment, float radius, Vector2f [] uv) {
		double degree = (360/this.segments);
		double r1 = Math.toRadians(degree*segment);
		double r2 = Math.toRadians(segments == segment+1 ? 0 : degree*(segment+1));
		
		Vector3f [] vec = new Vector3f [] {
				new Vector3f ((float)Math.cos(r1) * radius, 0f, (float)Math.sin(r1) * radius),
				new Vector3f ((float)Math.cos(r1) * radius, this.height, (float)Math.sin(r1) * radius),
				new Vector3f ((float)Math.cos(r2) * radius, this.height, (float)Math.sin(r2) * radius),
				new Vector3f ((float)Math.cos(r2) * radius, 0f, (float)Math.sin(r2) * radius)
		};
		
		Vector3f [] uvs = new Vector3f [] {
				new Vector3f(segment * (uv[1].x/segments), uv[0].y, 0f),
				new Vector3f(segment * (uv[1].x/segments), uv[1].y, 0f),
				new Vector3f((segment+1) * (uv[1].x/segments), uv[1].y, 0f),
				new Vector3f((segment+1) * (uv[1].x/segments), uv[0].y, 0f)
		};
		
		this.vec = vec;
		this.tex = uvs;
	}
	
	public void cleanup() {
		// delete it if it is not used any more
		glDeleteLists(callIndex, 1);
	}
	
	public void setRotation (float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}
	
	public void render() {
		glDisable(GL_LIGHTING);
		glEnable(GL_BLEND);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, this.texture);
			
			glRotatef(180f, 1, 0, 0);
			glRotatef((this.rotation+=rotationSpeed) >= 360f ? this.rotation = 0f : this.rotation, 0, 1, 0);
			glPushMatrix();
				glCallList(callIndex);
			glPopMatrix();
			
			glDisable(GL_TEXTURE_2D);
		glPopMatrix();
		
		glDisable(GL_BLEND);
		glEnable(GL_LIGHTING);
		
	}
}
