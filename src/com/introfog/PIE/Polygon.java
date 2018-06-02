package com.introfog.PIE;

import com.introfog.PIE.math.*;

public class Polygon extends Shape{
	public int vertexCount;
	public Vector2f[] vertices = Vector2f.arrayOf (MathPIE.MAX_POLY_VERTEX_COUNT);
	public Vector2f[] normals = Vector2f.arrayOf (MathPIE.MAX_POLY_VERTEX_COUNT);
	
	public Vector2f tmpV = new Vector2f ();
	public Vector2f tmpV2 = new Vector2f ();
	
	//TODO у меня правильно создаётся фигура, если ее вершина расставлять последовательно по часовой стрелке т.к. берется левая нормаль
	//TODO реализовать поиск минимальной выпуклой оболочки (например алгоритм Грэхема)
	public Polygon (float density, float restitution, float centreX, float centreY, Vector2f... vertices){
		body = new Body (this, centreX, centreY, density, restitution);
		
		vertexCount = vertices.length;
		
		//находим самую верхнюю и правую координату, она станет стартовой точкой, и точно принадлежит МВО (мин. выпукл. оболочке)
		tmpV.y = Float.MAX_VALUE;
		tmpV.x = Float.MAX_VALUE;
		for (int i = 0; i < vertexCount; i++){
			if (tmpV.y < vertices[i].y){
				tmpV.set (vertices[i]);
			}
			else if (tmpV.y == vertices[i].y){
				if (tmpV.x > vertices[i].x){
					tmpV.set (vertices[i]);
				}
			}
		}
		
		
		
		for (int i = 0; i < vertexCount; i++){
			this.vertices[i].set (vertices[i]);
		}
		
		for (int i = 0; i < vertexCount; ++i){
			tmpV.set (vertices[(i + 1) % vertexCount]);
			tmpV.sub (vertices[i]);
			
			normals[i].set (tmpV.y, -tmpV.x); //берем левую нормаль
			normals[i].normalize ();
		}
		
		computeMass ();
		computeAABB ();
		
		type = Type.polygon;
	}
	
	public static Polygon generateRectangle (float centerX, float centerY, float width, float height, float density,
											 float restitution){
		Vector2f[] vertices = new Vector2f[4];
		vertices[0] = new Vector2f (-width / 2f, -height / 2f);
		vertices[1] = new Vector2f (width / 2f, -height / 2f);
		vertices[2] = new Vector2f (width / 2f, height / 2f);
		vertices[3] = new Vector2f (-width / 2f, height / 2f);
		return new Polygon (density, restitution, centerX, centerY, vertices);
	}
	
	public Vector2f getSupport (Vector2f dir){
		//Ищем самую удаленную точку в заданном направлении
		float bestProjection = -Float.MAX_VALUE;
		Vector2f bestVertex = new Vector2f ();
		
		for (int i = 0; i < vertexCount; ++i){
			Vector2f v = vertices[i];
			float projection = Vector2f.dotProduct (v, dir);
			
			if (projection > bestProjection){
				bestVertex.set (v);
				bestProjection = projection;
			}
		}
		
		return bestVertex;
	}
	
	@Override
	public void computeAABB (){
		aabb.min.x = Float.MAX_VALUE;
		aabb.min.y = Float.MAX_VALUE;
		
		aabb.max.x = -Float.MAX_VALUE;
		aabb.max.y = -Float.MAX_VALUE;
		for (int i = 0; i < vertexCount; i++){
			tmpV.set (vertices[i]);
			rotateMatrix.mul (tmpV, tmpV);
			if (tmpV.x < aabb.min.x){
				aabb.min.x = tmpV.x;
			}
			if (tmpV.y < aabb.min.y){
				aabb.min.y = tmpV.y;
			}
			if (tmpV.x > aabb.max.x){
				aabb.max.x = tmpV.x;
			}
			if (tmpV.y > aabb.max.y){
				aabb.max.y = tmpV.y;
			}
		}
		
		aabb.min.add (body.position);
		aabb.max.add (body.position);
	}
	
	@Override
	protected void computeMass (){
		float area = 0f;
		float I = 0f;
		final float k_inv3 = 1f / 3f;
		
		for (int i = 0; i < vertexCount; ++i){
			//Разбиваем выпуклый многоугольник на треугольники, у которых одна из точек (0, 0)
			Vector2f p1 = vertices[i];
			Vector2f p2 = vertices[(i + 1) % vertexCount];
			
			float D = Vector2f.crossProduct (p1, p2);
			float triangleArea = 0.5f * D;
			
			area += triangleArea;
			
			float intX2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x;
			float intY2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y;
			I += (0.25f * k_inv3 * D) * (intX2 + intY2);
		}
		
		float mass = body.density * area;
		body.invertMass = (mass != 0f) ? 1f / mass : 0f;
		float inertia = I * body.density;
		body.invertInertia = (inertia != 0f) ? 1f / inertia : 0f;
	}
}