package com.mnn.society.graphics;

import static org.lwjgl.opengl.GL11.GL_FALSE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.server.utils.Logger;

/**
 * this is a test to see if i can efficiently replace an alpha color with another color
 * THIS TEST IS SUCCESSFUL!!! FULLY WORKING SHADER!
 * the texture must be 32bit PNG with "transparent pixels", the transparancy will be replaced with the color.
 * @author mickeman
 *
 */
public class AlphaTest {
	private Shader alphaShader;
    
    private Plane plane;
    private FloatBuffer resolutionOverride;
    private int textureId;
    private Vector4f color;
    
	public AlphaTest(float size, Vector4f color) {
		this.resolutionOverride = BufferUtils.createFloatBuffer(2);
		this.resolutionOverride.clear();
		this.resolutionOverride.put(100f).put(100f);
		this.resolutionOverride.flip();
		this.textureId 	= TextureLoader.loadSharedTexture("res\\alpha_test.png");
		this.color 		= color;
		
		try {
			this.alphaShader = new Shader("alphacolor", "alphacolor");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : unable to create shader for alphatest: " + e);
		}
		
		this.plane = new Plane(new Vector3f(size/2f, 1.4f, size/2f), size, new Vector2f [] { new Vector2f(0f,0f), new Vector2f(1f,0f), new Vector2f(1f,1f), new Vector2f(0f,1f) }, this.textureId);
	}
	
	public void render(float time, FloatBuffer resolution) {
		if (this.alphaShader != null) { 
			this.alphaShader.preRender();
			
			int my_color = ARBShaderObjects.glGetUniformLocationARB(this.alphaShader.programId, "my_color");
			if (my_color < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : my_color failed"); }
			ARBShaderObjects.glUniform4fARB(my_color, this.color.x, this.color.y, this.color.z, this.color.w);
			
			int sampler01 = ARBShaderObjects.glGetUniformLocationARB(this.alphaShader.programId, "my_color_texture");
			if(sampler01 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : sampler01 failed"); }
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
			ARBShaderObjects.glUniform1iARB(sampler01, 0);
		}
		
		this.plane.render();
		
		Shader.clearAllShaders();
	}
}
