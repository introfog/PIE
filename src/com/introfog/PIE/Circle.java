package com.introfog.PIE;

import com.sun.javafx.geom.Vec2f;

public class Circle{
	public float weight;
	public float radius;
	public Vec2f acceleration;
	public Vec2f force;
	public Vec2f velocity;
	public Vec2f centre;
	
	
	public Circle (float radius, Vec2f centre){
		this.radius = radius;
		this.centre = new Vec2f (centre);
		acceleration = new Vec2f (0f, 0f);
		velocity = new Vec2f (0f, 0f);
		force = new Vec2f (0f, 0f);
		weight = 1f;
	}
	
	public static boolean isIntersected (Circle a, Circle b ){ //TODO изменить название метода
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius < (a.centre.x + b.centre.x)*(a.centre.x + b.centre.x) + (a.centre.y + b.centre.y)*(a.centre.y + b.centre.y);
	}
	
	public void update (float deltaTime){
		force.x = World.gravity.x; //рассчитали итоговую силу
		force.y = World.gravity.y;
		
		acceleration.x = force.x / weight; //рассчитали итоговое ускорение
		acceleration.y = force.y / weight;
		
		velocity.x += acceleration.x * deltaTime; //обновили скорость
		velocity.y += acceleration.y * deltaTime;
		
		centre.x += velocity.x * deltaTime; //обновили позицию
		centre.y += velocity.y * deltaTime;
	}
	
	public Vec2f getCentre (){
		return centre;
	}
	
	public float getRadius (){
		return radius;
	}
}
