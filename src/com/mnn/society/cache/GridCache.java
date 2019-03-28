package com.mnn.society.cache;

public class GridCache {
	public String casheHash;
	public Cache rawCache;
	
	public GridCache () {}
	
	public void setCache (Cache cache) {
		this.rawCache = cache;
	}
}