package com.introfog.PIE;

import java.awt.*;

public class AABB extends Shape{ //Axis Aligned Bounding Box
	public float width;
	public float height;
	public Vector2f centre;
	
	
	public AABB (){
		body = new Body (this, 0f, 0f);
		
		type = Type.AABB;
	}
	
	public AABB (float positionX, float positionY, float width, float height, float density, float restitution){
		body = new Body (this, positionX, positionY, density, restitution);
		this.width = width;
		this.height = height;
		centre = new Vector2f ();
		
		computeMass ();
		computeAABB ();
		
		type = Type.AABB;
	}
	
	@Override
	public void render (Graphics graphics){
		centre.set (body.position.x + width / 2f, body.position.y + height / 2f);
		graphics.drawRect ((int) body.position.x, (int) body.position.y, (int) width, (int) height);
	}
	
	@Override
	public void computeAABB (){
		aabb = this;
	}
	
	@Override
	protected void computeMass (){
		float mass = width * height * body.density;
		body.invertMass = (mass == 0f) ? 0f : 1f / mass;
	}
	
	public void updateCentre (){
		centre.set (body.position.x + width / 2f, body.position.y + height / 2f);
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
		a.updateCentre ();
		
		Vector2f tmpNormal = Vector2f.sub (b.body.position, a.centre);
		Vector2f closest = new Vector2f ();
		
		float xExtent = a.width / 2;
		float yExtent = a.height / 2;
		
		closest.x = MathPIE.clamp (-xExtent, xExtent, tmpNormal.x);
		closest.y = MathPIE.clamp (-yExtent, yExtent, tmpNormal.y);
		
		if (tmpNormal.equals (closest)){
			return true;
		}
		
		float distance = Vector2f.sub (tmpNormal, closest).lengthWithoutSqrt ();
		if (distance > b.radius * b.radius){
			return false;
		}
		return true;
	}
}
