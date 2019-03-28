package com.mnn.society.units;

import org.lwjgl.util.vector.Vector3f;

import com.mnn.society.graphics.Picker;

/**
 * any object or unit that wants to be selectable must implement this interface
 * @author mickeman
 *
 */
public interface ISelectable {
	public boolean checkPicker (Picker pick);
	public void setSelected (boolean selected);
	public boolean getSelected ();
	public Vector3f getPosition ();
}
