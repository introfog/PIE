package com.introfog.PIE;

import java.awt.*;

public class Circle extends Body{
	public float radius;
	public Vector2f centre;
	
	
	public Circle (float radius, float centreX, float centreY, float mass){
		super (mass);
		this.radius = radius;
		this.centre = new Vector2f (centreX, centreY);
	}
	
	public static boolean isIntersected (Circle a, Circle b){ //TODO изменить название метода
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius < Vector2f.distanceWithoutSqrt (a.centre, b.centre);
	}
	
	@Override
	public void update (float deltaTime){
		force.set (World.gravity);
		acceleration.set (force, invertMass);
		
		velocity.x += acceleration.x * deltaTime; //обновили скорость
		velocity.y += acceleration.y * deltaTime;
		
		centre.x += velocity.x * deltaTime; //обновили позицию
		centre.y += velocity.y * deltaTime;
		
		force.set (0f, 0f);
	}
	
	@Override
	public void draw (Graphics graphics){
		graphics.drawOval ((int) (centre.x - radius / 2), (int) (centre.y - radius / 2), (int) radius, (int) radius);
	}
}
