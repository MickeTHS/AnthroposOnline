package com.mnn.society.gui.windows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.gui.Button;
import com.mnn.society.gui.TextBox;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IMouseListener;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.units.Worker;

/**
 * this is the window where the BAMUL code is being written
 * @author mickeman
 *
 */
public class BAMULWindow implements IMouseListener {
	public TextBox			box;
	public Button			saveButton;
	public Button			closeButton;
	private boolean			visible;
	private Vector2f [] 	bounds;
	private Worker			selectedWorker;
	
	public BAMULWindow (Worker selected, Vector2f [] bounds, String initial) {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : BAMULWindow : Creating...");
		this.bounds 	= bounds;
		this.visible 	= true;
		this.selectedWorker = selected;
		//this.box = new TextBox(new Vector2f [] { new Vector2f(Display.getWidth()/2-150,Display.getHeight()/2-100), new Vector2f(300f, 200f) }, 4, 16);
		this.box 		= new TextBox(this.selectedWorker.getBAMULScript(), new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(550f, 255f) }, 4, 16, 2f, new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(0.1f, 0.7f, 0.1f, 0.0f), bounds);
		
		//button that saves the BAMUL script
		this.saveButton = new Button(new Vector2f [] { new Vector2f(0f, this.bounds[1].y-25f), new Vector2f(52f, 25f) }, "saveButton", TextureLoader.loadSharedTexture("res\\textures2.png"), 
				new Vector2f [] { 
					new Vector2f(0f, 0.394f), 
					new Vector2f(0.099609375f, 0.394f),
					new Vector2f(0.099609375f, 0.44f),
					new Vector2f(0f, 0.44f) }, this.bounds, 0f);
		
		this.closeButton = new Button(new Vector2f [] { new Vector2f(52f, this.bounds[1].y-25f), new Vector2f(52f, 25f) }, "closeButton", TextureLoader.loadSharedTexture("res\\textures2.png"), 
				new Vector2f [] { 
					new Vector2f(0f, 0.444f), 
					new Vector2f(0.099609375f, 0.444f),
					new Vector2f(0.099609375f, 0.488f),
					new Vector2f(0f, 0.488f) }, this.bounds, 0f);

		
		this.box.addMouseListener(this);
		this.saveButton.addMouseListener(this);
		this.closeButton.addMouseListener(this);
	}
	
	public String getBAMULString () {
		return this.box.getText();
	}
	
	public boolean isVisible () {
		return this.visible;
	}
	
	public void setVisible (boolean visible) {
		this.visible = visible;
		this.saveButton.setVisible(visible);
		this.closeButton.setVisible(visible);
		this.box.setVisible(visible);
	}
	
	public void render () {
		if (!visible) return; //if not visible, dont render
		
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);	
			this.box.render();
			this.saveButton.render();
			this.closeButton.render();
		glPopMatrix();
	}
	
	@Override
	public void onClick (IGUIObject obj) {
		if (!visible) return;
		
		if (obj == this.box) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : BAMULWindow : TextBox clicked");
		}
		else if (obj == this.saveButton) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : BAMULWindow : save button clicked");
		}
		else if (obj == this.closeButton) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : BAMULWindow : close button clicked, hiding window");
			this.setVisible(false);
		}
	}
}
