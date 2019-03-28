package com.mnn.society.view;

import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;
import com.mnn.society.graphics.HeightMap;
import com.mnn.society.graphics.SkyBox;
import com.mnn.society.server.time.ServerTime;

public class WorldTranslation {
	public static float JUMP_FORCE = 3f;
	public static float HORIZON = -0.5f;
	public boolean flying;
	public float speed;
	public float verticalSpeed;
	public long lastTime = 0L;
	public long lastJumpTime = 0L;
	public float height;
	public boolean jumping;
	public Vector3f pos;
	public List<HeightMap> maps = new ArrayList<HeightMap>();
	public FloatBuffer resolution;
	
	public SkyBox box;
	
	public WorldTranslation(Vector3f initialPosition, FloatBuffer resolution) { 
		this.pos = initialPosition;
		this.height = 0f;
		this.speed = 0.001f;
		this.flying = false;
		this.jumping = false;
		this.resolution = resolution;
		this.box = new SkyBox();
		this.box.position = this.pos;
	}
	
	/* call this when stopping MOVEMENT */
	public void stop() {
		this.lastTime = 0L;
	}
	
	public void addMapAt(HeightMap map, float x, float y) {
		this.maps.add(map);
	}
	
	public void render(float time, Vector3f newpos) {
		this.box.position = newpos;
		this.box.render();
		/*FloatBuffer mousePos = Camera.getMousePosition();
		
		for (HeightMap m : this.maps) {
			glPushMatrix();
			//glTranslatef((this.pos.x + (m.pos.x*HeightMap.segmentSize)), this.pos.y, (this.pos.z + m.pos.y*HeightMap.segmentSize));
			glTranslatef(((m.pos.x*HeightMap.segmentSize)), 0f, (m.pos.y*HeightMap.segmentSize));
			m.render(mousePos, time, this.resolution);
			glPopMatrix();
		}*/
		//glLoadIdentity();
        //glTranslatef(this.pos.x, this.pos.y, this.pos.z);
	}
	
	public static int counter = 0;
	
	/* call this every rendering cycle to render movement */
	public void move(Vector3f direction, boolean forward, boolean backward, boolean left, boolean right, boolean up, boolean down) {
		if(lastTime == 0f) lastTime = ServerTime.getSyncedTimeInMillis();
		long now = ServerTime.getSyncedTimeInMillis();
		
		float delta = now - lastTime;
		
		float angle = 
				forward 	&& !left 	&& !right 	? direction.y :
				forward 	&& right 	&& !left 	? direction.y + 45 :
				forward 	&& left 	&& !right 	? direction.y - 45 :
				backward 	&& !left 	&& !right 	? direction.y - 180 :
				backward 	&& right	&& !left 	? direction.y - 225 :
				backward 	&& left 	&& !right 	? direction.y - 135 :
				left 		&& !forward ? direction.y - 90 :
				right 		&& !forward ? direction.y + 90 :
				0;
        
		Vector3f newPos = new Vector3f(this.pos);
        float hypotenuse = (speed) * delta;
        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
        newPos.z += adjacent;
        newPos.x -= opposite;
        //TODO: Check collisions on newPos, if collided, dont set newPos as pos
        this.pos.z = newPos.z;
        this.pos.x = newPos.x;
        if (up) this.pos.y+=0.1f;
        if (down) this.pos.y-=0.1f;
        this.lastTime = now;
	}
}