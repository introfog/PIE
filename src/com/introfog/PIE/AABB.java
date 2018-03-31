package com.introfog.PIE;

import java.awt.*;

public class AABB extends Body{ //Axis Aligned Bounding Box
	private Vector2f max;
	
	
	public AABB (float positionX, float positionY, float maxX, float maxY, float mass){
		super (mass);
		
		position = new Vector2f (positionX, positionY);
		max = new Vector2f (maxX, maxY);
		
		shape = Shape.AABB;
	}
	
	public static boolean isIntersected (AABB a, AABB b){ //TODO изменить название метода
		if (a.max.x < b.position.x || a.position.x > b.max.x){
			return false;
		}
		if (a.max.y < b.position.y || a.position.y > b.max.y){
			return false;
		}
		return true;
	}
	
	@Override
	public void draw (Graphics graphics){
		graphics.drawRect ((int) position.x, (int) position.y, (int) (max.x - position.x), (int) (max.y - position.y));
	}
}
