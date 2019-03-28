package com.mnn.society.graphics;

import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

import com.mnn.society.server.utils.Logger;

public class Planet {
	private Shader waterShader;
	private Shader earthShader;
	
	private ICOSphere earth;
	private ICOSphere water;
	private float zoomFactor = 1f;
	
	public Planet () {
		try {
			this.earthShader = new Shader("earth", "earth");
			this.waterShader = new Shader("water", "water");
			this.earth = new ICOSphere(new Vector3f(0f, 0f, 0f), 1.0f, true, 0.45f, 0.013f, 4, 3.0f);
			this.water = new ICOSphere(new Vector3f(0f, 0f, 0f), 1.96f, false, 0.45f, 0.087f, 6, 1.01f);
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : Planet : earth : " + e);
		}
		this.water.setScale(1.5f);
	}
	
	public void setZoomFactor (float zoomFactor) {
		this.zoomFactor = zoomFactor;
		this.earth.setZoomFactor(zoomFactor);
	}
	
	public float getZoomFactor () {
		return this.zoomFactor;
	}
	
	public void setWaterScale (float scale) {
		this.water.setScale(scale);
	}
	
	public void generateEarth (float frequency, float amplitude, int octaves, float persistance) {
		this.earth = new ICOSphere(new Vector3f(0f, 0f, 0f), 1.5f, true, frequency, amplitude, octaves, persistance);
	}
	
	public void render () {
		glPushMatrix();
			if (this.waterShader != null) this.waterShader.preRender();
			this.water.render();
			Shader.clearAllShaders();
		glPopMatrix();
		
		glPushMatrix();
			if (this.earthShader != null) this.earthShader.preRender();
			this.earth.render();
			Shader.clearAllShaders();
		glPopMatrix();
	}
}