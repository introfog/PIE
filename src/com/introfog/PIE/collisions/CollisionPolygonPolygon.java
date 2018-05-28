package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.*;

public class CollisionPolygonPolygon implements CollisionCallback{
	public static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon ();
	
	
	private float minPenetration (Polygon A, Polygon B){
		float penetration = Float.MAX_VALUE;
		float currPenetration;
		int indexMinA;
		int indexMaxA;
		int indexMinB;
		int indexMaxB;
		Vector2f axisX = new Vector2f ();
		Vector2f axisY = new Vector2f ();
		Mat22 currRotateMatrix = new Mat22 ();
		Vector2f projMinA = new Vector2f ();
		Vector2f projMaxA = new Vector2f ();
		Vector2f projMinB = new Vector2f ();
		Vector2f projMaxB = new Vector2f ();
		
		Vector2f tmpV = new Vector2f ();
		
		for (int i = 0; i < A.vertexCount; i++){
			axisX.set (A.normals[i]);
			A.rotateMatrix.mul (axisX, axisX);
			
			if (axisX.y == 0){
				axisY.set (0f, 1f);
			}
			else{
				axisY.set (1f, -axisX.x / axisX.y);
				axisY.normalize ();
			}
			currRotateMatrix.setAngle (axisX, axisY);
			//сделил новую систему координат, которая по оси Х совпадает с нормой к ребру полигона А
			//вычисляем координаты проекций осей в новой системе координат, и смотрим персечение отрезков по оси Х
			
			indexMaxA = A.getSupportIndex (axisX);
			indexMaxB = B.getSupportIndex (axisX);
			axisX.negative ();
			indexMinA = A.getSupportIndex (axisX);
			indexMinB = B.getSupportIndex (axisX);
			
			A.rotateMatrix.mul (A.vertices[indexMinA], tmpV);
			tmpV.add (A.body.position);
			currRotateMatrix.mul (tmpV, projMinA);
			A.rotateMatrix.mul (A.vertices[indexMaxA], tmpV);
			tmpV.add (A.body.position);
			currRotateMatrix.mul (tmpV, projMaxA);
			B.rotateMatrix.mul (B.vertices[indexMinB], tmpV);
			tmpV.add (B.body.position);
			currRotateMatrix.mul (tmpV, projMinB);
			B.rotateMatrix.mul (B.vertices[indexMaxB], tmpV);
			tmpV.add (B.body.position);
			currRotateMatrix.mul (tmpV, projMaxB);
			
			currPenetration = projMaxA.x - projMinB.x;
			if (currPenetration < penetration){
				penetration = currPenetration;
			}
		}
		
		return penetration;
	}
	
	
	@Override
	public void handleCollision (Manifold manifold){
		Polygon A = manifold.polygonA;
		Polygon B = manifold.polygonB;
		
		minPenetration (A, B);
		minPenetration (B, A);
		
	}
}
