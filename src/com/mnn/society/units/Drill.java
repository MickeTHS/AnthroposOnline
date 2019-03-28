package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.graphics.FloatingText;
import com.mnn.society.md3.ModelQuake3;

public class Drill {
	public static ModelQuake3 drillModel 	= null; //a static model will not work if were using lots of animations! NEED TO CHANGE THIS! really? not sure i need to change it actually
	
	public Drill () {
		if (Drill.drillModel == null) { //singleton workerModel
			try {
				Drill.drillModel = new ModelQuake3();
				Drill.drillModel.load("drill", "drill");
				Drill.drillModel.setTorsoAnimation("TORSO_ATTACK");
				//Drill.drillModel.setWeaponAnimation("WEAP_FETCH");
			} catch (IOException e) {
				System.out.println("CLIENT : Drill : unable to load MD3 drill model : " + e);
			}
		}
	}
	
	public void render () {
		glPushMatrix();
			Drill.drillModel.draw();
		glPopMatrix();
	}
}
