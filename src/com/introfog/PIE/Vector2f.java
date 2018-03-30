package com.introfog.PIE;

public class Vector2f{
	public float x;
	public float y;
	
	
	public Vector2f (){
		this (0f, 0f);
	}
	
	public Vector2f (float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2f (Vector2f vector2f){
		this (vector2f.x, vector2f.y);
	}
	
	@Override
	public String toString (){
		return "Vector2f [" + x + "][" + y + "]";
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
	
	
	
	public float lengthWithoutSqrt (){
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
	
	public void subi (Vector2f vector2f){
		x -= vector2f.x;
		y -= vector2f.y;
	}
	
	public void adds (Vector2f vector2f, float s){
		x += vector2f.x * s;
		y += vector2f.y * s;
	}
	
	public void normalize (){
		float length = (float) Math.sqrt (lengthWithoutSqrt ());
		x /= length;
		y /= length;
	}
	
	public Vector2f returnMuli ( float s){
		return new Vector2f (x * s, y * s);
	}
	
	public static Vector2f mul (Vector2f a, Vector2f b){
		return new Vector2f (a.x * b.x, a.y * b.y);
	}
	
	public static float distanceWithoutSqrt (Vector2f a, Vector2f b){
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}
	
	public static float dotProduct (Vector2f a, Vector2f b){
		return a.x * b.x + a.y * b.y;
	}
}