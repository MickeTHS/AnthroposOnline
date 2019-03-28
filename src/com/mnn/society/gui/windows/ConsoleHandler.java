package com.mnn.society.gui.windows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.gui.ScrollableText;
import com.mnn.society.gui.TextBox;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IKeyboardListener;
import com.mnn.society.input.IMouseListener;
import com.mnn.society.server.utils.Logger;

public class ConsoleHandler implements IMouseListener {
	public ScrollableText 	scroller;
	public TextBox 			box;
	private Vector2f [] 	bounds;
	
	public ConsoleHandler(  ) {
		this.bounds = new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(300f, 200f) };
		
		//the scrolling content
		this.scroller 	= new ScrollableText(1f, new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(500f, 200f) });
		//the input box
		this.box 		= new TextBox("", new Vector2f [] { new Vector2f(0f, 200f), new Vector2f(500f, 25f) }, 1, -1, 0.5f, new Vector4f(0.5f, 0.5f, 0.5f, 0.5f), new Vector4f(0.0f, 0.0f, 0.0f, 0.5f), new Vector4f(1.0f, 1.0f, 1.0f, 0.0f), bounds);
		//set the general console as the scrolling content
		Console.general = this.scroller;
		
		this.box.addMouseListener(this);
	}
	
	public void render() {
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			this.scroller.render();
			this.box.render();
		glPopMatrix();
	}

	@Override
	public void onClick(IGUIObject obj) {
		if (obj == this.box) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : ConsoleHandler : the console box has been clicked");
		}
	}
}
