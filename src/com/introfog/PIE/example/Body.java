package com.introfog.PIE.example;

import com.introfog.PIE.*;

public class Body{
	public float mass;
	public float invertMass;
	public float restitution;
	public Vector2f position;
	public Vector2f force;
	public Vector2f velocity;
	
	
	public Body (float mass, float positionX, float positionY){
		this.mass = mass;
		
		if (mass == MathPIE.INFINITY_MASS){
			invertMass = 0f;
		}
		else{
			invertMass = 1f / mass;
		}
		
		restitution = 0.15f;
		force = new Vector2f (0f, 0f);
		velocity = new Vector2f (0f, 0f);
		position = new Vector2f (positionX, positionY);
	}
}
