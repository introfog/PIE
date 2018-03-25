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
	
	@Override
	public String toString (){
		return "Vector2f [" + x + "][" + y + "]";
	}
	
	
	public Vector2f (Vector2f vector2f){
		this.x = vector2f.x;
		this.y = vector2f.y;
	}
	
	public void set (float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void set (Vector2f vector2f){
		x = vector2f.x;
		y = vector2f.y;
	}
	
	public void set (Vector2f vector2f, float multiplier){
		x = vector2f.x * multiplier;
		y = vector2f.y * multiplier;
	}
	
	public Vector2f sub (Vector2f vector2f){
		return new Vector2f (this.x - vector2f.x, this.y - vector2f.y);
	}
	
	public float lengthSqrt (){
		return x * x + y * y;
	}
	
	public void divi (float value){
		x /= value;
		y /= value;
	}
	
	public void muli (float value){
		x *= value;
		y *= value;
	}
	
	public void addi (Vector2f vector2f){
		x += vector2f.x;
		y += vector2f.y;
	}
	
	
	public static float distanceSqrt (Vector2f a, Vector2f b){
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y)*(a.y - b.y);
	}
}