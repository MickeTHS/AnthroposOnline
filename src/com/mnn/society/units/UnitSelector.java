package com.mnn.society.units;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.spi.SyncResolver;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.Picker;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.view.Camera;

/**
 * responsible for keeping track on selected units
 * @author mickeman
 *
 */
public class UnitSelector {
	public static ISelectable	currentlySelected;
	private Picker				picker;
	private List<ISelectable> 	allUnits = new ArrayList<ISelectable>();
	private ObjectMarker		marker;
	
	public UnitSelector () {
		this.picker = new Picker(new Vector3f(0f, 0f, 0f));
		this.marker = new ObjectMarker();
	}
	
	public void addSelectableUnit (ISelectable unit) {
		this.allUnits.add(unit);
	}
	
	public void update () {
		FloatBuffer buf = Camera.get3DMousePosition();
		this.picker.position = new Vector3f(buf.get(0), buf.get(1), buf.get(2));
		//System.out.println(this.picker.position);
		//this.picker.render();
	} 
	
	public void pick () {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : UnitSelector : picking ... ");
		for (ISelectable sel : this.allUnits) {
			if (sel.checkPicker(this.picker)) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : UnitSelector : found object to select : " + sel.toString());
				
				UnitSelector.currentlySelected = sel;
				
				return;
			}	
		}
		
		this.currentlySelected = null;
	}
	
	public void render () {
		this.picker.render();
		
		if (this.currentlySelected != null) {
			this.marker.setPosition(this.currentlySelected.getPosition());
			this.marker.render();
		}
	}
}
