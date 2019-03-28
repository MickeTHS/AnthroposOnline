package com.mnn.society.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IKeyboardListener;
import com.mnn.society.input.IMouseListener;
import com.mnn.society.server.utils.Logger;


public class TextBox implements IGUIObject {

	private IMouseListener 		mouseListener;
	private IKeyboardListener 	keyboardListener;
	private int 				callIndex;
	private Vector2f [] 		uv;
	private int 				texture;
	public 	Vector2f [] 		bounds;
	private Marker 				mark;
	private boolean				selected = false;
	private Vector4f			color;
	private Vector4f			borderColor;
	private Vector2f []			parentBounds;
	private float 				fontsize;
	
	private ScreenText []		textRows;
	private int					rows;
	private int					maxChars;
	private boolean				visible = true;
	
	/**
	 * 
	 * @param bounds - the size and position
	 * @param rows - amount of rows, when maxChars has been reached, will break to a new line, if bigger than 1
	 * @param maxChars - -1 is infinite
	 */
	public TextBox (String initialString, Vector2f [] bounds, int rows, int maxChars, float fontsize, Vector4f color, Vector4f borderColor, Vector4f fontColor, Vector2f [] parentBounds) {
		this.rows 		= rows;
		this.maxChars	= maxChars;
		this.bounds 	= bounds;
		this.uv 		= new Vector2f [] { new Vector2f(0.25f, 0.0625f), new Vector2f(0.3125f, 0.0625f), new Vector2f(0.3125f, 0.125f), new Vector2f(0.25f, 0.125f) };
		this.mark 		= new Marker(new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(fontsize*4f, (this.bounds[1].y/rows)-4f)});
		this.textRows 	= new ScreenText [rows];
		this.color 		= color;
		this.borderColor= borderColor;
		this.parentBounds=parentBounds;
		this.fontsize	= fontsize;
		
		if (this.maxChars != -1) {
			int i;
			for (i = 0; i <= (initialString.length()-1) / maxChars; i++) {
				this.textRows[i] = new ScreenText(initialString.substring(i*maxChars, i*maxChars+maxChars > initialString.length()-1 ? initialString.length() : i*maxChars+maxChars), fontsize, fontColor, new Vector2f [] { new Vector2f(2f, 6f+(i*fontsize*30f)), new Vector2f(this.bounds[1].x, this.bounds[1].y/rows) }, 0f);
			}
			
			for (; i < rows; i++) {
				this.textRows[i] = new ScreenText("", fontsize, fontColor, new Vector2f [] { new Vector2f(2f, 6f+(i*fontsize*30f)), new Vector2f(this.bounds[1].x, this.bounds[1].y/rows) }, 0f);
			}
		}
		else {
			this.textRows[0] = new ScreenText(initialString, fontsize, fontColor, new Vector2f [] { new Vector2f(2f, 6f), new Vector2f(0f, 0f) }, 0f);
		}
		
		generate();
	}
	
	private void generate () {
		// create one display list
		this.callIndex = glGenLists(1);

		// compile the display list, store a triangle in it
		glNewList(callIndex, GL_COMPILE);
			glBegin(GL_QUADS);
		    	glColor4f(this.color.x, this.color.y, this.color.z, this.color.w);
		    	glVertex3f(1f, 1f, 0.001f);
		    	glVertex3f(this.bounds[1].x-1, 1f, 0.001f);
		    	glVertex3f(this.bounds[1].x-1, this.bounds[1].y-1, 0.001f);
		    	glVertex3f(1f, this.bounds[1].y-1, 0.001f);
		    	glNormal3f(0f, 0f, -1f);
		    	
		    	glColor4f(this.borderColor.x, this.borderColor.y, this.borderColor.z, this.borderColor.w);
		    	glVertex3f(0f, 0f, 0.001f);
		    	glVertex3f(this.bounds[1].x, 0f, 0.001f);
		    	glVertex3f(this.bounds[1].x, this.bounds[1].y, 0.001f);
		    	glVertex3f(0f, this.bounds[1].y, 0.001f);
		    	glNormal3f(0f, 0f, -1f);
	    	glEnd();
		glEndList();
	}
	
	public void setVisible (boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible () {
		return this.visible;
	}
	
	public String getText () {
		String str = "";
		
		for (int i = 0; i < this.textRows.length; i++) {
			str += this.textRows[i].getText();
		}
		
		return str;
	}
	
	public void cleanup () {
		// delete it if it is not used any more
		glDeleteLists(this.callIndex, 1);
	}
	
	public void render () {
		glPushMatrix();
			glTranslatef(this.bounds[0].x, this.bounds[0].y, 0f);
			
			glPushMatrix();
				glCallList(this.callIndex);
			glPopMatrix();
			
			//draw all the characters
			for (int i = 0; i < this.textRows.length; i++) {
				this.textRows[i].render();
			}
			
			if (this.selected) {
				glPushMatrix();
					glTranslatef(2f+this.mark.bounds[0].x, 2f+this.mark.bounds[0].y, -0.2f);
					this.mark.render();
				glPopMatrix();
			}
		glPopMatrix();
	}
	
	public void setMarker (boolean visible) {
		this.selected = visible;
	}
	
	@Override
	public void addMouseListener (IMouseListener listener) {
		this.mouseListener = listener;
	}

	@Override
	public void addKeyboardListener (IKeyboardListener listener) {
		this.keyboardListener = listener;
	}
	
	@Override
	public int keyboardEvent (char [] sequence) {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : TextBox : keyboardEvent : " + sequence[0]);
		
		for (int i = 0; i < sequence.length && sequence[i] != '\0'; i++) {
			if (sequence[i] == 13 && this.keyboardListener != null) this.keyboardListener.onKeyDown(this);
			
			//check which ScreenText is not empty
			if (sequence[i] == 8) {
				int last = -1;
				for (int j = this.textRows.length-1; j >= 0; j--) {
					if (this.textRows[j].size() < this.maxChars || this.maxChars == -1) {
						last = j;
					}
					else if (this.textRows[j].size() == this.maxChars && this.textRows[j+1 < this.textRows.length ? j+1 : j].size() == 0) {
						last = j;
					}
				}
				
				if (last == -1) last = this.textRows.length-1;
				
				this.textRows[last].removeLastChar();
				
				if (this.textRows[last].size() == 0 && last > 0) { //move up one row and put the marker at the end
					//this.mark.bounds[0].x = this.textRows[last-1].width + 3f;
					//this.mark.bounds[0].y = (last-1) * this.textRows[last-1].bounds[1].y;
					setMarkPosition(last-1, this.textRows[last-1].size());
					continue;
				}
				
				setMarkPosition(last, this.textRows[last].size());
				//this.mark.bounds[0].x = this.textRows[last].width + 3f;
				//this.mark.bounds[0].y = (last) * this.textRows[last].bounds[1].y;
			}
			else {
				int last = -1;
				for (int j = this.textRows.length-1; j >= 0; j--) {
					if (this.textRows[j].size() < this.maxChars || this.maxChars == -1) {
						last = j;
					}
				}
				
				if (last != -1) {
					this.textRows[last].addText(sequence[i]+"");
					setMarkPosition(last, this.textRows[last].size());
					//this.mark.bounds[0].x = this.textRows[last].width + 3f;
					//this.mark.bounds[0].y = last * this.textRows[last].bounds[1].y;
				}
			}
		}
		
		return 0;
	}
	
	/* pos is not really working, need to calculate the width of each character before the pos index */
	public void setMarkPosition (int row, int pos) {
		this.mark.bounds[0].x = this.textRows[row].width + 3f;
		this.mark.bounds[0].y = row * (this.textRows[row].bounds[1].y-4f);
	}
	
	public void setMarkAtEnd () {
		int last = -1;
		
		for (int i = this.textRows.length-1; i >= 0; i--) {
			last = i;
			if (this.textRows[i].size() > 0) break;
		}
		
		setMarkPosition(last, 0);
	}
	
	@Override
	public int mouseEvent (float x, float y, int button) {
		if (!this.visible) return IGUIObject.DONT_BOTHER;
		
		if (this.mouseListener != null && 
			this.parentBounds[0].x + this.bounds[0].x <= x && 
			this.parentBounds[0].y + this.bounds[0].y <= y && 
			this.parentBounds[0].x + this.bounds[0].x + this.bounds[1].x >= x && 
			this.parentBounds[0].y + this.bounds[0].y + this.bounds[1].y >= y) {
			this.mouseListener.onClick(this);
			
			this.setFocus(true);
			
			return IGUIObject.FOCUS;
		}
		
		return IGUIObject.DONT_BOTHER;
	}

	@Override
	public void setFocus (boolean status) {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : TextBox : got focus");
		this.setMarker(status);
		this.setMarkAtEnd();
	}
	
	public void clear () {
		for (int i = 0; i < this.textRows.length; i++) {
			this.textRows[i].clear();
		}
		
		setMarkAtEnd();
	}
}
