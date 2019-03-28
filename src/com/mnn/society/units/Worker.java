package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;
import java.io.IOException;
import java.util.LinkedList;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.math.VectorMath;
import com.mnn.society.md3.ModelQuake3;
import com.mnn.society.server.time.ServerTime;
import com.mnn.society.server.utils.Calculation;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.world.TheGrid;
import com.mnn.society.graphics.FloatingText;
import com.mnn.society.graphics.HeightMap;
import com.mnn.society.graphics.Picker;
import com.mnn.society.graphics.Shader;
import com.mnn.society.graphics.textures.TextureLoader;

public class Worker implements ISelectable {
	public static final int ACTION_MOVE 		= 1;
	public static final int ACTION_DRILL 		= 2;
	public static final int ACTION_REPAIR 		= 3;
	public static final int ACTION_A_ANALYZE 	= 4;
	public static final int ACTION_B_ANALYZE 	= 5;
	public static final int ACTION_PICKUP 		= 6;
	public static final int ACTION_MOVE_ITEM 	= 7;
	public static final int ACTION_CONSTRUCTION = 8;
	public static final int ACTION_NO_ACTION 	= -1;
	
	private class Action { //a private class only to keep track of what work the worker has queued up
		public int 		action;
		public String 	sparam;
		public int 		iparam;
		public float 	fparam;
		public long 	starttime;
		public long 	endtime;
		
		public Action (int action, String sparam, int iparam, long starttime, long endtime) {
			this.action 	= action;
			this.sparam 	= sparam;
			this.iparam 	= iparam;
			this.starttime 	= starttime;
			this.endtime 	= endtime;
		}
	}
	
	public static ModelQuake3 workerModel 	= null; //a static model will not work if were using lots of animations! NEED TO CHANGE THIS! really? not sure i need to change it actually
	private ModelQuake3 drillModel			= null;
	private ModelQuake3 scannerModel		= null;
	
	public static final float width 		= 4f;
	public static final float depth 		= 2f;
	public static final float height 		= 10f;
	private LinkedList<Action> actionQueue 	= new LinkedList<Action>();
	public static final float y_offset 		= 1.9f;
	public static final float x_offset 		= HeightMap.segmentSize/2f;
	public static final float z_offset 		= HeightMap.segmentSize/2f;
	public static final float [] gridLookups = new float [] { 180f, 90f, 0f, 270f };
	public Vector3f position 				= new Vector3f(); //the actual position in 3D space
	public int [] coordinates 				= new int [] { 0, 0 }; //the actual coordinates in grid space
	private int facing 						= TheGrid.GRID_WEST; //the direction the worker is currently facing
	private long [] rotationTimestamps 		= new long [] { 0L, 0L }; //first element is start time of rotation, second is end time of rotation
	private long [] movementTimestamps 		= new long [] { 0L, 0L }; //first element is start time of movement, second is end time of movement
	private boolean doRotation 				= false; //if true, will rotate the worker
	private boolean doMovement 				= false; //if true, will move the worker
	private float [] rotationAngles 		= new float [] { 0f, 0f }; //the angles of rotation, start, end
	private float currentRotationY 			= Worker.gridLookups[TheGrid.GRID_WEST]; //the current rotation angle of the worker
	private Vector3f [] movementVectors; 	//the start movement, and end movement
	private int [] targetCoordinates; 		//the goal grid
	private int heading 					= -1;
	private int count 						= 0; //just a lil counter, not vital
	private boolean selected 				= false; //if the worker is selected or not
	public int id;
	public FloatingText text;
	private boolean	working					= false;
	//private long animationCompleteTime		= 0L;
	private String bamulScript				= "";
	private boolean done					= true;
	
	private Shader alphaShader				= null;
	private Vector4f color					= new Vector4f(0.3f, 0.2f, 0.8f, 0f);
	private int textureId					= 0;
	
	public Worker () {
		
		this.textureId = TextureLoader.loadSharedTexture("rob\\robot_texture.png");
		
		//load shaders
		try {
			this.alphaShader = new Shader("alphacolor", "alphacolor");
		}
		catch (Exception e) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : unable to create shader for alphatest: " + e);
		}
		
		//load the utils, drills and stuff
		try {
			this.drillModel = new ModelQuake3();
			this.drillModel.load("drill", "drill");
			this.drillModel.setTorsoAnimation("TORSO_GET_DRILL");
			this.drillModel.setNextAnimation("TORSO_RAISE");
		}
		catch (IOException ioe) {
			System.out.println("CLIENT : Worker : unable to load drill model");
		}
		
		try {
			this.scannerModel = new ModelQuake3();
			this.scannerModel.load("scanner", "scanner");
			this.scannerModel.setTorsoAnimation("TORSO_DROP");
		}
		catch (IOException ioe) {
			System.out.println("CLIENT : Worker : unable to load scanner model");
		}
		
		//load the actual model
		if (Worker.workerModel == null) { //singleton workerModel
			try {
				Worker.workerModel = new ModelQuake3();
				Worker.workerModel.load("rob", "rob");
				//Worker.workerModel.loadWeapon("rob", "drill");
				Worker.workerModel.setTorsoAnimation("TORSO_GET_DRILL");
				Worker.workerModel.setNextAnimation("TORSO_RAISE");
				//Worker.workerModel.setLegsAnimation("LEGS_RUN");
				//Worker.workerModel.setWeaponAnimation("WEAP_FETCH");
			} catch (IOException e) {
				System.out.println("CLIENT : Worker : Unable to load workerModel MD3 format : " + e);
			}
		}
		
		this.text = new FloatingText(" ", new Vector4f(0.15f, 0.95f, 0.26f, 0f));
		this.text.setOrigin(this);
	}
	
	/* set the BAMUL script */
	public void setBAMULScript (String bamul) {
		this.bamulScript = bamul;
	}
	
	/* get the BAMUL script */
	public String getBAMULScript () {
		return this.bamulScript;
	}
	
	/* just a small check to see if the opCode came through in a good way */
	public int getAction (String opCode) {
		switch (opCode) {
			case "A" : return Worker.ACTION_A_ANALYZE;
			case "B" : return Worker.ACTION_B_ANALYZE;
			case "D" : return Worker.ACTION_DRILL;
			case "P" : return Worker.ACTION_PICKUP;
			case "M" : return Worker.ACTION_MOVE_ITEM;
			case "C" : return Worker.ACTION_CONSTRUCTION;
			case "R" : return Worker.ACTION_REPAIR;
			default : return Worker.ACTION_NO_ACTION;
		}
	}
	
	/* the only way the worker will visibly do something is if an action has been added to the action queue */
	public void addAction (int action, int iparam, String sparam, long starttime, long endtime) {
		this.actionQueue.add(new Action(action, sparam, iparam, starttime, endtime));
	}
	
	/* set the facing of the Worker */
	public void setFacing (int facing) {
		this.facing = facing;
		this.currentRotationY = Worker.gridLookups[facing];
	}
	
	/* will translate the grid position to the vector position */
	public void setGridPosition (int [] pos) {
		this.coordinates = pos;
		this.position = Worker.addOffsets(TheGrid.coordinatesToVector(pos));
	}
	
	/* give this function the direction and at what time it will start, and the rest is done automatically */
	private void setRotationAndMovement (int direction, long startTimestamp) {
		System.out.println("CLIENT : Worker : setRotationAndMovement : facing '"+this.facing+"' heading: '"+direction+"' ");
		
		this.heading = direction;
		
		//determine the start and end angles
		this.rotationAngles = new float [] { Worker.gridLookups[this.facing], Worker.gridLookups[direction] };
		
		//do some special cases
		if (TheGrid.GRID_SOUTH == this.facing && TheGrid.GRID_WEST == direction) {
			this.rotationAngles[0] = 360f;
		}
		else if (TheGrid.GRID_WEST == this.facing && TheGrid.GRID_SOUTH == direction) {
			this.rotationAngles[1] = 360f;
		}
		
		this.rotationTimestamps = Calculation.rotationTimeStamps(startTimestamp, Calculation.WORKER_ROTATION_TIME, this.facing, direction);
		
		if (this.rotationTimestamps == null) {
			this.rotationTimestamps = new long [] { 0L, 0L };
			this.doRotation = false;
		}
		else {
			this.doRotation = true;
		}
		
		//determine the movement timestamps
		this.movementTimestamps = Calculation.movementTimeStamps(this.doRotation, this.rotationTimestamps[1], startTimestamp, Calculation.WORKER_MOVEMENT_TIME);
		this.doMovement = true;
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : setRotationAndMovement : movement from grid '"+this.coordinates[0]+"','"+this.coordinates[1]+"' : Vector3f : " + this.position);
		
		//lets figure out the target grid
		int [] target = new int [] {
				direction == TheGrid.GRID_EAST ? this.coordinates[0]-1 : direction == TheGrid.GRID_WEST ? this.coordinates[0]+1 : this.coordinates[0],
				direction == TheGrid.GRID_NORTH ? this.coordinates[1]+1 : direction == TheGrid.GRID_SOUTH ? this.coordinates[1]-1 : this.coordinates[1]
		};
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : setRotationAndMovement : to grid : '" + target[0] + "','"+target[1]+"' : " + Worker.addOffsets(TheGrid.coordinatesToVector(target)));
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : setRotationAndMovement : '"+this.rotationTimestamps[0]+"', '"+this.rotationTimestamps[1]+"', '"+this.movementTimestamps[0]+"', '"+this.movementTimestamps[1]+"'");
		
		//set 'start' vector and 'target' vector
		this.movementVectors = new Vector3f [] { new Vector3f(this.position), Worker.addOffsets(TheGrid.coordinatesToVector(target)) };
		this.targetCoordinates = target; //we need this to tell us what the actual coordinates will be in the end
		this.done = false;
	}
	
	/* this is to place the Worker smack in the middle of the square and not on the edge, this assumes that the Vector3f coming in is on the edge of course */
	public static Vector3f addOffsets (Vector3f pos) {
		return new Vector3f ((pos.x + Worker.x_offset), (pos.y) + Worker.y_offset, (pos.z + Worker.z_offset));
	}
	
	/* make sure we dont rotate ourselves to death */
	public void resetRotation () {
		this.currentRotationY 	= Worker.gridLookups[this.heading]; //set the rotation to what its suppose to be in the end
		this.facing 			= this.heading;
		this.heading 			= TheGrid.GRID_NO_DIR;
		this.doRotation 		= false;
		this.rotationAngles 	= new float [] { 0f, 0f };
		this.rotationTimestamps = new long [] { 0L, 0L };
	}
	
	/* make sure we're right where were suppose to be and not moving further */
	public void resetMovement () {
		this.doMovement 		= false;
		this.movementTimestamps = new long [] { 0L, 0L };
		this.coordinates 		= this.targetCoordinates; //set to destination
		this.position 			= this.movementVectors[1]; //set to destination
		this.movementVectors 	= null;
		this.targetCoordinates 	= null;
		this.count 				= 0;
		this.done 				= true;
	}
	
	public void updateRotationAndMovement (long time) {
		/* DONT TOUCH! WORKS! */
		if (this.doRotation && time >= this.rotationTimestamps[0] ) {
			
			float destmul = (float)((float)(time - this.rotationTimestamps[0]) / (float)(this.rotationTimestamps[1]-this.rotationTimestamps[0]));
			float locmul = (float)((float)(this.rotationTimestamps[1] - time) / (float)(this.rotationTimestamps[1]-this.rotationTimestamps[0]));
			
			this.currentRotationY = this.rotationAngles[1] * destmul + this.rotationAngles[0] * locmul;
			
			if (this.rotationTimestamps[1] <= time) {
				resetRotation();
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : done rotating ");
			}
		} /* WORKS AS WELL! HANDS OFF! */
		else if (this.doMovement && time >= this.movementTimestamps[0] ) {
			
			float destmul = (float)((float)(time - this.movementTimestamps[0]) / (float)(this.movementTimestamps[1]-this.movementTimestamps[0]));
			float locmul = (float)((float)(this.movementTimestamps[1] - time) / (float)(this.movementTimestamps[1]-this.movementTimestamps[0]));
			
			if (count++ % 200 == 0) Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : movement tween : " + (this.movementVectors[1].x * destmul + this.movementVectors[0].x * locmul) + " : " + destmul + " : " + locmul);
			
			this.position.x = this.movementVectors[1].x * destmul + this.movementVectors[0].x * locmul;
			this.position.y = this.movementVectors[1].y * destmul + this.movementVectors[0].y * locmul;
			this.position.z = this.movementVectors[1].z * destmul + this.movementVectors[0].z * locmul;
			
			if (this.movementTimestamps[1] <= time) {
				resetMovement();
				
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : done moving, current grid : '" + this.coordinates[0] + "', '" + this.coordinates[1] + "'");
			}
		}
	}
	
	/* i really need to look at that scaling shit, i dont need this function!!! */
	public Vector3f getRealPosition (float xo, float yo, float zo) {
		return new Vector3f (this.position.x + xo, this.position.y + yo, this.position.z + zo);
	}
	
	/* call this to set the next worker animations */
	private void runNextAction () {
		if (!this.done || this.actionQueue.size() == 0) return; //if theres no action then we dont do anything
		Action act = this.actionQueue.removeFirst();
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : runNextAction : time now : '"+ServerTime.getSyncedTimeInMillis()+"'");
		
		if (act != null) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : runNextAction : running the next action");
			if (act.action == Worker.ACTION_MOVE) {
				act.starttime = ServerTime.getSyncedTimeInMillis() + 200;
				setRotationAndMovement(act.iparam, act.starttime);
			}
		}
	}
	
	/* the render function, call this every game loop */
	public void render (long time) {
		if (!this.working) {
			runNextAction();
		}
		
		updateRotationAndMovement(time);
		
		if (this.alphaShader != null) { 
			this.alphaShader.preRender();
			
			int my_color = ARBShaderObjects.glGetUniformLocationARB(this.alphaShader.programId, "my_color");
			if (my_color < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : my_color failed"); }
			ARBShaderObjects.glUniform4fARB(my_color, this.color.x, this.color.y, this.color.z, this.color.w);
			
			int sampler01 = ARBShaderObjects.glGetUniformLocationARB(this.alphaShader.programId, "my_color_texture");
			if(sampler01 < 0) { Logger.log(Logger.LOG_CLIENT, "CLIENT : AlphaTest : sampler01 failed"); }
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
			ARBShaderObjects.glUniform1iARB(sampler01, 0);
		}
		
		glPushMatrix();
			//glScalef(0.1f, 0.1f, 0.1f); //ugh! dont like this scaling, we need to export it as proper size from blender.
    		glTranslatef(this.position.x, this.position.y, this.position.z);
    		glRotatef(this.currentRotationY, 0, 1, 0);
    		
    		glPushMatrix();
    			this.scannerModel.draw();
    		glPopMatrix();
    		
    		glPushMatrix();
    			Worker.workerModel.draw();
    		glPopMatrix();
    		
    		glPushMatrix();
    			this.drillModel.draw();
    		glPopMatrix();
		glPopMatrix();
		
		Shader.clearAllShaders();
		
		//Render the BoundingBox just to see that its the proper size, comment this out when live
		/*Vector3f [] bb = getBoundingBox();
		glPushMatrix();
			glBegin(GL_QUADS);
				glVertex3f(bb[0].x, bb[0].y, bb[0].z);
				glVertex3f(bb[1].x, bb[1].y, bb[1].z);
				glVertex3f(bb[3].x, bb[3].y, bb[3].z);
				glVertex3f(bb[2].x, bb[2].y, bb[2].z);
				
				glVertex3f(bb[0].x, bb[0].y, bb[0].z);
				glVertex3f(bb[4].x, bb[4].y, bb[4].z);
				glVertex3f(bb[5].x, bb[5].y, bb[5].z);
				glVertex3f(bb[1].x, bb[1].y, bb[1].z);
				
				glVertex3f(bb[3].x, bb[3].y, bb[3].z);
				glVertex3f(bb[7].x, bb[7].y, bb[7].z);
				glVertex3f(bb[6].x, bb[6].y, bb[6].z);
				glVertex3f(bb[2].x, bb[2].y, bb[2].z);
				
				glVertex3f(bb[4].x, bb[4].y, bb[4].z);
				glVertex3f(bb[5].x, bb[5].y, bb[5].z);
				glVertex3f(bb[7].x, bb[7].y, bb[7].z);
				glVertex3f(bb[6].x, bb[6].y, bb[6].z);
				
			glEnd();
		glPopMatrix();
		*/
	}

	/* gets a bounding box translated to the position */
	public Vector3f [] getBoundingBox () {
		//return convert(new Vector3f [] {
		return new Vector3f [] {
			new Vector3f(this.position.x - Worker.width/2, this.position.y - Worker.height/2, this.position.z - Worker.width/2),
			new Vector3f(this.position.x + Worker.width/2, this.position.y - Worker.height/2, this.position.z - Worker.width/2),
			new Vector3f(this.position.x - Worker.width/2, this.position.y - Worker.height/2, this.position.z + Worker.width/2),
			new Vector3f(this.position.x + Worker.width/2, this.position.y - Worker.height/2, this.position.z + Worker.width/2),
			
			new Vector3f(this.position.x - Worker.width/2, this.position.y + Worker.height/2, this.position.z - Worker.width/2),
			new Vector3f(this.position.x + Worker.width/2, this.position.y + Worker.height/2, this.position.z - Worker.width/2),
			new Vector3f(this.position.x - Worker.width/2, this.position.y + Worker.height/2, this.position.z + Worker.width/2),
			new Vector3f(this.position.x + Worker.width/2, this.position.y + Worker.height/2, this.position.z + Worker.width/2),
		};
	}
	
	/* check if any point of the Picker is inside the bounding box */
	@Override
	public boolean checkPicker (Picker pick) {
		Vector3f [] bbPick = pick.getBoundingBox();
		
		//check if any of the picks vectors are inside our bounding box
		for (int i = 0; i < bbPick.length; i++) {
			Vector3f v = bbPick[i];
			if (v.x >= (this.position.x - Worker.width/2)   	&& v.x <= (this.position.x + Worker.width/2)   && 
				v.y >= (this.position.y - Worker.height/2)  	&& v.y <= (this.position.y + Worker.height/2)  &&
				v.z >= (this.position.z - Worker.width/2)   	&& v.z <= (this.position.z + Worker.width/2)   ) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : Worker : successful pick on worker");
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void setSelected (boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean getSelected () {
		return this.selected;
	}

	@Override
	public Vector3f getPosition () {
		return getRealPosition(0f, 0.3f, 0f);
	}
}