package com.introfog.PIE;

import java.awt.*;

public class AABB extends Body{ //Axis Aligned Bounding Box
	private Vector2f min;
	private Vector2f max;
	
	
	public AABB (float minX, float minY, float maxX, float maxY, float mass){
		super (mass);
		
		min = new Vector2f (minX, minY);
		max = new Vector2f (maxX, maxY);
		
		shape = Shape.AABB;
	}
	
	public static boolean isIntersected (AABB a, AABB b){ //TODO изменить название метода
		if (a.max.x < b.min.x || a.min.x > b.max.x){
			return false;
		}
		if (a.max.y < b.min.y || a.min.y > b.max.y){
			return false;
		}
		return true;
	}
	
	@Override
	public void update (float deltaTime){
		force.set (World.GRAVITY);
		acceleration.set (force, invertMass);
		
		velocity.x += acceleration.x * deltaTime; //обновили скорость
		velocity.y += acceleration.y * deltaTime;
		
		
		min.x += velocity.x * deltaTime; //обновили позицию
		min.y += velocity.y * deltaTime;
		
		max.x += velocity.x * deltaTime; //обновили позицию
		max.y += velocity.y * deltaTime;
		
		force.set (0f, 0f);
	}
	
	@Override
	public void draw (Graphics graphics){
		graphics.drawRect ((int) min.x, (int) min.y, (int) (max.x - min.x), (int) (max.y - min.y));
	}
}
