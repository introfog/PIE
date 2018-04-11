package com.introfog.PIE;

import java.awt.*;

public class AABB extends Shape{ //Axis Aligned Bounding Box
	public float width;
	public float height;
	public Vector2f centre;
	
	
	public AABB (float positionX, float positionY, float width, float height, float mass){
		body = new Body (this, mass, positionX, positionY);
		type = Type.AABB;
		
		this.width = width;
		this.height = height;
		
		centre = new Vector2f (positionX + width / 2f, positionY + height / 2f);
	}
	
	public void updateCentre (){
		centre.set (body.position.x + width / 2f, body.position.y + height / 2f);
	}
	
	@Override
	public void render (Graphics graphics){
		centre.set (body.position.x + width / 2f, body.position.y + height / 2f);
		graphics.drawRect ((int) body.position.x, (int) body.position.y, (int) width, (int) height);
	}
	
	
	public static boolean isIntersected (AABB a, AABB b){
		if (a.body.position.x + a.width < b.body.position.x || a.body.position.x > b.body.position.x + b.width){
			return false;
		}
		if (a.body.position.y + a.height < b.body.position.y || a.body.position.y > b.body.position.y + b.height){
			return false;
		}
		return true;
	}
	
	public static boolean isIntersected (AABB a, Circle b){
		if (a.body.position.x + a.width < b.body.position.x - b.radius || a.body.position.x > b.body.position.x + b.radius){
			return false;
		}
		if (a.body.position.y + a.height < b.body.position.y - b.radius || a.body.position.y > b.body.position.y + b.radius){
			return false;
		}
		return true;
	}
	
	@Override
	public AABB computeAABB (){
		return null;
	}
	
	@Override
	public void computeMass (){
	
	}
}
