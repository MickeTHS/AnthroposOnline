package com.mnn.society.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.physics.Movement;
import com.mnn.society.server.time.ServerTime;
import com.mnn.society.server.utils.Logger;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
	public Vector3f rotation;
	public Movement mov;

	public float fov = 90;
    public float aspectRatio;
    protected float zNear = 0.1f;
    protected float zFar = 20f;

    public LinkedList<String> pressedKeys = new LinkedList<String>();
    
    public Camera(Movement mov, Vector3f rot) {
    	this.mov = mov;
    	this.rotation = rot;
    }
    
    public void processMouse(float mouseSpeed, float maxLookUp, float maxLookDown) {
    	if (!Mouse.isGrabbed()) return;
    	float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
		float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
		if (this.rotation.y + mouseDX >= 360) {
			this.rotation.y = this.rotation.y + mouseDX - 360;
		} else if (this.rotation.y + mouseDX < 0) {
			this.rotation.y = 360 - this.rotation.y + mouseDX;
		} else {
			this.rotation.y += mouseDX;
		}
		if (this.rotation.x - mouseDY >= maxLookDown
				&& this.rotation.x - mouseDY <= maxLookUp) {
			this.rotation.x += -mouseDY;
		} else if (this.rotation.x - mouseDY < maxLookDown) {
			this.rotation.x = maxLookDown;
		} else if (this.rotation.x - mouseDY > maxLookUp) {
			this.rotation.x = maxLookUp;
		}
    }
    
    /* Use this one for picking tests! */
    public static FloatBuffer get3DMousePosition () {
    	int mouseX = Mouse.getX();
	 	int mouseY = Mouse.getY();
	 	
	 	IntBuffer viewport = BufferUtils.createIntBuffer(16); 
	 	FloatBuffer modelview = BufferUtils.createFloatBuffer(16); 
	 	FloatBuffer projection = BufferUtils.createFloatBuffer(16); 
	 	FloatBuffer winZ = BufferUtils.createFloatBuffer(1); 
	 	float winX, winY; 
	 	FloatBuffer position = BufferUtils.createFloatBuffer(3); 
	 	glGetFloat( GL_MODELVIEW_MATRIX, modelview ); 
	 	glGetFloat( GL_PROJECTION_MATRIX, projection ); 
	 	glGetInteger( GL_VIEWPORT, viewport ); 
	 	winX = (float)mouseX; 
	 	//winY = (float)viewport.get(3) - (float)mouseY;
	 	winY = (float)mouseY;
	 		 	
	 	glReadPixels(mouseX, (int)winY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ); 
	 	GLU.gluUnProject(winX, winY, winZ.get(), modelview, projection, viewport, position); 
	 	
	 	return position;
    }
    
    /* If you get a DirectBuffer exception when reading the position from the FloatBuffer, just use 
    position.get(0) for X, 
    position.get(1) for Y and 
    position.get(2) for Z. Enjoy!*/
	public static FloatBuffer getMousePosition() { 
		int mouseX = Mouse.getX();
	 	int mouseY = Mouse.getY();
	 	
	 	IntBuffer viewport = BufferUtils.createIntBuffer(16); 
	 	FloatBuffer modelview = BufferUtils.createFloatBuffer(16); 
	 	FloatBuffer projection = BufferUtils.createFloatBuffer(16); 
	 	FloatBuffer winZ = BufferUtils.createFloatBuffer(1); 
	 	float winX, winY; 
	 	FloatBuffer position = BufferUtils.createFloatBuffer(3); 
	 	glGetFloat( GL_MODELVIEW_MATRIX, modelview ); 
	 	glGetFloat( GL_PROJECTION_MATRIX, projection ); 
	 	glGetInteger( GL_VIEWPORT, viewport ); 
	 	winX = (float)mouseX; 
	 	//winY = (float)viewport.get(3) - (float)mouseY;
	 	winY = (float)mouseY;
	 		 	
	 	glReadPixels(mouseX, (int)winY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ); 
	 	GLU.gluUnProject(winX, winY, winZ.get(), modelview, projection, viewport, position); 
	 	
	 	FloatBuffer mouseBuffer = BufferUtils.createFloatBuffer(2);
	 	mouseBuffer.clear();
	 	mouseBuffer.put(winX).put(winY);
	 	mouseBuffer.flip();
	 	
	 	return mouseBuffer; 
	}
	
	private long cooldown = ServerTime.getSyncedTimeInMillis();
	
    public void processKeyboard(int delta, float speedX, float speedY, float speedZ) {
    	boolean keyT = Keyboard.isKeyDown(Keyboard.KEY_T);
    	boolean keyF = Keyboard.isKeyDown(Keyboard.KEY_F);
    	boolean keyG = Keyboard.isKeyDown(Keyboard.KEY_G);
    	boolean keyH = Keyboard.isKeyDown(Keyboard.KEY_H);
    	boolean keyY = Keyboard.isKeyDown(Keyboard.KEY_Y);
    	boolean keyU = Keyboard.isKeyDown(Keyboard.KEY_U);
    	boolean keyJ = Keyboard.isKeyDown(Keyboard.KEY_J);
    	
    	
    	if ((keyT || keyF || keyG || keyH || keyY || keyU || keyJ) && (cooldown + 200 < ServerTime.getSyncedTimeInMillis())) {
	    	
    		Logger.log(Logger.LOG_CLIENT, "keypress event");
    		
	    	if (keyT) this.pressedKeys.add("T");
	        if (keyF) this.pressedKeys.add("F");
	        if (keyG) this.pressedKeys.add("G");
	        if (keyH) this.pressedKeys.add("H");
	        if (keyY) this.pressedKeys.add("Y");
	        if (keyU) this.pressedKeys.add("U");
	        if (keyJ) this.pressedKeys.add("J");
	        cooldown = ServerTime.getSyncedTimeInMillis();
    	}
    	
    	boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        
        if (keyUp || keyDown || keyLeft || keyRight)
        	this.mov.move(this.rotation, keyUp, keyDown, keyLeft, keyRight, speedX);
        else 
        	this.mov.stop();
        
        //if (keyT) Console.log(Console.CONSOLE, "T has been pressed");
        
        if (flyUp && !flyDown) {
			this.mov.pos.y += speedY * delta;
		}
		if (flyDown && !flyUp) {
			this.mov.pos.y -= speedY * delta;
		}
		
		/*if (counter++ > 100) {
			System.out.println("world pos: " + this.mov.pos);
			counter = 0;
		}*/
    }
    
    public void moveFromLook(float dx, float dy, float dz) {
    	float nX = this.mov.pos.x;
    	float nY = this.mov.pos.y;
    	float nZ = this.mov.pos.z;
    	
    	float hypotenuseX = dx;
    	float adjacentX = hypotenuseX * (float) Math.cos(Math.toRadians(this.rotation.x - 90));
    	float oppositeX = (float) Math.sin(Math.toRadians(this.rotation.x - 90)) * hypotenuseX;
    	nZ += adjacentX;
    	nX -= oppositeX;
    	
    	nY += dy;
    	
    	float hypotenuseZ = dz;
    	float adjacentZ = hypotenuseZ * (float) Math.cos(Math.toRadians(this.rotation.x));
    	float oppositeZ = (float) Math.sin(Math.toRadians(this.rotation.x)) * hypotenuseZ;
    	nZ += adjacentZ;
    	nX -= oppositeZ;
    	
    	this.mov.pos.x = nX;
    	this.mov.pos.y = nY;
    	this.mov.pos.z = nZ;
    }
    
    public void moveAlongAxis(float magnitude, float x, float y, float z) {
    	this.mov.pos.x += x * magnitude;
    	this.mov.pos.y += y * magnitude;
    	this.mov.pos.z += z * magnitude;
    	//System.out.println(this.x + ", " + this.y + ", " + this.z);
    }
    
    public void setPosition(float x, float y, float z) {
    	this.mov.pos.x = x;
    	this.mov.pos.y = y;
    	this.mov.pos.z = z;
    }

    public void applyProjectionMatrix() {
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
    	GLU.gluPerspective(45.0f, 1024.0f/600.0f, 0.1f, 1000f);
    	glMatrixMode(GL_MODELVIEW);
    }
    
    public void applyModelviewPosition() {
    	glTranslatef(mov.pos.x, mov.pos.y, mov.pos.z);
    }
    
    public void applyModelviewRotation(boolean resetMatrix) {
    	if (resetMatrix) glLoadIdentity();
    	glRotatef(rotation.x, 1, 0, 0);
    	glRotatef(rotation.y, 0, 1, 0);
    	glRotatef(rotation.z, 0, 0, 1);
    }
}