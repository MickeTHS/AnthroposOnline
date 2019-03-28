package com.mnn.society.input;

public interface IGUIObject {
	public static int DONT_BOTHER = 0, FOCUS = 1, CLICK = 2;
	
	public void setFocus(boolean status);
	public void addMouseListener(IMouseListener listener);
	public void addKeyboardListener(IKeyboardListener listener);
	public int 	mouseEvent(float x, float y, int button);
	public int 	keyboardEvent(char [] sequence);
}