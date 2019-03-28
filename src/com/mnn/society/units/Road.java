package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;


import com.mnn.society.graphics.Plane;
import com.mnn.society.graphics.textures.TextureLoader;

public class Road {
	public static int texId = 0;
	public static Plane [] roads = null;
	
	public Road() {
		
		if (this.roads == null) {
			Road.roads = new Plane[4];
			
			try {
				Road.texId = TextureLoader.loadSharedTexture("res\\textures.bmp");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//the first road
			/*Road.roads[0] = new Plane(Road.texId, new Vector2f [] {
					new Vector2f(0f, 		0.25f),
					new Vector2f(0.0625f, 	0.1875f),
					new Vector2f(0.0f, 		0.1875f),
					new Vector2f(0.0f, 		0.25f),
					new Vector2f(0.0625f, 	0.25f),
					new Vector2f(0.0625f, 	0.1875f) }, 0.44f );
			
			Road.roads[1] = new Plane(Road.texId, new Vector2f [] {
					new Vector2f(0.0625f, 	0.25f),
					new Vector2f(0.125f, 	0.1875f),
					new Vector2f(0.0625f, 	0.1875f),
					new Vector2f(0.0625f, 	0.25f),
					new Vector2f(0.125f, 	0.25f),
					new Vector2f(0.125f, 	0.1875f) }, 0.44f );
			
			Road.roads[2] = new Plane(Road.texId, new Vector2f [] {
					new Vector2f(0.125f, 	0.25f),
					new Vector2f(0.1875f, 	0.1875f),
					new Vector2f(0.125f, 	0.1875f),
					new Vector2f(0.125f, 	0.25f),
					new Vector2f(0.1875f,	0.25f),
					new Vector2f(0.1875f, 	0.1875f) }, 0.44f );
			
			Road.roads[3] = new Plane(Road.texId, new Vector2f [] {
					new Vector2f(0.1875f, 	0.25f),
					new Vector2f(0.25f, 	0.1875f),
					new Vector2f(0.1875f, 	0.1875f),
					new Vector2f(0.1875f, 	0.25f),
					new Vector2f(0.25f, 	0.25f),
					new Vector2f(0.25f, 	0.1875f) }, 0.44f );*/
		}
	}
	
	public void render(long delta, int type) {
		glPushMatrix();
			glRotatef(90f, 0f, 0f, 1f);
		
			Road.roads[type].render();
		glPopMatrix();
	}
}
