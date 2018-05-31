package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.*;

public class CollisionPolygonPolygon implements CollisionCallback{
	public static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon ();
	public static int counter = 0;
	
	
	private float minPenetration (Polygon A, Polygon B, int[] indexMinPenetrationNormal){
		boolean areCollisions = false;
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
			
			if (projMinA.x > projMaxA.x){
				tmpV.set (projMaxA);
				projMaxA.set (projMinA);
				projMinA.set (tmpV);
			}
			if (projMinB.x > projMaxB.x){
				tmpV.set (projMaxB);
				projMaxB.set (projMinB);
				projMinB.set (tmpV);
			}
			//проверить проекцию
			if (projMinA.x < projMaxB.x && projMaxB.x < projMaxA.x){
				currPenetration = projMaxB.x - projMinA.x;
			}
			else{
				currPenetration = projMaxA.x - projMinB.x;
			}
			
			if (currPenetration > 0f && currPenetration < penetration){
				areCollisions = true;
				penetration = currPenetration;
				indexMinPenetrationNormal[0] = i;
			}
		}
		
		if (areCollisions){
			return penetration;
		}
		return -1f;
	}
	
	private int searchContactVertices (Polygon prey, Vector2f[] contacts, int contactCounterPast){
		//Выпускаем луч паралельный оси Х вправо, через точку возбудителя, если луч пересек нечетное число ребер то точка внутри полигона
		//коэфициенты прямой ax + by + c = 0
		//т.к. прямая паралельна оси Х, то уравнение примет вид y - y0 = 0, где y0 = координате у проверяемой точки
		//Время работы O(n)
		float y1, y2;
		float x1, x2;
		int counterIntersectsEdges;
		float coordinateIntersectsX;
		Vector2f tmpV = new Vector2f ();
		int contactCounter = 0;
		Vector2f[] newContacts = Vector2f.arrayOf (2);
		
		for (int i = 0; i < contactCounterPast; i++){
			tmpV.set (contacts[i]); //преобразовали координаты вершины провокатора в координаты жертвы
			tmpV.sub (prey.body.position);
			prey.rotateMatrix.transposeMul (tmpV, tmpV);
			counterIntersectsEdges = 0;
			
			for (int j = 0; j < prey.vertexCount; j++){ //ищем пересечения с прямой, но у нас луч напралвенный вправо, поэтому проверяем что бы точка персечения лежала справа
				y1 = prey.vertices[(j + 1) % prey.vertexCount].y;
				y2 = prey.vertices[j].y;
				x1 = prey.vertices[(j + 1) % prey.vertexCount].x;
				x2 = prey.vertices[j].x;
				
				//находим точку пересечения искомой прямой y - y0 = 0 и прямой, проходящей через 2 точки (координаты prey)
				coordinateIntersectsX = -((x1 * y2 - x2 * y1) + (x2 - x1) * tmpV.y) / (y1 - y2);
				if (((tmpV.y - y1) * (tmpV.y - y2) < 0) && tmpV.x < coordinateIntersectsX){
					counterIntersectsEdges++;
				}
			}
			
			if (counterIntersectsEdges % 2 == 1){
				newContacts[contactCounter++].set (tmpV);
			}
		}
		
		contacts = newContacts;
		return contactCounter;
	}
	
	
	@Override
	public void handleCollision (Manifold manifold){
		counter++;
		
		Polygon A = manifold.polygonA;
		Polygon B = manifold.polygonB;
		
		int[] indexNormalA = new int[1];
		int[] indexNormalB = new int[1];
		
		float minPenetrationA = minPenetration (A, B, indexNormalA);
		if (minPenetrationA < MathPIE.EPSILON){
			manifold.areBodiesCollision = false;
			return;
		}
		
		float minPenetrationB = minPenetration (B, A, indexNormalB);
		if (minPenetrationB < MathPIE.EPSILON){
			manifold.areBodiesCollision = false;
			return;
		}
		
		Vector2f direction = new Vector2f ();
		int[] indicesContacts = new int[2];
		if (minPenetrationA <= minPenetrationB){
			manifold.normal.set (A.normals[indexNormalA[0]]);
			A.rotateMatrix.mul (manifold.normal, manifold.normal);
			manifold.penetration = minPenetrationA;
			direction.set (manifold.normal);
			direction.negative ();
			manifold.contactCount = B.getSupportIndices (direction, indicesContacts);
			for (int i = 0; i < manifold.contactCount; i++){
				manifold.contacts[i].set (B.vertices[indicesContacts[i]]);
				B.rotateMatrix.mul (manifold.contacts[i], manifold.contacts[i]);
				manifold.contacts[i].add (B.body.position);
			}
			//manifold.contactCount = searchContactVertices (A, manifold.contacts, manifold.contactCount);
		}
		else{
			manifold.normal.set (B.normals[indexNormalB[0]]);
			B.rotateMatrix.mul (manifold.normal, manifold.normal);
			manifold.penetration = minPenetrationB;
			direction.set (manifold.normal);
			direction.negative ();
			manifold.contactCount = A.getSupportIndices (direction, indicesContacts);
			for (int i = 0; i < manifold.contactCount; i++){
				manifold.contacts[i].set (A.vertices[indicesContacts[i]]);
				A.rotateMatrix.mul (manifold.contacts[i], manifold.contacts[i]);
				manifold.contacts[i].add (A.body.position);
			}
			//manifold.contactCount = searchContactVertices (B, manifold.contacts, manifold.contactCount);
		}
	}
}
