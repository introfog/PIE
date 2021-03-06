package com.introfog.PIE.collisions;

import com.introfog.PIE.*;

public class CollisionPolygonCircle implements CollisionCallback{
	
	public static final CollisionPolygonCircle instance = new CollisionPolygonCircle ();
	
	
	@Override
	public void handleCollision (Manifold manifold){
		manifold.circleA = manifold.circleB;
		manifold.polygonB = manifold.polygonA;
		CollisionCirclePolygon.instance.handleCollision (manifold);
		
		if (manifold.contactCount > 0){
			manifold.normal.negative ();
		}
	}
}