package com.introfog.PIE;

public class Circle{
	public float mass;
	public float invertMass;
	public float radius;
	public Vector2f acceleration;
	public Vector2f force;
	public Vector2f velocity;
	public Vector2f centre;
	
	
	public Circle (float radius, Vector2f centre, float mass){
		this.radius = radius;
		this.centre = new Vector2f (centre);
		this.mass = mass;
		
		if (mass == World.INIFINITY_MASS){
			invertMass = 0f;
		}
		else{
			invertMass = 1 / mass;
		}
		
		acceleration = new Vector2f (0f, 0f);
		force = new Vector2f (0f, 0f);
		velocity = new Vector2f (0f, 0f);
	}
	
	public static boolean isIntersected (Circle a, Circle b ){ //TODO изменить название метода
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius < Vector2f.distanceWithoutSqrt (a.centre, b.centre);
	}
	
	public void update (float deltaTime){
		force.x = World.gravity.x; //рассчитали итоговую силу
		force.y = World.gravity.y;
		
		acceleration.x = force.x * invertMass; //рассчитали итоговое ускорение
		acceleration.y = force.y * invertMass;
		
		velocity.x += acceleration.x * deltaTime; //обновили скорость
		velocity.y += acceleration.y * deltaTime;
		
		centre.x += velocity.x * deltaTime; //обновили позицию
		centre.y += velocity.y * deltaTime;
	}
}
