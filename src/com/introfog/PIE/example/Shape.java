package com.introfog.PIE.example;

import java.awt.*;

public abstract class Shape{
	public enum Type{
		circle, AABB, polygon
	}
	
	public Body body;
	public Type type;
	
	
	public abstract void render (Graphics graphics);
	
	public abstract AABB computeAABB ();
	
	public abstract void computeMass ();
}
