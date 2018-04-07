package com.introfog.PIE;

import java.awt.*;

public class Circle extends Body{
	public float radius;
	
	
	public Circle (float radius, float centreX, float centreY, float mass){
		super (mass);
		this.radius = radius;
		this.position = new Vector2f (centreX, centreY);
		
		shape = Shape.circle;
	}
	
	public static boolean isIntersected (Circle a, Circle b){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > Vector2f.distanceWithoutSqrt (a.position, b.position);
	}
	
	@Override
	public void draw (Graphics graphics){
		graphics.drawOval ((int) position.x, (int) position.y, 1, 1);
		graphics.drawOval ((int) (position.x - radius), (int) (position.y - radius), (int) radius * 2, (int) radius * 2);
	}
}
