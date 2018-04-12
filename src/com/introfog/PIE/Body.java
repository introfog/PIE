package com.introfog.PIE;

public class Body{
	public float invertMass;
	public float restitution;
	public float density;
	public Vector2f position;
	public Vector2f force;
	public Vector2f velocity;
	public Shape shape;
	
	
	public Body (Shape shape, float positionX, float positionY, float density, float restitution){
		this.shape = shape;
		this.density = density;
		this.restitution = restitution;
		
		force = new Vector2f (0f, 0f);
		velocity = new Vector2f (0f, 0f);
		position = new Vector2f (positionX, positionY);
	}
	
	public Body (Shape shape, float positionX, float positionY){
		this.shape = shape;
		this.density = MathPIE.STATIC_BODY_DENSITY;
		this.restitution = 0.4f;
		
		force = new Vector2f (0f, 0f);
		velocity = new Vector2f (0f, 0f);
		position = new Vector2f (positionX, positionY);
	}
}
