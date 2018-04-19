package com.introfog.PIE;

import javafx.util.Pair;

import java.util.LinkedList;

public class BroadPhase{
	private LinkedList <Body> bodies;
	
	//for my realisation sweep and prune method
	private LinkedList <Body> xAxisProjection;
	private LinkedList <Body> yAxisProjection;
	private LinkedList <Body> activeList;
	
	//for sweep and prune method (dispersion)
	private int CURRENT_AXIS = 0;
	private Vector2f p = new Vector2f ();
	private Vector2f s = new Vector2f ();
	private Vector2f s2 = new Vector2f ();
	
	
	public BroadPhase (LinkedList <Body> bodies){
		this.bodies = bodies;
		
		xAxisProjection = new LinkedList <> ();
		yAxisProjection = new LinkedList <> ();
		activeList = new LinkedList <> ();
	}
	
	public void bruteForce (LinkedList <Pair <Body, Body>> mayBeCollision){ //сложность O(n^2)
		Body a;
		Body b;
		for (int i = 0; i < bodies.size (); i++){
			for (int j = i + 1; j < bodies.size (); j++){
				a = bodies.get (i);
				b = bodies.get (j);
				
				if (a.invertMass == 0f && b.invertMass == 0f){
					continue;
				}
				
				a.shape.computeAABB ();
				b.shape.computeAABB ();
				
				if (AABB.isIntersected (a.shape.aabb, b.shape.aabb)){
					mayBeCollision.add (new Pair <> (a, b));
				}
			}
		}
	}
	
	public void myRealisationSweepAndPrune (LinkedList <Pair <Body, Body>> mayBeCollision){
		//Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем
		// возможные пересечения по оси Х, а потом bruteForce
		bodies.forEach ((body) -> body.shape.computeAABB ());
		xAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.body.position.x - b.shape.aabb.body.position.x));
		//TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)
		
		activeList.add (xAxisProjection.getFirst ());
		float currEnd = xAxisProjection.getFirst ().shape.aabb.body.position.x + xAxisProjection.getFirst ().shape.aabb.width;
		
		for (int i = 1; i < xAxisProjection.size (); i++){
			if (xAxisProjection.get (i).shape.aabb.body.position.x <= currEnd){
				activeList.add (xAxisProjection.get (i));
			}
			else{
				Body first = activeList.removeFirst ();
				activeList.forEach ((body) -> {
					if (AABB.isIntersected (first.shape.aabb, body.shape.aabb)){
						mayBeCollision.add (new Pair <> (first, body));
					}
				});
				if (!activeList.isEmpty ()){
					i--;
				}
				else{
					activeList.add (xAxisProjection.get (i));
				}
				currEnd = activeList.getFirst ().shape.aabb.body.position.x + activeList.getFirst ().shape.aabb.width;
			}
		}
		if (!activeList.isEmpty ()){
			int size = activeList.size ();
			for (int i = 0; i < size; i++){
				Body first = activeList.removeFirst ();
				activeList.forEach ((body) -> {
					if (AABB.isIntersected (first.shape.aabb, body.shape.aabb)){
						mayBeCollision.add (new Pair <> (first, body));
					}
				});
			}
		}
		
		activeList.clear ();
	}
	
	public void sweepAndPrune (LinkedList <Pair <Body, Body>> mayBeCollision){
		//Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем возможные
		// пересечения по текущей оси, а потом bruteForce. Каждый раз через десперсию выбираем следующую ось
		bodies.forEach ((body) -> body.shape.computeAABB ());
		
		if (CURRENT_AXIS == 0){
			xAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.body.position.x - b.shape.aabb.body.position.x));
		}
		else{
			yAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.body.position.y - b.shape.aabb.body.position.y));
		}
		//TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)
		
		p.set (0f, 0f);
		s.set (0f, 0f);
		s2.set (0f, 0f);
		float numBodies = bodies.size ();
		
		AABB currAABB;
		for (int i = 0; i < bodies.size (); i++){
			if (CURRENT_AXIS == 0){
				currAABB = xAxisProjection.get (i).shape.aabb;
			}
			else{
				currAABB = yAxisProjection.get (i).shape.aabb;
			}
			
			p.x = (currAABB.body.position.x + currAABB.width / 2f) / numBodies;
			p.y = (currAABB.body.position.y + currAABB.height / 2f) / numBodies;
			
			s.add (p);
			p.x *= p.x * numBodies;
			p.y *= p.y * numBodies;
			s2.add (p);
			
			for (int j = i + 1; j < bodies.size (); j++){
				if (CURRENT_AXIS == 0 && xAxisProjection.get (
						j).shape.aabb.body.position.x > currAABB.body.position.x + currAABB.width){
					break;
				}
				else if (yAxisProjection.get (
						j).shape.aabb.body.position.y > currAABB.body.position.y + currAABB.height){
					break;
				}
				
				if (CURRENT_AXIS == 0 && AABB.isIntersected (xAxisProjection.get (j).shape.aabb, currAABB)){
					mayBeCollision.add (new Pair <> (xAxisProjection.get (j), xAxisProjection.get (i)));
				}
				else if (AABB.isIntersected (yAxisProjection.get (j).shape.aabb, currAABB)){
					mayBeCollision.add (new Pair <> (yAxisProjection.get (j), yAxisProjection.get (i)));
				}
			}
		}
		
		s.x *= s.x; //с помощью дисперсии выбираем следуюущую ось (ищем ось, по которой координаты объектов больше
		s.y *= s.y; //всего различаются)
		s2.sub (s);
		CURRENT_AXIS = 0;
		if (s.y > s.x){
			CURRENT_AXIS = 1;
		}
	}
	
	public void addBody (Shape shape){
		xAxisProjection.add (shape.body);
		yAxisProjection.add (shape.body);
	}
}
