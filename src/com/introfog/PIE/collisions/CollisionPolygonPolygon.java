package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.*;

public class CollisionPolygonPolygon implements CollisionCallback{
	public static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon ();
	
	private int contactCounter;
	private Vector2f tmpV = new Vector2f ();
	private Vector2f[] contactVertices;
	private Manifold manifold;
	
	@Override
	public void handleCollision (Manifold manifold){
		this.manifold = manifold;
		Polygon A = manifold.polygonA;
		Polygon B = manifold.polygonB;
		
		contactVertices = Vector2f.arrayOf (2); //точки, которые лежат внутри полигона
		contactCounter = 0;
		
		searchContactVertices (A, B, contactVertices);
		if (contactCounter > 0){
			searchNormalAndPenetration (A,false);
			return;
		}
		searchContactVertices (B, A, contactVertices);
		if (contactCounter > 0){
			searchNormalAndPenetration (B,true);
			return;
		}
		
		manifold.areBodiesCollision = false;
	}
	
	private void searchContactVertices (Polygon prey, Polygon provocative, Vector2f[] contactVertices){
		//Пропускаем прямую паралельную оси Х, через точку возбудителя, если прямая пересекла 2 ребра то точка внутри полигона
		//коэфициенты прямой ax + by + c = 0
		//т.к. прямая паралельна оси Х, то уравнение примет вид y - y0 = 0, где y0 = координате у проверяемой точки
		float y1, y2;
		for (int i = 0; i < provocative.vertexCount; i++){
			tmpV.set (provocative.vertices[i]); //преобразовали координаты вершины провокатора в координаты жертвы
			provocative.rotateMatrix.mul (tmpV, tmpV);
			tmpV.add (provocative.body.position);
			tmpV.sub (prey.body.position);
			prey.rotateMatrix.transposeMul (tmpV, tmpV);
			for (int j = 0; j < prey.vertexCount; j++){
				y1 = prey.vertices[(j + 1) % prey.vertexCount].y;
				y2 = prey.vertices[j].y;
				if (((tmpV.y - y1) * (tmpV.y - y2) < 0)){
					contactVertices[contactCounter++].set (tmpV);
					
					if (contactCounter == 2){
						return;
					}
					
					break;
				}
			}
		}
	}
	
	private void searchNormalAndPenetration (Polygon prey, boolean reverse){
		//ищем ближайшее ребро к точке
		float maxDotProduct = -Float.MAX_VALUE;
		float currentDot;
		int indexNearEdge = -1; //индеекс ближайшего ребра
		for (int i = 0; i < contactCounter; i++){
			for (int j = 0; j < prey.vertexCount; j++){
				tmpV.set (prey.normals[j]); //поулчили координты ребра
				currentDot = Vector2f.dotProduct (tmpV, contactVertices[i]);
				
				if (currentDot > maxDotProduct){
					maxDotProduct = currentDot;
					indexNearEdge = j;
				}
			}
			
			manifold.contacts[i].set (contactVertices[i]); //восстанавливаем координаты точки касания в мировые кординаты
			prey.rotateMatrix.mul (manifold.contacts[i], manifold.contacts[i]);
			manifold.contacts[i].add (prey.body.position);
		}
		
		manifold.contactCount = contactCounter;
		
		manifold.normal.set (prey.normals[indexNearEdge]);
		if (reverse){
			manifold.normal.negative ();
		}
		
		float x1 = prey.vertices[(indexNearEdge + 1) % prey.vertexCount].x;
		float y1 = prey.vertices[(indexNearEdge + 1) % prey.vertexCount].y;
		
		float x2 = prey.vertices[indexNearEdge].x;
		float y2 = prey.vertices[indexNearEdge].y;
		
		float a = (y2 - y1);
		float b = (x1 - x2);
		float c = (x2 * y1 - x1 * y2);
		manifold.penetration = Math.abs (a * contactVertices[0].x + b * contactVertices[0].y + c) / (float) Math.sqrt (a * a + b * b);
	}
}

