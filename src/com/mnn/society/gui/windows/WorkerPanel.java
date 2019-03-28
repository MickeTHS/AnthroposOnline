package com.mnn.society.gui.windows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.gui.Button;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IMouseListener;

/**
 * This panel holds all the UI related stuff of the worker.
 * When you click on a Worker, this panel will be displayed,
 * giving the player some options on how to interact with
 * the Worker.
 * @author mickeman
 * 
 */
public class WorkerPanel implements IMouseListener {
	public Button		bamulButton;
	public Button		pauseButton;
	public Button		runButton;
	
	private Vector2f []	bounds;
	
	public WorkerPanel (Vector2f [] bounds) {
		this.bounds = bounds;
		
		int tex = TextureLoader.loadSharedTexture("res\\textures2.png");
		
		//button to open the BAMUL editor
		this.bamulButton = new Button(new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(52f, 25f) }, "bamulButton", tex, 
			new Vector2f [] {
				new Vector2f(0f, 0.25f), 
				new Vector2f(0.099609375f, 0.25f),
				new Vector2f(0.099609375f, 0.296875f),
				new Vector2f(0f, 0.296875f) }, this.bounds, 0f);
	
		//Pauses the BAMUL script currently running
		this.pauseButton = new Button(new Vector2f [] { new Vector2f(52f, 0f), new Vector2f(52f, 25f) }, "pauseButton", tex, 
			new Vector2f [] {
				new Vector2f(0f, 0.348f), 
				new Vector2f(0.099609375f, 0.348f),
				new Vector2f(0.099609375f, 0.394f),
				new Vector2f(0f, 0.394f) }, this.bounds, 0f);
	
		//Runs the current BAMUL script
		this.runButton = new Button(new Vector2f [] { new Vector2f(104f, 0f), new Vector2f(52f, 25f) }, "runButton", tex, 
			new Vector2f [] {
				new Vector2f(0f, 0.299f), 
				new Vector2f(0.099609375f, 0.299f),
				new Vector2f(0.099609375f, 0.345f),
				new Vector2f(0f, 0.345f) }, this.bounds, 0f);
		
		//this.bamulButton.addMouseListener(this);
		//this.runButton.addMouseListener(this);
		//this.pauseButton.addMouseListener(this);
	}
	
	public void render () {
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			this.bamulButton.render();
			this.pauseButton.render();
			this.runButton.render();
		glPopMatrix();
	}
	
	@Override
	public void onClick(IGUIObject obj) {
		/*if (obj == this.pauseButton) {
			System.out.println("CLIENT : WorkerPanel : Clicked on pause button");
		}
		else if (obj == this.runButton) {
			System.out.println("CLIENT : WorkerPanel : Clicked on run button");
		}*/
	}
}
