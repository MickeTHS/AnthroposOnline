package com.mnn.society.game;

import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.gson.Gson;
import com.mnn.society.physics.*;
import com.mnn.society.clientsocket.ClientConnection;
import com.mnn.society.font.Font;
import com.mnn.society.graphics.AlphaTest;
import com.mnn.society.graphics.Planet;
import com.mnn.society.graphics.Shader;
import com.mnn.society.graphics.SkyBox;
import com.mnn.society.gui.GUIHolder;
import com.mnn.society.gui.GUIRequest;
import com.mnn.society.gui.windows.Console;
import com.mnn.society.input.KeyboardBuffer;
import com.mnn.society.server.bamul.utils.Validator;
import com.mnn.society.server.grid.WorkerData;
import com.mnn.society.server.messaging.Request;
import com.mnn.society.server.messaging.RequestType;
import com.mnn.society.server.time.ServerTime;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.units.UnitSelector;
import com.mnn.society.units.Worker;
import com.mnn.society.view.Camera;
import com.mnn.society.view.WorldTranslation;
import com.mnn.society.world.WorldContainer;

public class Game {

	private void initGL(int width, int height) {
    	try {
	    	Display.setDisplayMode(new DisplayMode(width,height));
	    	Display.create();
	    	Display.setVSyncEnabled(false);
	    } catch (LWJGLException e) {
	    	e.printStackTrace();
	    	System.exit(0);
    	}
    	 
    	glEnable(GL_TEXTURE_2D);              
    	 
    	glClearColor(0.337f, 0.478f, 0.714f, 1.0f);         
    	 
    	glDisable(GL_LIGHTING);
    	
    	glViewport(0,0,width,height);
    	glMatrixMode(GL_MODELVIEW);
    	
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
    	glOrtho(0, width, height, 0, 1, -1);
    	glMatrixMode(GL_MODELVIEW);
    	
    	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); 
    	//LIGHTING STUFF
    }
    
	public static final int maxLookUp = 85;
	public static final int maxLookDown = -85;
	
	private KeyboardBuffer keyboard = new KeyboardBuffer();
	
	 //----------- Variables added for Lighting Test -----------//

    private FloatBuffer matSpecular;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight; 
    private FloatBuffer lModelAmbient;

    //----------- END: Variables added for Lighting Test -----------//
	
	private void initLightArrays() {
        matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(1.0f).put(0.0f).put(1.0f).flip();

        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

        lModelAmbient = BufferUtils.createFloatBuffer(4);
        lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();
	}
	
	
	
	public static Vector3f position = new Vector3f(0, -0.5f, 0);
	public static final boolean resizable = true;
	public static int fov = 68;
	
	public static float zNear = 0.3f;
    public static final int gridSize = 10;
    public static final float tileSize = 0.20f;
    public static float zFar = 100f;
    
    private static FloatBuffer perspectiveProjectionMatrix = reserveData(16);
	private static FloatBuffer orthographicProjectionMatrix = reserveData(16);
	
	private FloatBuffer resolution;// = BufferUtils.createFloatBuffer(2);
	
	private Camera camera = new Camera(new Movement(new Vector3f(0f,0f,0f)), new Vector3f(0f,0f,0f));
	
	private WorldTranslation trans;
	
	private static FloatBuffer reserveData(int size) {
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}
	
	private AlphaTest alphaTest;
	
	public Shader dottedLight;
	
	private GUIHolder gui;
	
	private ClientConnection networkConnection;
	
	private Worker [] playerWorkers;
	
	private UnitSelector unitSelector;
	
	private Planet planet;
	
	private SkyBox skybox;
	
	public void start() {
		initGL(1024,600);
		initLightArrays();
		
		float i = 0.56f;
		int index = (int)((i-0.05f)*10.0f);
		float rest = ((float)(i * 10.0f)) - (float)(index);
		
		System.out.println("i : " + i + " index : " + index + " rest : " + rest);
		
		try {
			this.dottedLight = new Shader("light","light");
		} catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : start : light shader exception: " + e);
		}
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : Initializing UnitSelector...");
    	this.unitSelector = new UnitSelector();
		
		this.gui = new GUIHolder();
		//ICOSphere.vdata = PerlinNoise.perlinMesh(ICOSphere.vdata, 0.021f, 0.078f, 4);
		
		this.planet = new Planet();
		this.skybox = new SkyBox();
		Console.log(Console.CONSOLE, "Anthropos Online v.0.042 - planetary version");
		Console.log(Console.CONSOLE, "Working server - client alpha");
		Console.log(Console.CONSOLE, "Developed by MichaelNilssonInc");
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : GAME : network connection establishing...");
		//this.networkConnection = new ClientConnection();
		Logger.log(Logger.LOG_CLIENT, "CLIENT: Game : adding login request to network queue" );
		//create a new user test
		//username, password, color, x, y
		//this.networkConnection.send(new Request(RequestType.USER_CREATE, new Gson().toJson( new String [] { "mickenew", "password", "FFFFFFFF", "10", "10" } ), 0));
		//this.networkConnection.send(new Request(RequestType.LOGIN, new Gson().toJson( new String [] { "mickenew", "password2" } ), 0));

		WorldContainer cont = new WorldContainer(12,10);
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : loading world");
		
		this.resolution = BufferUtils.createFloatBuffer(2);
		this.resolution.clear();
		this.resolution.put(600f).put(800f);
		this.resolution.flip();
		
		this.alphaTest = new AlphaTest(10f, new Vector4f(0.3f, 0.1f, 0.6f, 0.0f));
		
		this.trans = new WorldTranslation(new Vector3f(0f, 0f, 0f), this.resolution);

		this.setUpCamera();
    	
    	while (!Display.isCloseRequested()) {
    		render();
    		checkInput();
    		//this.unitSelector.pick();
    		Display.update();
    		
    		//requests from GUI
    		doActionQueue();
    		
    		//do requests from the network
    		//doNetworkQueue();
    	}
    	
		Display.destroy();
	}
	
	/* stuff happening on the network */
	private void doNetworkQueue () {
		Request req = null;
		
		//get the next request queued up from the server
		if ((req = this.networkConnection.queue.getNextRequest()) != null) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : got a new Request object from server");
			
			if (req.request == RequestType.LOGIN_SUCCESS) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : Successful login");
			}
			else if (req.request == RequestType.WORKER_OWNER_LIST) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : Got a list of workers: " + req.wparam);
				
				WorkerData [] wd = new Gson().fromJson(req.wparam, WorkerData[].class);
				
				this.playerWorkers = new Worker[wd.length];
				
				for (int i = 0; i < wd.length; i++) {
					Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : WorkerData: " + wd[i].id + " " + wd[i].bamul + " creating worker ...");
					this.playerWorkers[i] = new Worker();
					this.playerWorkers[i].setGridPosition(new int [] { wd[i].x, wd[i].y });
					this.playerWorkers[i].setFacing(wd[i].facing);
					this.playerWorkers[i].setBAMULScript(wd[i].bamul);
					this.playerWorkers[i].id = wd[i].id;
					
					this.unitSelector.addSelectableUnit(this.playerWorkers[i]);
				}
			}
			else if (req.request == RequestType.WORKER_ACTION_BEGIN) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : got action begin command");
				
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				//broadcast string [] { id, bamulcommand, actiontype, param1, param2 ... }
				
				//find the worker
				for (int i = 0; i < this.playerWorkers.length; i++) {
					if (this.playerWorkers[i] != null && this.playerWorkers[i].id == Integer.parseInt(data[0])) {
						Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : worker found, processing command '"+data[2]+"'");
						int action = -1;
						if (data[2].trim().equals("move")) {
							Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : movement has been requested");
							this.playerWorkers[i].addAction(Worker.ACTION_MOVE, Integer.parseInt(data[3]), "", Long.parseLong(data[4]), 0L);
							this.playerWorkers[i].text.setText(data[5].trim());
						}
						else if ((action = this.playerWorkers[i].getAction(data[2].trim())) != Worker.ACTION_NO_ACTION) {
							this.playerWorkers[i].addAction(action, 0, "", Long.parseLong(data[4].trim()), Long.parseLong(data[5].trim()));
							this.playerWorkers[i].text.setText(data[2].trim());
							//Integer.toString(w.id), "", w.currentBAMULOpCode, Integer.toString(0), Long.toString(w.departure), Long.toString(w.arrival), Integer.toString(w.x_registry), Integer.toString(w.y_registry)
						}
						else {
							Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_ACTION_BEGIN ERROR : didnt find a proper command for : '"+data[2].trim()+"'");
							this.playerWorkers[i].text.setText("invalid command");
						}
						//this.playerWorkers[i].setRotationAndMovement(Integer.parseInt(data[3]), Long.parseLong(data[4]));
					}
				}
				
				//data[0] //worker id
				//data[1] //bamul
				//data[2] //actiontype, "move"
				//data[3] //completion time (long);
			}
			else if (req.request == RequestType.WORKER_ACTION_COMPLETED) {
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_ACTION_COMPLETED '"+data[0]+"' ");
			}
			else if (req.request == RequestType.WORKER_ABORT_SCRIPT_OK) {
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_ABORT_SCRIPT_OK '"+data[0]+"' ");
				
				this.gui.showMessageBox("Worker script abort request accepted, worker is being stopped. Please hold...", false);
			}
			else if (req.request == RequestType.WORKER_ABORT_SCRIPT_DONE) {
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_ABORT_SCRIPT_DONE '"+data[0]+"' ");
				
				this.gui.showMessageBox("Worker BAMUL script stopped.", true);
				
				for (int i = 0; i < this.playerWorkers.length; i++) {
					if (this.playerWorkers[i] != null && this.playerWorkers[i].id == Integer.parseInt(data[0])) {
						this.playerWorkers[i].text.setText("");
					}
				}
			}
			else if (req.request == RequestType.WORKER_BUSY) {
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_BUSY '"+data[0]+"' ");
				
				this.gui.showMessageBox("Worker already has a BAMUL script running. Abort Worker BAMUL script and try again.", true);
			}
			else if (req.request == RequestType.WORKER_BAMUL_UPDATE_OK) {
				String [] data = new Gson().fromJson(req.wparam, String [].class);
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doNetworkQueue : WORKER_BAMUL_UPDATE_OK '"+data[0]+"'");
				
				this.gui.showMessageBox("BAMUL script successfully uploaded to worker. Script will run shortly.", true);
			}
		}
	}
	
	/* stuff requested to happen through the GUI */
	private void doActionQueue () {
		//get requests that the GUI wants to make
		GUIRequest guiRequest = this.gui.getNextGUIRequest();
		
		if (guiRequest != null) {
			switch (guiRequest.type) {
				case GUIRequest.GUI_UPDATE_BAMUL : //when we want to send a new BAMUL string to the server 
					Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : adding BAMUL request to network thread..." );
					this.networkConnection.send(new Request(RequestType.WORKER_BAMUL_UPDATE, new Gson().toJson( new String [] { Integer.toString(guiRequest.param2), guiRequest.param1 } ), 0));
					break;
				case GUIRequest.GUI_VALIDATE_BAMUL :
					Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : validating BAMUL script '"+guiRequest.param1+"'");
					if (Validator.validBAMULScript(guiRequest.param1)) {
						this.gui.showMessageBox("BAMUL script is valid. Establishing uplink to worker...", false);
						this.networkConnection.send(new Request(RequestType.WORKER_BAMUL_UPDATE, new Gson().toJson( new String [] { Integer.toString(guiRequest.param2), guiRequest.param1 }), 0));
					}
					else {
						this.gui.showMessageBox("BAMUL script is not valid, please see referense at www.bamul.net", true);
					}
					break;
				case GUIRequest.GUI_STOP_WORKER :
					Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : Stopping worker");
					this.gui.showMessageBox("Aborting BAMUL script on worker...", false);
					this.networkConnection.send(new Request(RequestType.WORKER_ABORT_SCRIPT, new Gson().toJson( new String [] { Integer.toString(guiRequest.param2) }), 0));
					break;
				case GUIRequest.GUI_GENERATE_SPHERE :
					try {
						Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : generating perlin sphere");
						
						String [] tokens = guiRequest.param1.split(" "); 
						
						float freq 	= Float.parseFloat(tokens[1]);
						float amp 	= Float.parseFloat(tokens[2]);
						int oct 	= Integer.parseInt(tokens[3]);
						float pers 	= Float.parseFloat(tokens[4]);
						
						this.planet.generateEarth(freq, amp, oct, pers);
						
						Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : frequency: '"+freq+"', amplitude : '"+amp+"', octaves : '"+oct+"', persistance : '"+pers+"'");
					}
					catch (Exception e) {
						Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : doActionQueue : exception when generating planet : " + e);
					}
					break;
				default: break;
			}
		}
	}
	
	private float waterScale = 1f;
	private boolean leftMouseDownState = false, rightMouseDownState = false;
	
	private void checkInput() {
		this.camera.processMouse(1, 80, -80);
		if (!this.gui.lockedKeyboardInput()) this.camera.processKeyboard(16, 0.1f, 0.001f, 0.1f);
		
		if (this.camera.pressedKeys.size() > 0) {
			String key = this.camera.pressedKeys.removeFirst();

			if (key == "T") {
				this.planet.setWaterScale(waterScale+=0.1f);
			}
			else if (key == "G") {
				this.planet.setWaterScale(waterScale-=0.1f);
			}
			else if (key == "Y") {
				this.planet.setWaterScale(waterScale+=0.01f);
			}
			else if (key == "H") {
				this.planet.setWaterScale(waterScale-=0.01f);
			}
			else if (key == "U") {
				this.planet.setZoomFactor(this.planet.getZoomFactor()+0.1f);
			}
			else if (key == "J") {
				this.planet.setZoomFactor(this.planet.getZoomFactor()-0.1f);
			}
			
			System.out.println("waterscale: " + waterScale);
			
			/*if (key == "T") {
				this.work.setRotationAndMovement(TheGrid.GRID_NORTH, ServerTime.getSyncedTimeInMillis()+1000);
			}
			else if (key == "F") {
				this.work.setRotationAndMovement(TheGrid.GRID_WEST, ServerTime.getSyncedTimeInMillis()+1000);
			}
			else if (key == "G") {
				this.work.setRotationAndMovement(TheGrid.GRID_SOUTH, ServerTime.getSyncedTimeInMillis()+1000);
			}
			else if (key == "H") {
				this.work.setRotationAndMovement(TheGrid.GRID_EAST, ServerTime.getSyncedTimeInMillis()+1000);
			}*/
		}

		this.keyboard.doSequence();
		
		for (int i = 0; i < this.keyboard.buffer.length; i++) {
			if (this.keyboard.buffer[i] != '\0')
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Game : checkInput : keyboard buffer : " + keyboard.buffer[i]);
			//this.keyboard.buffer[i] = '\0';
		}
		
		this.gui.keyboardSequence(this.keyboard.buffer);
		
		this.keyboard.clear();
		
		//we need leftMouseDownState to make sure we only call this ONCE per click 
		if (!leftMouseDownState && Mouse.isButtonDown(0)) {
			//Mouse.setGrabbed(true);
			leftMouseDownState = true;
			
			//if mouseClick returns true, then the GUI intercepted a click
			//so only select units if we didnt click the GUI
			if (!this.gui.mouseClick(Mouse.getX(), Display.getHeight() - Mouse.getY(), 0)) {
				this.unitSelector.pick();
			}
		}
		else if (leftMouseDownState && !Mouse.isButtonDown(0))
			leftMouseDownState = false;
		if (!rightMouseDownState && Mouse.isButtonDown(1)) {
			Mouse.setGrabbed(true);
			rightMouseDownState = true;
		}
		else if (rightMouseDownState && !Mouse.isButtonDown(1)) {
			rightMouseDownState = false;
			Mouse.setGrabbed(false);
		}
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable (GL_DEPTH_TEST);
		glLoadIdentity();
		/* draw the world */
		this.camera.applyModelviewRotation(true);
		this.trans.box.render();
		this.camera.applyModelviewPosition();
		
		long time = ServerTime.getSyncedTimeInMillis();
		//for (int i = 0; i < 81; i++) {
			/*if (this.dottedLight != null) this.dottedLight.preRender();
			
			int time_loc = ARBShaderObjects.glGetUniformLocationARB(this.dottedLight.programId, "time");
			ARBShaderObjects.glUniform1fARB(time_loc, time);
			
			int resolution = ARBShaderObjects.glGetUniformLocationARB(this.dottedLight.programId, "resolution");
			if (resolution == -1) Logger.log(Logger.LOG_CLIENT, "couldnt bind uniform resolution");
			ARBShaderObjects.glUniform2ARB(resolution, this.resolution);
			
			/*int resolution_x = ARBShaderObjects.glGetUniformLocationARB(this.dottedLight.programId, "resolution_x");
			ARBShaderObjects.glUniform1fARB(resolution_x, 600f);
			int resolution_y = ARBShaderObjects.glGetUniformLocationARB(this.dottedLight.programId, "resolution_y");
			ARBShaderObjects.glUniform1fARB(resolution_y, 800f);
*/
			//this.structures[i].render();
			
			//Shader.clearAllShaders();
		//}
		
		//this.skybox.render();
		
		glPushMatrix();
			glTranslatef(0f,0f,0f);
			glEnable(GL_LIGHT0);
			glLight(GL_LIGHT0, GL_POSITION, this.lightPosition);
		
			glPushMatrix();
				glTranslatef(-1f, -2f, 0f);
				this.planet.render();
			glPopMatrix();
		glPopMatrix();
		
		glEnable(GL_LIGHTING);
		
		/*if (this.playerWorkers != null) {
			for (Worker w : this.playerWorkers) {
				w.render(time);
			}
		}*/
		
		//this.alphaTest.render(time, resolution);
		
		glCullFace(GL_FRONT_AND_BACK);
		//glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		//glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		
		//glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		
		unitSelector.update();
		unitSelector.render();
		
		
		//render all the floating texts for the workers
		if (this.playerWorkers != null) {
			for (Worker w : this.playerWorkers) {
				glPushMatrix();
				if (w.text != null) w.text.render();
				glPopMatrix();
			}
		}
		
		/* change view from 3D to 2D */
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(orthographicProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		
		this.gui.renderAll();

		glPopMatrix();
		
		glEnable(GL_NORMALIZE);
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		
		updateFPS();
	}
	
	
	private void setUpCamera() {
		this.camera.aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		this.camera.fov = 90;
		this.camera.applyProjectionMatrix();
		
		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW_MATRIX);
	}
	
	
	private int fps;
    private long lastFPS = ServerTime.getSyncedTimeInMillis();
    private long lastFrame;
	
	public void updateFPS() {
		if (ServerTime.getSyncedTimeInMillis() - lastFPS > 1000) {
			this.gui.setFPS(fps);
			fps = 0;
	        lastFPS += 1000;
		}
	    fps++;
	}
	
	
	public static void main (String[] argv) {
		Game game = new Game();
		game.start();
	}
}
