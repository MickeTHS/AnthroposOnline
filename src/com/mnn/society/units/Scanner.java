package com.mnn.society.units;

import static org.lwjgl.opengl.GL11.*;
import java.io.IOException;
import com.mnn.society.md3.ModelQuake3;

public class Scanner {
public static ModelQuake3 scannerModel 	= null; //a static model will not work if were using lots of animations! NEED TO CHANGE THIS! really? not sure i need to change it actually
	
	public Scanner () {
		if (Scanner.scannerModel == null) { //singleton workerModel
			try {
				Scanner.scannerModel = new ModelQuake3();
				Scanner.scannerModel.load("scanner", "scanner");
				Scanner.scannerModel.setTorsoAnimation("TORSO_DROP");
				//Drill.drillModel.setWeaponAnimation("WEAP_FETCH");
			} catch (IOException e) {
				System.out.println("CLIENT : Scanner : unable to load MD3 scanner model : " + e);
			}
		}
	}
	
	public void render () {
		glPushMatrix();
			Scanner.scannerModel.draw();
		glPopMatrix();
	}
}
