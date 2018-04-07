package com.introfog.PIE;

import java.awt.*;

public class AABB extends Body{ //Axis Aligned Bounding Box
	public float width;
	public float height;
	public Vector2f centre;
	
	
	public AABB (float positionX, float positionY, float width, float height, float mass){
		super (mass);
		shape = Shape.AABB;
		
		position = new Vector2f (positionX, positionY);
		this.width = width;
		this.height = height;
		
		centre = new Vector2f (positionX + width / 2f, positionY + height / 2f);
	}
	
	public void updateCentre (){
		centre.set (position.x + width / 2f, position.y + height / 2f);
	}
	
	@Override
	public void draw (Graphics graphics){
		centre.set (position.x + width / 2f, position.y + height / 2f);
		graphics.drawRect ((int) position.x, (int) position.y, (int) width, (int) height);
	}
	
	
	public static boolean isIntersected (AABB a, AABB b){
		if (a.position.x + a.width < b.position.x || a.position.x > b.position.x + b.width){
			return false;
		}
		if (a.position.y + a.height < b.position.y || a.position.y > b.position.y + b.height){
			return false;
		}
		return true;
	}
	
	public static boolean isIntersected (AABB a, Circle b){
		if (a.position.x + a.width < b.position.x - b.radius || a.position.x > b.position.x + b.radius){
			return false;
		}
		if (a.position.y + a.height < b.position.y - b.radius || a.position.y > b.position.y + b.radius){
			return false;
		}
		return true;
	}
}
