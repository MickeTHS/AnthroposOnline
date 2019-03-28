package com.mnn.society.graphics;

/**
 * Collidable stuff may not be needed, this class just might be removed.
 * @author mickeman
 *
 */
public interface Collidable {
	public boolean intersects(Collidable other);
	public void render(long delta);
}