package com.mnn.society.units;

import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.Mesh;
import com.mnn.society.graphics.OBJLoader;
import com.mnn.society.graphics.VBOHandler;
import com.mnn.society.graphics.textures.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class House {
	
	public static Mesh houseMesh = null;
	
	public Vector3f position = new Vector3f();
	
	public House() {
		if(houseMesh == null) {
			houseMesh = OBJLoader.getModelFromFile("res\\house.obj");
			//houseMesh.textureFilename = "res\\textures.bmp";
			houseMesh.dataTexId = TextureLoader.loadSharedTexture("res\\textures.bmp");
			
			VBOHandler.setupVBO(houseMesh);
		}
	}
	
	public void render() {
		glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			glScalef(0.05f,0.05f,0.05f);
			glRotatef(-90f, 0f, 1f, 0f);
			VBOHandler.renderVBO(houseMesh, 0);
		glPopMatrix();
	}
}
