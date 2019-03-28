package com.mnn.society.gui.windows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.gui.Button;
import com.mnn.society.gui.TextBox;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IMouseListener;

/**
 * A settings window. Used now for planetary generation.
 * @author mickeman
 * 
 */
public class SettingsWindow implements IMouseListener {
	public Button		saveButton;
	public TextBox		frequencyBox;
	public TextBox		amplitudeBox;
	public TextBox		octavesBox;
	public TextBox		persistanceBox;
	
	private Vector2f []	bounds;
	
	public SettingsWindow (Vector2f [] bounds) {
		this.bounds = bounds;
		
		//this will run the settings
		this.saveButton = new Button(new Vector2f [] { new Vector2f(0f, this.bounds[1].y-25f), new Vector2f(52f, 25f) }, "saveSettingsButton", TextureLoader.loadSharedTexture("res\\textures2.png"), 
				new Vector2f [] { 
					new Vector2f(0f, 0.394f), 
					new Vector2f(0.099609375f, 0.394f),
					new Vector2f(0.099609375f, 0.44f),
					new Vector2f(0f, 0.44f) }, this.bounds, 0f);
		this.frequencyBox 	= new TextBox("", new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(200f, 25f) }, 1, -1, 0.5f, new Vector4f(0.5f, 0.5f, 0.5f, 0.5f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(1.0f, 1.0f, 1.0f, 0.0f), bounds);
		this.amplitudeBox 	= new TextBox("", new Vector2f [] { new Vector2f(0f, 30f), new Vector2f(200f, 25f) }, 1, -1, 0.5f, new Vector4f(0.5f, 0.5f, 0.5f, 0.5f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(1.0f, 1.0f, 1.0f, 0.0f), bounds);
		this.octavesBox 	= new TextBox("", new Vector2f [] { new Vector2f(0f, 60f), new Vector2f(200f, 25f) }, 1, -1, 0.5f, new Vector4f(0.5f, 0.5f, 0.5f, 0.5f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(1.0f, 1.0f, 1.0f, 0.0f), bounds);
		this.persistanceBox = new TextBox("", new Vector2f [] { new Vector2f(0f, 90f), new Vector2f(200f, 25f) }, 1, -1, 0.5f, new Vector4f(0.5f, 0.5f, 0.5f, 0.5f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(1.0f, 1.0f, 1.0f, 0.0f), bounds);
		
		this.frequencyBox.addMouseListener(this);
		this.amplitudeBox.addMouseListener(this);
		this.octavesBox.addMouseListener(this);
		this.persistanceBox.addMouseListener(this);
	}
	
	public void render () {
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			this.saveButton.render();
			this.frequencyBox.render();
			this.amplitudeBox.render();
			this.octavesBox.render();
			this.persistanceBox.render();
		glPopMatrix();
	}
	
	@Override
	public void onClick(IGUIObject obj) {

	}
}
