package com.introfog.PIE;

import java.awt.*;

public class Circle extends Shape{
	public float radius;
	
	
	public Circle (float radius, float centreX, float centreY, float density, float restitution){
		body = new Body (this, centreX, centreY, density, restitution);
		this.radius = radius;
		aabb = new AABB ();
		
		computeMass ();
		computeAABB ();
		
		type = Type.circle;
	}
	
	@Override
	public void render (Graphics graphics){
		graphics.drawOval ((int) body.position.x, (int) body.position.y, 1, 1);
		graphics.drawOval ((int) (body.position.x - radius), (int) (body.position.y - radius), (int) radius * 2, (int) radius * 2);
	}
	
	@Override
	public void computeAABB (){
		aabb.body.position.set (body.position.x - radius, body.position.y - radius);
		aabb.width = 2f * radius;
		aabb.height = 2f * radius;
	}
	
	@Override
	protected void computeMass (){
		float mass = MathPIE.PI * radius * radius * body.density;
		body.invertMass = (mass == 0f) ? 0f : 1f / mass;
	}
	
	
	public static boolean isIntersected (Circle a, Circle b){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > Vector2f.distanceWithoutSqrt (a.body.position, b.body.position);
	}
}
