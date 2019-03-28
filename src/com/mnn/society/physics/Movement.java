package com.mnn.society.physics;

import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.server.time.ServerTime;

public class Movement {
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
	
	public Movement(Vector3f initialPosition) { 
		this.pos = initialPosition;
		this.height = 0f;
		this.speed = 0.1f;
		this.flying = false;
	}
	
	/* call this when stopping MOVEMENT */
	public void stop() {
		this.lastTime = 0L;
	}
	
	/* call this every rendering cycle to render movement */
	public void move(Vector3f direction, boolean forward, boolean backward, boolean left, boolean right, float speed) {
		if(lastTime == 0L) lastTime = ServerTime.getSyncedTimeInMillis();
		long now = ServerTime.getSyncedTimeInMillis();
		
		long delta = now - lastTime;
		
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
        float hypotenuse = (speed ) * delta;
        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
        newPos.z += adjacent * speed;
        newPos.x -= opposite * speed;
        //TODO: Check collisions on newPos, if collided, dont set newPos as pos
        this.pos.z = newPos.z;
        this.pos.x = newPos.x;
        this.lastTime = now;
	}
}