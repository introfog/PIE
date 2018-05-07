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
		graphics.drawLine ((int) body.position.x, (int) body.position.y, (int) body.position.x, (int) body.position.y);
		graphics.drawOval ((int) (body.position.x - radius), (int) (body.position.y - radius), (int) radius * 2, (int) radius * 2);
	}
	
	@Override
	public void computeAABB (){
		aabb.min.set (body.position.x - radius, body.position.y - radius);
		aabb.max.set (body.position.x + radius, body.position.y + radius);
	}
	
	@Override
	public void setOrientation (float radian){ }
	
	@Override
	protected void computeMass (){
		float mass = MathPIE.PI * radius * radius * body.density;
		body.invertMass = (mass == 0f) ? 0f : 1f / mass;
		
		float inertia = radius * radius / body.invertMass;
		body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
	}
	
	
	public static boolean isIntersected (Circle a, Circle b){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > Vector2f.distanceWithoutSqrt (a.body.position, b.body.position);
	}
}
