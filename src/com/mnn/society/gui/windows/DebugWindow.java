package com.mnn.society.gui.windows;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.gui.ScreenText;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IMouseListener;

import static org.lwjgl.opengl.GL11.*;

public class DebugWindow implements IMouseListener {
	private ScreenText 		fpsLabel;
	private Vector2f [] 	bounds;
	
	public DebugWindow (Vector2f [] bounds) {
		this.bounds		= bounds;
		this.fpsLabel 	= new ScreenText("fps: ", 0.5f, new Vector4f(0.8f, 0.1f, 0.2f, 0.0f), new Vector2f [] {new Vector2f(0f, 0f), new Vector2f(200f, 25f)}, 0.1f);
	}
	
	public void setFPS (float fps) {
		this.fpsLabel.setText("fps: " + Float.toString(fps));
	}
	
	@Override
	public void onClick(IGUIObject obj) {
		// TODO Auto-generated method stub
	}
	
	public void render () {
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			this.fpsLabel.render();
		glPopMatrix();
	}
}
