package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.HeightMap;
import com.mnn.society.math.PerlinNoise;
import com.mnn.society.math.VectorMath;
import com.mnn.society.md3.ModelQuake3;

public class Structure {
	public static Map<String, ModelQuake3> structureMeshes = new HashMap<String, ModelQuake3>(); 
	public float 		yoffset;
	private ModelQuake3 model;
	private Vector3f	rotation;
	private Vector3f	position;
	private Vector3f	angles;
	
	public Structure (Vector2f pos, String name) {
		/*float first 	= PerlinNoise.getXYValue((int)pos.x, (int)pos.y, 0.0621f, 0.99f, 0.3688f, 16);
		float second 	= PerlinNoise.getXYValue((int)pos.x+1, (int)pos.y, 0.0621f, 0.99f, 0.3688f, 16);
		float third 	= PerlinNoise.getXYValue((int)pos.x+1, (int)pos.y+1, 0.0621f, 0.99f, 0.3688f, 16);
		*/
		this.position = new Vector3f(pos.x*HeightMap.segmentSize+HeightMap.segmentSize/2, 0f, pos.y*HeightMap.segmentSize+HeightMap.segmentSize/2);
		
		//System.out.println("the height of the heightmap: " + HeightMap.to3dheight(first));
		
		this.yoffset = 2.5f;
		
		this.rotation = VectorMath.calculateNormal(new Vector3f [] {
				new Vector3f(0f, 0f, 0f),
				new Vector3f(1f, 0f, 0f),
				new Vector3f(1f, 0f, 1f)
		});
		
		//System.out.println("structure normal: " + this.rotation);
		//System.out.println("structure position: " + this.position + " height: " + first);
		
		
		float min = 0f, max = 0f;
		
		this.angles = new Vector3f(
				(float)Math.toDegrees((float)Math.atan((double)(this.rotation.x / this.rotation.y)))/2f,
				(float)Math.toDegrees((float)Math.atan((double)(this.rotation.x / this.rotation.z)))/2f,
				(float)Math.toDegrees((float)Math.atan((double)(this.rotation.z / this.rotation.y)))/2f);
		
		
		//System.out.println("structure angles: " + this.angles);
		
		//check if the mesh is loaded
		if (!Structure.structureMeshes.containsKey(name)) {
			try {
				ModelQuake3 tmp = new ModelQuake3();
				tmp.load(name, name);
				tmp.setTorsoAnimation("TORSO_STAND");
				tmp.setLegsAnimation("LEGS_RUN");
				
				Structure.structureMeshes.put(name, tmp);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.model = Structure.structureMeshes.get(name);
	}
	
	public void render() {
		glPushMatrix();
			glTranslatef(this.position.x, this.position.y, this.position.z);
			
			//glTranslatef(0f, 0f, 10f);
		
			glPushMatrix();
				//glRotatef(15f, -1f, 0f, 0f);
				//glRotatef(15f, 0f, 0f, 1f);
			
				glRotatef(this.angles.z, 1f, 0f, 0f);
				glRotatef(this.angles.x, 0f, 0f, -1f);
				//glRotatef(this.angles.y, 0f, 1f, 0f);
				//glRotatef(-this.angles.z, 0f, 0f, 1f);
				this.model.draw();
				
			glPopMatrix();
		glPopMatrix();
	}
}
