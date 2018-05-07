package com.introfog.PIE.collisions;

import com.introfog.PIE.*;

public class CollisionCircleCircle implements CollisionCallback{
	public static final CollisionCircleCircle instance = new CollisionCircleCircle ();
	
	private float distanceWithoutSqrt;
	
	
	private boolean areIntersected (Circle a, Circle b){
		float sumRadius = a.radius + b.radius;
		sumRadius *= sumRadius;
		return sumRadius > distanceWithoutSqrt;
	}
	
	
	@Override
	public void handleCollision (Manifold manifold){
		Circle A = manifold.circleA;
		Circle B = manifold.circleB;
		
		manifold.normal = Vector2f.sub (B.body.position, A.body.position);
		distanceWithoutSqrt = manifold.normal.lengthWithoutSqrt ();
		
		if (!areIntersected (A, B)){
			manifold.areBodiesCollision = false;
			return;
		}
		
		manifold.penetration = A.radius + B.radius - (float) Math.sqrt (distanceWithoutSqrt);
		
		if (distanceWithoutSqrt == 0){
			manifold.normal.set (1f, 0f);
			manifold.penetration = A.radius;
		}
	}
}
