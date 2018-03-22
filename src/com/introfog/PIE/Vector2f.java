package com.introfog.PIE;

public class Vector2f{
	public float x;
	public float y;
	
	
	public Vector2f (){
		x = 0;
		y = 0;
	}
	
	public Vector2f (float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2f (Vector2f vector2f){
		this.x = vector2f.x;
		this.y = vector2f.y;
	}
	
	public static float distanceWithoutSqrt (Vector2f a, Vector2f b){
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y)*(a.y - b.y);
	}
}