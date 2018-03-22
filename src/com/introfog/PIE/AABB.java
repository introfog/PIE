package com.introfog.PIE;

public class AABB{ //Axis Aligned Bounding Box
	private Vector2f min;
	private Vector2f max;
	
	
	public AABB(float minX, float minY, float maxX, float maxY){
		min = new Vector2f (minX, minY);
		max = new Vector2f (maxX, maxY);
	}
	
	public AABB(Vector2f min, Vector2f max){
		this.min = new Vector2f (min);
		this.max = new Vector2f (max);
	}
	
	public static boolean isIntersected (AABB a, AABB b){ //TODO изменить название метода
		if(a.max.x < b.min.x || a.min.x > b.max.x){
			return false;
		}
		if(a.max.y < b.min.y || a.min.y > b.max.y){
			return false;
		}
		return true;
	}
}
