package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.Vector2f;

public class CollisionCirclePolygon implements CollisionCallback{
	
	public static final CollisionCirclePolygon instance = new CollisionCirclePolygon ();
	
	@Override
	public void handleCollision (Manifold manifold){
		Circle A = manifold.circleA;
		Polygon B = manifold.polygonB;
		
		//перевели координаты центра в координаты полигона
		// center = B->u.Transpose( ) * (center - b->position);
		Vector2f centerA = new Vector2f (A.body.position);
		centerA.sub (B.body.position);
		B.rotateMatrix.mul (centerA, centerA);
		
		//Ищем ближайшее ребро полигона к центру окружности, проецируя центр на каждую нормаль ребра полигона
		float separation = Float.MIN_VALUE;
		float currS;
		int indexFaceNormal = 0;
		Vector2f tmpV = new Vector2f ();
		for (int i = 0; i < B.vertexCount; i++){
			tmpV.set (centerA);
			tmpV.sub (B.vertices[i]);
			currS = Vector2f.dotProduct (B.normals[i], tmpV);
			
			if (currS > A.radius){
				manifold.areBodiesCollision = false;
				return;
			}
			
			if (currS > separation){
				separation = currS;
				indexFaceNormal = i;
			}
		}
	}
}
