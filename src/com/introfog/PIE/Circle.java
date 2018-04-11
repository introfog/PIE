package com.introfog.PIE;

import java.awt.*;

public class Circle extends Shape{
	public float radius;
	
	
	public Circle (float radius, float centreX, float centreY, float mass){
		body = new Body (this, mass, centreX, centreY);
		this.radius = radius;
		
		type = Type.circle;
	}
	
	public static boolean isIntersected (Circle a, Circle b){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > Vector2f.distanceWithoutSqrt (a.body.position, b.body.position);
	}
	
	@Override
	public void render (Graphics graphics){
		graphics.drawOval ((int) body.position.x, (int) body.position.y, 1, 1);
		graphics.drawOval ((int) (body.position.x - radius), (int) (body.position.y - radius), (int) radius * 2, (int) radius * 2);
	}
	
	@Override
	public AABB computeAABB (){
		return null;
	}
	
	@Override
	public void computeMass (){
	
	}
}
