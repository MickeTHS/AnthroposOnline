package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.glColor4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * A wise man might ask himself why this is needed.
 * As do I.
 * @author mickeman
 *
 */
public class Quad {
	public Face a, b;
	public int textureId;
	public int elementSize;
	
	public int dataVboId;
	public int dataTexId = -1;
	public int vHandle;
	public FloatBuffer vBuffer;
	public IntBuffer nBuffer;
	
	public int tSize;
	
	public void render() {
		VBOHandler.renderVBO(this);
	}
}
