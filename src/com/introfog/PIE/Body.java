package com.introfog.PIE;

import java.awt.*;

public abstract class Body{
	public static final float INFINITY_MASS = 0f;
	
	public enum Shape{
		circle, AABB
	}
	
	public Shape shape;
	public float mass;
	public float invertMass;
	public Vector2f position;
	public Vector2f force;
	public Vector2f velocity;
	
	
	public Body (float mass){
		this.mass = mass;
		
		if (mass == INFINITY_MASS){
			invertMass = 0f;
		}
		else{
			invertMass = 1f / mass;
		}
		
		force = new Vector2f (0f, 0f);
		velocity = new Vector2f (0f, 0f);
	}
	
	public abstract void draw (Graphics graphics);
}
