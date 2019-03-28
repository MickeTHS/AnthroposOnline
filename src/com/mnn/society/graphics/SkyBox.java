package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.textures.TextureLoader;

public class SkyBox {
	public Vector3f		position;
	private int			callIndex;
	private float		size;
	private int			texture1;
	private int			texture2;
	private int			texture3;
	private int			texture4;
	private int			texture5;
	private int			texture6;
	
	public SkyBox () {
		this.texture1 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_back6.png");
		this.texture2 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_top3.png");
		this.texture3 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_bottom4.png");
		this.texture4 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_front5.png");
		this.texture5 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_left2.png");
		this.texture6 = TextureLoader.loadSharedTexture("res\\skybox\\highres_skybox_right1.png");
		this.position = new Vector3f(0f, 0f, 0f);
		this.size = 20f;
		this.generate();
	}
	
	private void generate () {
		callIndex = glGenLists(1);
		
		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, this.texture1);
			glBegin(GL_QUADS);
				//top
				glColor3f(1.0f, 1.0f, 1.0f);	
				glTexCoord2f(0.0f, 0.0f);
				glVertex3f(-this.size/2f, +this.size/2f, +this.size/2f);
				glTexCoord2f(1.0f, 0.0f);
				glVertex3f(+this.size/2f, +this.size/2f, +this.size/2f);
				glTexCoord2f(1.0f, 1.0f);
				glVertex3f(+this.size/2f, +this.size/2f, -this.size/2f);
				glTexCoord2f(0.0f, 1.0f);
				glVertex3f(-this.size/2f, +this.size/2f, -this.size/2f);
			glEnd();
			
			glBindTexture(GL_TEXTURE_2D, this.texture2);
			glBegin(GL_QUADS);
				//back
	    		glColor3f(1.0f, 1.0f, 1.0f);
				glTexCoord2f(0.0f, 0.0f);
	    		glVertex3f(-this.size/2f, +this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 0.0f);
	    		glVertex3f(+this.size/2f, +this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 1.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, +this.size/2f);
	    		glTexCoord2f(0.0f, 1.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, +this.size/2f);
	    	glEnd();
	    	
	    	glBindTexture(GL_TEXTURE_2D, this.texture3);
			glBegin(GL_QUADS);
				//bottom
				glColor3f(1.0f, 1.0f, 1.0f);
	    		glTexCoord2f(0.0f, 0.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 0.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 1.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, -this.size/2f);
	    		glTexCoord2f(0.0f, 1.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, -this.size/2f);
	    	glEnd();
	    		
	    	glBindTexture(GL_TEXTURE_2D, this.texture4);
			glBegin(GL_QUADS);
				//forward
				glColor3f(1.0f, 1.0f, 1.0f);
	    		glTexCoord2f(0.0f, 0.0f);
	    		glVertex3f(-this.size/2f, +this.size/2f, -this.size/2f);
	    		glTexCoord2f(1.0f, 0.0f);
	    		glVertex3f(+this.size/2f, +this.size/2f, -this.size/2f);
	    		glTexCoord2f(1.0f, 1.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, -this.size/2f);
	    		glTexCoord2f(0.0f, 1.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, -this.size/2f);
	    	glEnd();
	    	
	    	glBindTexture(GL_TEXTURE_2D, this.texture5);
			glBegin(GL_QUADS);
				//left
				glColor3f(1.0f, 1.0f, 1.0f);
	    		glTexCoord2f(0.0f, 0.0f);
	    		glVertex3f(-this.size/2f, +this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 0.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 1.0f);
	    		glVertex3f(-this.size/2f, -this.size/2f, -this.size/2f);
	    		glTexCoord2f(0.0f, 1.0f);
	    		glVertex3f(-this.size/2f, +this.size/2f, -this.size/2f);
	    	glEnd();
	    	
	    	glBindTexture(GL_TEXTURE_2D, this.texture6);
			glBegin(GL_QUADS);
				//right
				glColor3f(1.0f, 1.0f, 1.0f);
	    		glTexCoord2f(0.0f, 0.0f);
	    		glVertex3f(+this.size/2f, +this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 0.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, +this.size/2f);
	    		glTexCoord2f(1.0f, 1.0f);
	    		glVertex3f(+this.size/2f, -this.size/2f, -this.size/2f);
	    		glTexCoord2f(0.0f, 1.0f);
	    		glVertex3f(+this.size/2f, +this.size/2f, -this.size/2f);
	    	glEnd();
	    glEndList();
	}
	
	public void render () {
		glDisable(GL_LIGHTING);
		
		glPushMatrix();
			glCallList(callIndex);
		glPopMatrix();
		
		glEnable(GL_LIGHTING);
	}
}