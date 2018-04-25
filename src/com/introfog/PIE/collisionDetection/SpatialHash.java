package com.introfog.PIE.collisionDetection;

import com.introfog.PIE.*;
import javafx.util.Pair;

import java.util.*;

public class SpatialHash{
	private int cellSize;
	private HashMap <Integer, LinkedList <Body>> dict;
	private HashMap <Body, LinkedList <Integer>> objects;
	LinkedHashSet <Pair <Body, Body>> collisionPairSet;
	
	private static final int BIG_ENOUGH_INT = 16 * 1024;
	private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT + 0.0000;
	
	private static int FastFloor (float f){
		return (int) (f + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}
	
	private int GenerateKey (float x, float y){
		return ((FastFloor (x / cellSize) * 73856093) ^ (FastFloor (y / cellSize) * 19349663));
	}
	
	
	public SpatialHash (int cellSize){
		this.cellSize = cellSize;
		dict = new HashMap <> ();
		objects = new HashMap <> ();
		collisionPairSet = new LinkedHashSet <> ();
	}
	
	public void Insert (Body body){
		body.shape.computeAABB ();
		AABB aabb = body.shape.aabb;
		float currX = aabb.body.position.x;
		float currY = aabb.body.position.y;
		int key;
		while (currX <= aabb.body.position.x + aabb.width + cellSize){ //TODO надо как-то обрабатывать случай, и не делать + cellSize
			while (currY <= aabb.body.position.y + aabb.height + cellSize){
				key = GenerateKey (currX, currY);
				
				if (dict.containsKey (key)){
					dict.get (key).add (body);
				}
				else{
					dict.put (key, new LinkedList <> ());
					dict.get (key).add (body);
				}
				
				if (objects.containsKey (body)){
					objects.get (body).add (key);
				}
				else{
					objects.put (body, new LinkedList <> ());
					objects.get (body).add (key);
				}
				
				currY += cellSize;
			}
			currY = aabb.body.position.y;
			currX += cellSize;
		}
	}
	
	public void UpdatePosition (Body body){
		if (objects.containsKey (body)){
			objects.get (body).forEach ((cell) -> dict.get (cell).remove (body));
		}
		Insert (body);
	}
	
	public LinkedHashSet <Pair <Body, Body>> ComputeCollisions (){
		collisionPairSet.clear ();
		dict.forEach ((cell, list) -> {
			for (int i = 0; i < list.size (); i++){
				for (int j = i + 1; j < list.size (); j++){
					collisionPairSet.add (new Pair <> (list.get (i), list.get (j)));
				}
			}
		});
		return collisionPairSet;
	}
}
