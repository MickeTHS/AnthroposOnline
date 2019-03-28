package com.mnn.society.input;

import org.lwjgl.input.Keyboard;

import com.mnn.society.server.time.ServerTime;

/**
 * THIS is the actual keyboard handler. The difference from KeyboardStream is that this one: 
 * 1) works
 * ....
 * 
 * thats all i have to say about that
 * @author mickeman
 *
 */
public class KeyboardBuffer {
	public char [] buffer = new char [256];
	public int index = 0;
	
	private long wait = 0L;
	public boolean shiftstate = false;
	public boolean ctrlstate = false;
	public boolean altstate = false;
	public boolean pressed = false;
	public char current = '\0';
	
	public KeyboardBuffer () {
		for (int i = 0; i < buffer.length; i++) {
			this.buffer[i] = '\0';
		}
	}
	
	public void startSequence() {}
	public void stopSequence() {}
	
	public void clear() {
		for (int i = 0; i < this.buffer.length; i++) {
			this.buffer[i] = '\0';
			this.index = 0;
		}
	}
	
	public void doSequence() {
		while (Keyboard.next()) {
			//int k = Keyboard.getEventKey(); 
			//char c = '\0';
			
			char e = Keyboard.getEventCharacter();
			
			if (Keyboard.getEventKeyState()) { //when pressed
				if (Keyboard.getEventKey() == Keyboard.KEY_BACK) e = 8;
				
				this.buffer[index++] = e;
				this.current = e;
				this.pressed = true;
				this.wait = ServerTime.getSyncedTimeInMillis()+700;
				
			}
			else { //when released
				this.current = '\0';
				this.pressed = false;
			}
		}
		
		if (this.wait < ServerTime.getSyncedTimeInMillis()) {
			this.wait = ServerTime.getSyncedTimeInMillis()+50;
			
			if (pressed && this.current != '\0')
				this.buffer[index++] = this.current;
		}
	}
}
