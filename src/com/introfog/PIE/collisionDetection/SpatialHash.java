package com.introfog.PIE.collisionDetection;

import com.introfog.PIE.*;
import javafx.util.Pair;

import java.util.*;

public class SpatialHash{
	private int cellSize;
	private HashMap <Integer, LinkedList <Body>> cells;
	private HashMap <Body, LinkedList <Integer>> objects;
	LinkedHashSet <Pair <Body, Body>> collisionPairSet;
	
	
	private int GenerateKey (float x, float y){
		return ((MathPIE.fastFloor (x / cellSize) * 73856093) ^ (MathPIE.fastFloor (y / cellSize) * 19349663));
	}
	
	
	public SpatialHash (){
		cells = new HashMap <> ();
		objects = new HashMap <> ();
		collisionPairSet = new LinkedHashSet <> ();
	}
	
	public void setCellSize (int cellSize){
		this.cellSize = cellSize;
	}
	
	public void insert (Body body){ //медленый из-за округления и умножения лишнего
		body.shape.computeAABB ();
		AABB aabb = body.shape.aabb;
		int key;
		int cellX = MathPIE.fastFloor ((aabb.body.position.x + aabb.width) / cellSize) - MathPIE.fastFloor (aabb.body.position.x / cellSize);
		int cellY = MathPIE.fastFloor ((aabb.body.position.y + aabb.height) / cellSize) - MathPIE.fastFloor (aabb.body.position.y / cellSize);
		for (int i = 0; i <= cellX; i++){
			for (int j = 0; j <= cellY; j++){
				key = GenerateKey (aabb.body.position.x + i * cellSize, aabb.body.position.y + j * cellSize);
				
				if (cells.containsKey (key)){
					cells.get (key).add (body);
				}
				else{
					cells.put (key, new LinkedList <> ());
					cells.get (key).add (body);
				}
				
				if (objects.containsKey (body)){
					objects.get (body).add (key);
				}
				else{
					objects.put (body, new LinkedList <> ());
					objects.get (body).add (key);
				}
			}
		}
	}
	
	public void optimizedInsert (Body body){
		//работает быстрее чем insert
		//делим AABB на ячейки, пришлось увиличить размер AABB на целую клетку, что бы не проверять дополнительно
		//лежит ли остаток AABB в новой ячейке.
		body.shape.computeAABB ();
		AABB aabb = body.shape.aabb;
		float currX = aabb.body.position.x;
		float currY = aabb.body.position.y;
		int key;
		while (currX <= aabb.body.position.x + aabb.width + cellSize){
			while (currY <= aabb.body.position.y + aabb.height + cellSize){
				BroadPhase.INTERSECTED_COUNTER++;
				key = GenerateKey (currX, currY);
				
				if (cells.containsKey (key)){
					cells.get (key).add (body);
				}
				else{
					cells.put (key, new LinkedList <> ());
					cells.get (key).add (body);
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
	
	public void clear (){
		cells.clear ();
		objects.clear ();
	}
	
	public LinkedHashSet <Pair <Body, Body>> computeCollisions (){
		//использую LinkedHashSet что бы избежать повторяющихся пар, это не очень быстро
		//TODO возможно есть более легкий способ избежать повтора пар кроме как использовать LinkedHashSet (какое-нить лексикографическое сравнение)
		collisionPairSet.clear ();
		cells.forEach ((cell, list) -> {
			for (int i = 0; i < list.size (); i++){
				for (int j = i + 1; j < list.size (); j++){
					BroadPhase.INTERSECTED_COUNTER++;
					collisionPairSet.add (new Pair <> (list.get (i), list.get (j)));
				}
			}
		});
		return collisionPairSet;
	}
}