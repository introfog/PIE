package com.introfog.PIE;

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
		aabb = new AABB ();
		
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
		for (int i = 0; i < vertexCount; i++){
			Vector2f v = new Vector2f (vertices[i]);
			v.add (body.position);
			
			Vector2f v2 = new Vector2f (vertices[(i + 1) % vertexCount]);
			v2.add (body.position);
			graphics.drawLine ((int) v.x, (int) v.y, (int) v2.x, (int) v2.y);
		}
		
		graphics.drawLine ((int) body.position.x, (int) body.position.y, (int) body.position.x, (int) body.position.y);
	}
	
	@Override
	public void computeAABB (){
	
	}
	
	@Override
	public void setOrientation (float radian){ }
	
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