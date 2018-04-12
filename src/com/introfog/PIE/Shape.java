package com.introfog.PIE;

import java.awt.*;

public abstract class Shape{
	public enum Type{
		circle, AABB, polygon
	}
	
	public Type type;
	public AABB aabb;
	public Body body;
	
	
	public abstract void render (Graphics graphics);
	
	public abstract void computeAABB ();
	
	protected abstract void computeMass ();
}
