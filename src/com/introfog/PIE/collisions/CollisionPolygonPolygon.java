package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.*;

public class CollisionPolygonPolygon implements CollisionCallback{
	public static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon ();
	
	private int counter = 0;
	private int contactCounter;
	private Vector2f tmpV = new Vector2f ();
	private Vector2f[] contactVertices;
	private Manifold manifold;
	
	@Override
	public void handleCollision (Manifold manifold){
		counter++;
		if (counter == 2){
			System.out.println ();
		}
		
		this.manifold = manifold;
		Polygon A = manifold.polygonA;
		Polygon B = manifold.polygonB;
		
		contactVertices = Vector2f.arrayOf (2); //точки, которые лежат внутри полигона
		contactCounter = 0;
		
		searchContactVertices (A, B, contactVertices);
		if (contactCounter > 0){
			searchNormalAndPenetration (A, false);
			return;
		}
		searchContactVertices (B, A, contactVertices);
		if (contactCounter > 0){
			searchNormalAndPenetration (B, true);
			return;
		}
		
		manifold.areBodiesCollision = false;
	}
	
	private void searchContactVertices (Polygon prey, Polygon provocative, Vector2f[] contactVertices){
		//Выпускаем луч паралельный оси Х вправо, через точку возбудителя, если луч пересек нечетное число ребер то точка внутри полигона
		//коэфициенты прямой ax + by + c = 0
		//т.к. прямая паралельна оси Х, то уравнение примет вид y - y0 = 0, где y0 = координате у проверяемой точки
		//Время работы O(n)
		float y1, y2;
		float x1, x2;
		int counterIntersectsEdges;
		float coordinateIntersectsX;
		for (int i = 0; i < provocative.vertexCount; i++){
			tmpV.set (provocative.vertices[i]); //преобразовали координаты вершины провокатора в координаты жертвы
			provocative.rotateMatrix.mul (tmpV, tmpV);
			tmpV.add (provocative.body.position);
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
				contactVertices[contactCounter++].set (tmpV);
				
				if (contactCounter == 2){
					return;
				}
			}
		}
	}
	
	private void searchNormalAndPenetration (Polygon prey, boolean reverse){
		//Ищем ближайшее ребро полигона к точки контакта, проецируя точку на каждую нормаль ребра полигона
		float separation = Float.MAX_VALUE;
		int indexFace = 0;
		float dotProduct;
		
		for (int k = 0; k < contactCounter; k++){
			for (int i = 0; i < prey.vertexCount; i++){
				tmpV.set (contactVertices[k]);
				tmpV.sub (prey.vertices[i]);
				dotProduct = Vector2f.dotProduct (prey.normals[i], tmpV);
				
				//сохраняем ближайшее ребро к центру окружности, наслучай если центр внутри полигона,
				//обычное dotProduct > separation даёт не правильный ответ, т.к. все производные отрицательные,
				//а нам нужно меньшее по модулю
				if (Math.abs (dotProduct) < separation){
					separation = Math.abs (dotProduct);
					indexFace = i;
				}
			}
			
			manifold.contacts[k].set (contactVertices[k]); //восстанавливаем координаты точки касания в мировые кординаты
			prey.rotateMatrix.mul (manifold.contacts[k], manifold.contacts[k]);
			manifold.contacts[k].add (prey.body.position);
		}
		
		// m->normal = -(prey->u * prey->m_normals[faceNormal]);
		// m->contacts[0] = m->normal * A->radius + a->position;
		
		manifold.contactCount = contactCounter;
		prey.rotateMatrix.mul (prey.normals[indexFace], manifold.normal);
		if (reverse){
			manifold.normal.negative ();
		}
		float a;
		float b;
		float c;
		a = prey.vertices[(indexFace + 1) % prey.vertexCount].y - prey.vertices[indexFace].y;
		b = prey.vertices[indexFace].x - prey.vertices[(indexFace + 1) % prey.vertexCount].x;
		c = prey.vertices[(indexFace + 1) % prey.vertexCount].x * prey.vertices[indexFace].y;
		c -= prey.vertices[indexFace].x * prey.vertices[(indexFace + 1) % prey.vertexCount].y;
		
		manifold.penetration = Math.abs (a * contactVertices[0].x + b * contactVertices[0].y + c);
		manifold.penetration /= Math.sqrt (a * a + b * b);
	}
}

