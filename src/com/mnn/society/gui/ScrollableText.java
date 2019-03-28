package com.mnn.society.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.textures.Texture;
import com.mnn.society.graphics.textures.TextureLoader;
import com.mnn.society.gui.windows.Console;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IMouseListener;

import static org.lwjgl.opengl.GL11.*;

public class ScrollableText implements IMouseListener {
	
	private Vector2f [] 	bounds;
	private float 			fontsize;
	private static int 		SCROLLABLECOUNTER = 0;
	private int 			id;
	private ScreenText [] 	texts = new ScreenText[1];
	
	public Button 			scrollerUp;
	public Button			scrollerDown;
		
	public ScrollableText(float fontsize, Vector2f [] bounds) {
		this.id = ScrollableText.SCROLLABLECOUNTER++; 
		this.bounds = bounds;
		this.fontsize = fontsize;
		this.texts[0] = new ScreenText("window loaded", 0.5f, new Vector4f(1f, 1f, 1f, 0f), new Vector2f [] { new Vector2f(0f, (this.bounds[1].y-25f)), new Vector2f(bounds[1].x, 25f) }, 0f);
		
		int tex = TextureLoader.loadSharedTexture("res\\textures2.png");
		
		this.scrollerUp = new Button(new Vector2f [] { new Vector2f(bounds[1].x-25f, 0f), new Vector2f(25f, 25f)}, "up", tex, 
				new Vector2f [] { new Vector2f(0.125f, 0.0625f), new Vector2f(0.1875f, 0.0625f), new Vector2f(0.1875f, 0.125f), new Vector2f(0.125f, 0.125f) }, bounds, 0f);
		
		this.scrollerDown = new Button(new Vector2f [] { new Vector2f(bounds[1].x-25f, bounds[1].y-25f), new Vector2f(25f, 25f)}, "down", tex, 
				new Vector2f [] { new Vector2f(0.1875f, 0.0625f), new Vector2f(0.25f, 0.0625f), new Vector2f(0.25f, 0.125f), new Vector2f(0.1875f, 0.125f) }, bounds, 0f);
		
		this.scrollerDown.addMouseListener(this);
		this.scrollerUp.addMouseListener(this);
		
	}
	
	public void addString(String text) {
		ScreenText [] tmp = new ScreenText [this.texts.length+1];
		
		for (int i = 0; i < this.texts.length; i++) {
			tmp[i] = this.texts[i];
			tmp[i].bounds[0].y -= 25f;
		}
		
		tmp[this.texts.length] = new ScreenText(text, 0.5f, new Vector4f(1f, 1f, 1f, 0f), new Vector2f [] { new Vector2f(0f, (this.bounds[1].y-25f)), new Vector2f(bounds[1].x, 25f) }, 0f);
		this.texts = tmp;
		
	}
	
	public void render() {
		//glPushAttrib(GL_CURRENT_BIT);
		glPushMatrix();
			//glTranslatef(this.scrollerUp.bounds[0].x, this.scrollerUp.bounds[0].y, 0f);
			this.scrollerUp.render();
			//glTranslatef(0f, this.scrollerDown.bounds[0].y, 0f);
			this.scrollerDown.render();
		glPopMatrix();
		
		glTranslatef(bounds[0].x, bounds[0].y, 0f);
		glPushMatrix();
			glBegin(GL_QUADS);
				glColor4f(0.3f, 0.3f, 0.3f,0.5f);
				glVertex3f(0f, 0f, 0.001f); //we need to put it slightly behind everything
				glVertex3f(bounds[1].x, 0f, 0.001f);
				glVertex3f(bounds[1].x, bounds[1].y, 0.001f);
				glVertex3f(0f, bounds[1].y, 0.001f);
				glNormal3f(0f, 0f, -1f);
			glEnd();
		glPopMatrix();
		
		for (int i = this.texts.length-1; i >= 0; i--) {
			this.texts[i].render();
		}
		//glPopAttrib();
	}
	
	@Override
	public String toString() {
		return "ScrollBox"+this.id;
	}

	@Override
	public void onClick(IGUIObject obj) {
		if (obj == this.scrollerDown) {
			Console.log(Console.CONSOLE, "down clicked");
		}
		else if (obj == this.scrollerUp) {
			Console.log(Console.CONSOLE, "up clicked");
		}
	}
}
