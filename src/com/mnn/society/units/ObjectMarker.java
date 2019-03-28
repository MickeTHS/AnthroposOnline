package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.Ring;
import com.mnn.society.graphics.textures.TextureLoader;

/**
 * a rotating ring that is centered on the selected unit
 * @author mickeman
 *
 */
public class ObjectMarker {
	private Ring ring;
	
	public ObjectMarker () {
		this.ring = new Ring(0.4f, new Vector2f [] { new Vector2f(0f, 0.9580f), new Vector2f(1f, 0.9883f) }, TextureLoader.loadSharedTexture("res\\textures2.png"));
		this.ring.setRotation(0.1f);
	}
	
	public void setPosition (Vector3f pos) {
		this.ring.position = pos;
	}
	
	public void render () {
		this.ring.render();
	}
}
