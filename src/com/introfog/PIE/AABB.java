package com.introfog.PIE;

import com.sun.javafx.geom.Vec2f;

public class AABB{ //Axis Aligned Bounding Box
	private Vec2f min;
	private Vec2f max;
	
	
	public AABB(float minX, float minY, float maxX, float maxY){
		min = new Vec2f (minX, minY);
		max = new Vec2f (maxX, maxY);
	}
	
	public AABB(Vec2f min, Vec2f max){
		this.min = new Vec2f (min);
		this.max = new Vec2f (max);
	}
	
	public static boolean isIntersected (AABB a, AABB b){
		if(a.max.x < b.min.x || a.min.x > b.max.x){
			return false;
		}
		if(a.max.y < b.min.y || a.min.y > b.max.y){
			return false;
		}
		return true;
	}
}
