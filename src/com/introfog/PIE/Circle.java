package com.introfog.PIE;

import java.awt.*;

public class Circle extends Body{
	public float radius;
	
	
	public Circle (float radius, float centreX, float centreY, float mass){
		super (mass);
		this.radius = radius;
		this.position = new Vector2f (centreX, centreY);
		
		shape = Shape.circle;
	}
	
	public static boolean isIntersected (Circle a, Circle b){ //TODO изменить название метода
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > Vector2f.distanceWithoutSqrt (a.position, b.position);
	}
	
	@Override
	public void update (float deltaTime){
		force.set (World.GRAVITY);
		acceleration.set (force, invertMass);
		
		velocity.x += acceleration.x * deltaTime; //обновили скорость
		velocity.y += acceleration.y * deltaTime;
		
		position.x += velocity.x * deltaTime; //обновили позицию
		position.y += velocity.y * deltaTime;
		
		force.set (0f, 0f);
	}
	
	@Override
	public void draw (Graphics graphics){
		graphics.drawOval ((int) (position.x - radius), (int) (position.y - radius), (int) radius * 2, (int) radius * 2);
	}
}
