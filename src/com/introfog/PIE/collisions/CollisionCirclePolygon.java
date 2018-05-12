package com.introfog.PIE.collisions;

import com.introfog.PIE.*;
import com.introfog.PIE.math.*;

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
		int indexFaceNormal = 0;
		float dotProduct;
		Vector2f projection = new Vector2f ();
		Vector2f realProjection = new Vector2f ();
		Vector2f tmpV = new Vector2f ();
		for (int i = 0; i < B.vertexCount; i++){
			tmpV.set (centerA);
			tmpV.sub (B.vertices[i]);
			dotProduct = Vector2f.dotProduct (B.normals[i], tmpV);
			projection.x = dotProduct * B.normals[i].x;
			projection.y = dotProduct * B.normals[i].y;
			
			//знак скалярного произведение показывает, находиться ли центр с противоположной стороны прямой от нормали
			if (dotProduct > 0f && projection.lengthWithoutSqrt () > A.radius * A.radius){
				manifold.areBodiesCollision = false;
				return;
			}
			
			if (dotProduct > separation){
				realProjection.set (projection);
				separation = dotProduct;
				indexFaceNormal = i;
			}
		}
		
		//если max скалярное произведение меньше 0, то значит центр круга внутри полигона
		if (separation < MathPIE.EPSILON){
			// m->normal = -(B->u * B->m_normals[faceNormal]);
			// m->contacts[0] = m->normal * A->radius + a->position;
			
			manifold.contactCount = 1;
			B.rotateMatrix.mul (B.normals[indexFaceNormal], manifold.normal);
			manifold.normal.negative ();
			
			manifold.contacts[0].set (manifold.normal);
			manifold.contacts[0].mul (A.radius);
			manifold.contacts[0].add (A.body.position);
			manifold.penetration = A.radius;
			return;
		}
		
		//Мы нашли ближайшее ребро к центру окржуности, и центр круга лежит снаружи полигона
		//Теперь определяем область Вороного
		Vector2f v1 = B.vertices[indexFaceNormal];
		Vector2f v2 = B.vertices[(indexFaceNormal + 1) % B.vertexCount];
		
		float dot1 = Vector2f.dotProduct (Vector2f.sub (centerA, v1), Vector2f.sub (v2, v1));
		float dot2 = Vector2f.dotProduct (Vector2f.sub (centerA, v2), Vector2f.sub (v1, v2));
		manifold.penetration = A.radius - (float) Math.sqrt (realProjection.lengthWithoutSqrt ());
		
		if (dot1 <= 0f){ //ближе к первой вершине
			if (Vector2f.distanceWithoutSqrt (centerA, v1) > A.radius * A.radius){
				manifold.areBodiesCollision = false;
				return;
			}
			
			manifold.contactCount = 1;
			// Vec2 n = v1 - center;
			// n = B->u * n;
			// n.Normalize( );
			// m->normal = n;
			// v1 = B->u * v1 + b->position;
			// m->contacts[0] = v1;
			Vector2f n = Vector2f.sub (v1, centerA);
			B.rotateMatrix.mul (n, n);
			n.normalize ();
			manifold.normal.set (n);
			B.rotateMatrix.mul (v1, v1);
			v1.add (B.body.position);
			manifold.contacts[0].set (v1);
		}
		else if (dot2 <= 0f){ //ближе ко второй вершине
			if (Vector2f.distanceWithoutSqrt (centerA, v1) > A.radius * A.radius){
				manifold.areBodiesCollision = false;
				return;
			}
			
			manifold.contactCount = 1;
			// Vec2 n = v2 - center;
			// v2 = B->u * v2 + b->position;
			// m->contacts[0] = v2;
			// n = B->u * n;
			// n.Normalize( );
			// m->normal = n;
			Vector2f n = Vector2f.sub (v2, centerA);
			B.rotateMatrix.mul (n, n);
			n.normalize ();
			manifold.normal.set (n);
			B.rotateMatrix.mul (v2, v2);
			v2.add (B.body.position);
			manifold.contacts[0].set (v2);
		}
		else{ //ближе к лицевой стороне
			Vector2f n = new Vector2f (B.normals[indexFaceNormal]);
			
			if (Vector2f.dotProduct (Vector2f.sub (centerA, v1), n) > A.radius){
				manifold.areBodiesCollision = false;
				return;
			}
			
			manifold.contactCount = 1;
			// n = B->u * n;
			// m->normal = -n;
			// m->contacts[0] = m->normal * A->radius + a->position;
			B.rotateMatrix.mul (n, n);
			n.negative ();
			manifold.normal.set (n);
			manifold.contacts[0].set (manifold.normal);
			manifold.contacts[0].mul (A.radius);
			manifold.contacts[0].add (A.body.position);
		}
	}
}
