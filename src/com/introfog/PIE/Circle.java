package com.introfog.PIE;

import com.sun.javafx.geom.Vec2f;

public class Circle{
	private float radius;
	private Vec2f centre;
	
	
	public Circle (float radius, Vec2f centre){
		this.radius = radius;
		this.centre = new Vec2f (centre);
	}
	
	public static boolean isIntersected( Circle a, Circle b ){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius < (a.centre.x + b.centre.x)*(a.centre.x + b.centre.x) + (a.centre.y + b.centre.y)*(a.centre.y + b.centre.y);
	}
}
