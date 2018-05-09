package com.introfog.PIE;

import com.introfog.PIE.math.*;

import java.awt.*;

public class Polygon extends Shape{
	public int vertexCount;
	public Vector2f[] vertices = Vector2f.arrayOf (MathPIE.MAX_POLY_VERTEX_COUNT);
	public Vector2f[] normals = Vector2f.arrayOf (MathPIE.MAX_POLY_VERTEX_COUNT);
	
	
	public Polygon (float density, float restitution, float centreX, float centreY, Vector2f... vertices){
		body = new Body (this, centreX, centreY, density, restitution);
		aabb = new AABB ();
		
		vertexCount = vertices.length;
		for (int i = 0; i < vertexCount; i++){
			this.vertices[i].set (vertices[i]);
		}
		
		computeMass ();
		//computeAABB ();
		
		type = Type.polygon;
	}
	
	public static Polygon generateRectangle (float centerX, float centerY, float width, float height, float density, float restitution){
		Vector2f[] vertices = {
				new Vector2f (-width / 2f, - height / 2f),
				new Vector2f (width / 2f, -height / 2f),
				new Vector2f (width / 2f, height / 2f),
				new Vector2f (-width / 2f, height / 2f)};
		return new Polygon (density, restitution, centerX, centerY, vertices);
	}
	
	@Override
	public void render (Graphics graphics){
		graphics.setColor (Color.GRAY);
		graphics.drawRect ((int) aabb.min.x, (int) aabb.min.y, (int) (aabb.max.x - aabb.min.x), (int) (aabb.max.y - aabb.min.y));
		graphics.setColor (Color.BLUE);
		
		for (int i = 0; i < vertexCount; i++){
			Vector2f v = new Vector2f (vertices[i]);
			rotateMatrix.mul (v, v);
			v.add (body.position);
			
			Vector2f v2 = new Vector2f (vertices[(i + 1) % vertexCount]);
			rotateMatrix.mul (v2, v2);
			v2.add (body.position);
			graphics.drawLine ((int) v.x, (int) v.y, (int) v2.x, (int) v2.y);
		}
		
		graphics.drawLine ((int) body.position.x, (int) body.position.y, (int) body.position.x, (int) body.position.y);
	}
	
	@Override
	public void computeAABB (){
		aabb.min.x = Float.MAX_VALUE;
		aabb.min.y = Float.MAX_VALUE;
		
		aabb.max.x = Float.MIN_VALUE;
		aabb.max.y = Float.MIN_VALUE;
		for (int i = 0; i < vertexCount; i++){
			if (vertices[i].x < aabb.min.x){
				aabb.min.x = vertices[i].x;
			}
			if (vertices[i].y < aabb.min.y){
				aabb.min.y = vertices[i].y;
			}
			if (vertices[i].x > aabb.max.x){
				aabb.max.x = vertices[i].x;
			}
			if (vertices[i].y > aabb.max.y){
				aabb.max.y = vertices[i].y;
			}
		}
		
		aabb.min.add (body.position);
		aabb.max.add (body.position);
	}
	
	@Override
	protected void computeMass (){
		float area = 0.0f;
		float I = 0.0f;
		final float k_inv3 = 1.0f / 3.0f;
		
		for (int i = 0; i < vertexCount; ++i)
		{
			//Разбиваем выпуклый многоугольник на треугольники, у которых одна из точек (0, 0)
			Vector2f p1 = vertices[i];
			Vector2f p2 = vertices[(i + 1) % vertexCount];
			
			float D = Vector2f.crossProduct ( p1, p2 );
			float triangleArea = 0.5f * D;
			
			area += triangleArea;
			
			float intx2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x;
			float inty2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y;
			I += (0.25f * k_inv3 * D) * (intx2 + inty2);
		}
		
		float mass = body.density * area;
		body.invertMass = (mass != 0.0f) ? 1.0f / mass : 0.0f;
		float inertia = I * body.density;
		body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
	}
}