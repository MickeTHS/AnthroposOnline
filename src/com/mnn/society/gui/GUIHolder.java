package com.mnn.society.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

import com.mnn.society.gui.windows.BAMULWindow;
import com.mnn.society.gui.windows.ConsoleHandler;
import com.mnn.society.gui.windows.DebugWindow;
import com.mnn.society.gui.windows.MessageBox;
import com.mnn.society.gui.windows.SettingsWindow;
import com.mnn.society.gui.windows.WorkerPanel;
import com.mnn.society.input.IGUIObject;
import com.mnn.society.input.IKeyboardListener;
import com.mnn.society.input.IMouseListener;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.units.ISelectable;
import com.mnn.society.units.UnitSelector;
import com.mnn.society.units.Worker;

/**
 * This class will hold ALL the GUI components
 * @author mickeman
 *
 */
public class GUIHolder implements IMouseListener, IKeyboardListener {
	private ConsoleHandler 		console;
	private List<IGUIObject> 	mouseParsers = new ArrayList<IGUIObject>();
	private	IGUIObject			focused;
	private WorkerPanel			workerPanel;
	private BAMULWindow			bamulWindow;
	private MessageBox			msgBox;
	private LinkedList<GUIRequest> guiRequestQueue = new LinkedList<GUIRequest>(); //holds all the requests that the GUI wants to do outside of the GUI scope
	private SettingsWindow		settingsWindow;
	private DebugWindow			debug;
	
	public GUIHolder () {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : Creating...");
		this.console 		= new ConsoleHandler();
		this.workerPanel 	= new WorkerPanel(new Vector2f [] { new Vector2f(0f, 400f), new Vector2f(200f, 200f) });
		this.settingsWindow = new SettingsWindow(new Vector2f [] { new Vector2f(800f, 0f), new Vector2f(800f, 150f)});
		this.debug			= new DebugWindow(new Vector2f [] { new Vector2f(800f, 500f), new Vector2f(800f, 600f) });
		
		//this.bamulWindow 	= new BAMULWindow(new Vector2f [] { new Vector2f(300f, 200f), new Vector2f(300f, 280f) }, "");
		//this.msgBox			= new MessageBox("Script validation: Invalid syntax or commands. See www.bamulscript.com for guides.");
		
		this.mouseParsers.add(this.console.scroller.scrollerDown);
		this.mouseParsers.add(this.console.scroller.scrollerUp);
		this.mouseParsers.add(this.console.box);
		this.mouseParsers.add(this.workerPanel.bamulButton);
		this.mouseParsers.add(this.workerPanel.pauseButton);
		this.mouseParsers.add(this.workerPanel.runButton);
		this.mouseParsers.add(this.settingsWindow.saveButton);
		this.mouseParsers.add(this.settingsWindow.amplitudeBox);
		this.mouseParsers.add(this.settingsWindow.frequencyBox);
		this.mouseParsers.add(this.settingsWindow.octavesBox);
		this.mouseParsers.add(this.settingsWindow.persistanceBox);
		
		//this.mouseParsers.add(this.msgBox.closeButton);
		this.workerPanel.bamulButton.addMouseListener(this);
		this.workerPanel.pauseButton.addMouseListener(this);
		this.settingsWindow.saveButton.addMouseListener(this);
		this.console.box.addKeyboardListener(this);
		Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : Done creating");
	}
	
	/* displays the messagebox with the given message */
	public void showMessageBox (String information, boolean visibleCloseButton) {
		if (this.msgBox != null) {
			this.mouseParsers.remove(this.msgBox.closeButton);
		}
		
		this.msgBox = new MessageBox(information);
		this.msgBox.showCloseButton(visibleCloseButton);
		this.mouseParsers.add(this.msgBox.closeButton);
		this.msgBox.closeButton.addMouseListener(this);
	}
	
	/* when we want to show the BAMUL window for a specific Worker */
	public void openBAMULWindow (Worker selected) {
		if (this.bamulWindow != null) {
			this.mouseParsers.remove(this.bamulWindow.box);
			this.mouseParsers.remove(this.bamulWindow.saveButton);
			this.mouseParsers.remove(this.bamulWindow.closeButton);
		}
		
		this.bamulWindow 	= new BAMULWindow(selected, new Vector2f [] { new Vector2f(300f, 200f), new Vector2f(300f, 280f) }, "");
		this.mouseParsers.add(this.bamulWindow.box);
		this.mouseParsers.add(this.bamulWindow.saveButton);
		this.mouseParsers.add(this.bamulWindow.closeButton);
		this.bamulWindow.saveButton.addMouseListener(this);
		//this.bamulWindow.setVisible(false);
	}
	
	/* check if keyboard input is locked on a textbox or something */
	public boolean lockedKeyboardInput () {
		return focused == null ? false : true;
	}
	
	/* displays the UI */
	public void renderAll () {
		
		glDisable(GL_LIGHTING);
		
		//glColor4f(1f,1f,1f,0f);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		if (this.msgBox != null) 		this.msgBox.render();
		if (this.bamulWindow != null) 	this.bamulWindow.render();
		if (this.console != null) 		this.console.render();
		if (this.workerPanel != null) 	this.workerPanel.render();
		if (this.settingsWindow != null) this.settingsWindow.render();
		if (this.debug != null)			this.debug.render();
		
		//when the gui is done, reset the color to white
		glColor3f(1f, 1f, 1f);
		glDisable(GL_BLEND);
		glEnable(GL_LIGHTING);
	}
	
	/* gets the next action requested by the UI */
	public GUIRequest getNextGUIRequest () {
		synchronized (this.guiRequestQueue) {
			if (this.guiRequestQueue.size() == 0) return null;
			
			return this.guiRequestQueue.removeFirst();
		}
	}
	
	/* the main entrypoint for an UI click, this will forward the x y coordinates clicked to each control */
	public boolean mouseClick (float x, float y, int button) {
		IGUIObject obj = null;
		
		//MessageBox should always have priority
		if (this.msgBox != null && this.msgBox.closeButton.isVisible() && this.msgBox.closeButton.mouseEvent(x, y, button) == IGUIObject.FOCUS) return true;
		
		//loop through all elements listening on mouse clicks
		for (int i = 0; i < this.mouseParsers.size(); i++) {
			int mouseResult = -1;
			//if the click event results in a FOCUS
			if ((mouseResult = (obj = this.mouseParsers.get(i)).mouseEvent(x, y, button)) == IGUIObject.FOCUS) {
				//unfocus the currently focused element
				if (this.focused != null) {
					this.focused.setFocus(false);
				}
				
				//focus on the new one
				this.focused = obj;
				this.focused.setFocus(true);
				return true;
			}
			else if (mouseResult == IGUIObject.CLICK) { //when a button has been clicked
				if (this.focused != null) this.focused.setFocus(false);
				this.focused = null;
				return true;
			} 
		}
		
		//nothing was clicked, so we unfocus anything that was previously focused
		if (this.focused != null) this.focused.setFocus(false);
		this.focused = null;
		
		return false;
	}
	
	/* when keys have been pressed, they are stored in the char [] chars array and sent to the currently focused UI control */
	public void keyboardSequence (char [] chars) {
		if (this.focused != null && chars != null && chars.length != 0 && chars[0] != '\0') {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : KeyboardBuffer set : " + chars.length + " : " + chars[0]);
			this.focused.keyboardEvent(chars);
		}
	}
	
	/* the click listener of the UI, this is stuff that needs to have connections to the actionqueue for instance */
	@Override
	public void onClick(IGUIObject obj) {
		if (this.workerPanel != null && obj == this.workerPanel.bamulButton) {
			if (UnitSelector.currentlySelected != null && UnitSelector.currentlySelected instanceof Worker)
				openBAMULWindow ((Worker)UnitSelector.currentlySelected);
		}
		else if (this.workerPanel != null && obj == this.workerPanel.pauseButton) {
			if (UnitSelector.currentlySelected != null && UnitSelector.currentlySelected instanceof Worker) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : pauseButton clicked, stopping worker");
				synchronized (this.guiRequestQueue) {
					this.guiRequestQueue.add(new GUIRequest(GUIRequest.GUI_STOP_WORKER, "", ((Worker)UnitSelector.currentlySelected).id));
				}
			}
		}
		else if (this.bamulWindow != null && obj == this.bamulWindow.saveButton) {
			if (UnitSelector.currentlySelected != null && UnitSelector.currentlySelected instanceof Worker) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : bamulWindow button clicked, validating '"+this.bamulWindow.getBAMULString()+"'");
				synchronized (this.guiRequestQueue) {
					//this.guiRequestQueue.add(new GUIRequest(GUIRequest.GUI_UPDATE_BAMUL, this.bamulWindow.getBAMULString(), ((Worker)UnitSelector.currentlySelected).id));	
					this.guiRequestQueue.add(new GUIRequest(GUIRequest.GUI_VALIDATE_BAMUL, this.bamulWindow.getBAMULString(), ((Worker)UnitSelector.currentlySelected).id));
				}
			}
		}
		else if (this.msgBox != null && obj == this.msgBox.closeButton) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : closing MessageBox");
			this.msgBox = null;
		}
		else if (this.settingsWindow != null && this.settingsWindow.saveButton == obj) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : GUIHolder : generating planet");
			synchronized (this.guiRequestQueue) {
				this.guiRequestQueue.add(new GUIRequest(GUIRequest.GUI_GENERATE_SPHERE, "/g "+this.settingsWindow.frequencyBox.getText()+" "+this.settingsWindow.amplitudeBox.getText()+" "+this.settingsWindow.octavesBox.getText()+" "+this.settingsWindow.persistanceBox.getText(), 0));
			}
		}
	}

	@Override
	public void onKeyDown(IGUIObject obj) {
		if (this.console != null && obj == this.console.box) {
			String command = this.console.box.getText();
			
			this.console.box.clear();
			
			System.out.println("GUIHandler Keydown : " + command);
			synchronized (this.guiRequestQueue) {
				this.guiRequestQueue.add(new GUIRequest(GUIRequest.GUI_GENERATE_SPHERE, command, 0));
			}
		}
	}
	
	public void setFPS (float fps) {
		this.debug.setFPS(fps);
	}
}
