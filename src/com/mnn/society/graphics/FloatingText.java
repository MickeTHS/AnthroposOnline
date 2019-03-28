package com.mnn.society.graphics;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mnn.society.gui.ScreenText;
import com.mnn.society.server.utils.Logger;
import com.mnn.society.units.ISelectable;
import static org.lwjgl.opengl.GL11.*;

/**
 * This is text that will be displayed over the ISelectable unit
 * @author mickeman
 *
 */
public class FloatingText {
	private String		text;
	private Vector4f	fontColor;
	private ISelectable	originObject;
	private ScreenText	onscreenText;
	private float		spinning = 0f;
	private float		scale = 0f;
	private boolean		down = false;
	
	public FloatingText (String text, Vector4f fontColor) {
		this.text 			= text;
		this.fontColor		= fontColor;
		//bounds are not relevant at this point because we will update it every frame anyways
		setText(text);
	}
	
	public void setText (String text) {
		this.onscreenText	= new ScreenText(text, 0.02f, this.fontColor, new Vector2f [] { new Vector2f(0f, 0f), new Vector2f(0f, 0f) }, 0f);
	}
	
	public void setOrigin (ISelectable originObject) {
		this.originObject = originObject;
	}
	
	public void render () {
		if (this.originObject == null) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : FloatingText : render : no origin object");
			return;
		}
		
		Vector3f loc = originObject.getPosition();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glPushMatrix();
			/*if (!down && scale >= 1f) down = true;
			scale = down ? scale-0.01f : scale+0.01f;
			glScalef(scale, 1f, 1f);
			*/
			glTranslatef(loc.x, loc.y+1f, loc.z);
			glTranslatef(this.onscreenText.width/2f,0f,0f);
			this.spinning = this.spinning+1f >= 360f ? 0f : this.spinning+1f;
			glRotatef(this.spinning, 0, 1, 0);
			glTranslatef(-this.onscreenText.width/2f,0f,0f);
			glRotatef(180f, 1, 0, 0);
			this.onscreenText.render();
		glPopMatrix();
		
		glDisable(GL_BLEND);
		/*this.font.renderCharAt(this.text.charAt(i), new Vector3f(loc.x + x, loc.y + 2f, loc.z), 10f);*/
	}
}
