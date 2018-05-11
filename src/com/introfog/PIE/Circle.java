package com.introfog.PIE;

import com.introfog.PIE.math.MathPIE;

import java.awt.*;

public class Circle extends Shape{
	public float radius;
	
	
	public Circle (float radius, float centreX, float centreY, float density, float restitution){
		body = new Body (this, centreX, centreY, density, restitution);
		this.radius = radius;
		
		computeMass ();
		computeAABB ();
		
		type = Type.circle;
	}
	
	@Override
	public void render (Graphics graphics){
		renderAABB (graphics);
		graphics.setColor (Color.RED);
		graphics.drawLine ((int) body.position.x, (int) body.position.y, (int) body.position.x, (int) body.position.y);
		graphics.drawLine ((int) body.position.x, (int) body.position.y,
						   (int) (body.position.x + radius * Math.cos (body.orientation)),
						   (int) (body.position.y + radius * Math.sin (body.orientation)));
		graphics.drawOval ((int) (body.position.x - radius), (int) (body.position.y - radius), (int) radius * 2, (int) radius * 2);
	}
	
	@Override
	public void computeAABB (){
		aabb.min.set (body.position.x - radius, body.position.y - radius);
		aabb.max.set (body.position.x + radius, body.position.y + radius);
	}
	
	@Override
	protected void computeMass (){
		float mass = MathPIE.PI * radius * radius * body.density;
		body.invertMass = (mass == 0f) ? 0f : 1f / mass;
		
		float inertia = radius * radius / body.invertMass;
		body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
	}
	
	@Override
	protected void renderAABB (Graphics graphics){
		computeAABB ();
		graphics.setColor (Color.GRAY);
		graphics.drawRect ((int) aabb.min.x, (int) aabb.min.y, (int) (aabb.max.x - aabb.min.x), (int) (aabb.max.y - aabb.min.y));
	}
}
