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
		
		body.invertMass = 0.001f;
		//computeMass ();
		//computeAABB ();
		
		type = Type.polygon;
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
	protected void computeMass (){ }
}
