package com.introfog.PIE.collisionDetection;

import com.introfog.PIE.Vector2f;

import java.util.*;

public class SpatialHash <T>{
	private HashMap <Integer, LinkedList <T>> dict;
	private HashMap <T, Integer> objects;
	private int cellSize;
	
	public SpatialHash (int cellSize){
		this.cellSize = cellSize;
		dict = new HashMap <> ();
		objects = new HashMap <> ();
	}
	
	public void Insert (Vector2f vector, T obj){
		int key = Key (vector);
		if (dict.containsKey (key)){
			dict.get (key).add (obj);
		}
		else{
			dict.put (key, new LinkedList <> ());
			dict.get (key).add (obj);
		}
		
		objects.put (obj, key);
	}
	
	public void UpdatePosition (Vector2f vector, T obj){
		if (objects.containsKey (obj)){
			dict.get (objects.get (obj)).remove (obj);
		}
		Insert (vector, obj);
	}
	
	public LinkedList <T> QueryPosition (Vector2f vector){
		int key = Key (vector);
		return dict.containsKey (key) ? dict.get (key) : new LinkedList <> ();
	}
	
	public boolean ContainsKey (Vector2f vector){
		return dict.containsKey (Key (vector));
	}
	
	public void Clear (){
		dict.forEach ((key, value) -> value.clear ());
		objects.clear ();
	}
	
	public void Reset (){
		dict.clear ();
		objects.clear ();
	}
	
	private static final int BIG_ENOUGH_INT = 16 * 1024;
	private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT + 0.0000;
	
	private static int FastFloor (float f){
		return (int) (f + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}
	
	private int Key (Vector2f v){
		return ((FastFloor (v.x / cellSize) * 73856093) ^ (FastFloor (v.y / cellSize) * 19349663));
	}
}
