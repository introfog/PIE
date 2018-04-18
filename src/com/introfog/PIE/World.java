package com.introfog.PIE;

import javafx.util.Pair;

import java.awt.*;
import java.util.*;

public class World{
	private int iterations = 1;
	private float accumulator;
	private LinkedList <Body> bodies;
	private LinkedList <Pair <Body, Body>> mayBeCollision;
	private LinkedList <Manifold> collisions;
	
	private LinkedList<Body> xAxisProjection;
	private LinkedList<Body> yAxisProjection;
	private LinkedList<Body> activeList;
	
	
	private void broadPhase_BruteForce (){ //сложность O(n^2)
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
	
	private void broadPhase_PartlySweepAndPrune (){ //Лучший случай O(n*logn) или O(k*n), в худщем O(n^2) частиный SAP, ищем возможные пересечения по оси Х, а потом bruteForce
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
	
	private void narrowPhase (){
		Manifold manifold;
		for (int i = 0; i < mayBeCollision.size (); i++){
			manifold = new Manifold (mayBeCollision.get (i).getKey (), mayBeCollision.get (i).getValue ());
			if (manifold.isCollision ()){
				collisions.add (manifold);
			}
		}
	}
	
	private void step (){ //physic simulation
		//broadPhase_BruteForce ();
		broadPhase_PartlySweepAndPrune ();
		narrowPhase ();
		mayBeCollision.clear ();
		
		//Integrate forces
		bodies.forEach ((body) -> integrateForces (body)); //Hanna modification Euler's method is used!
		
		//Initialize collisions
		collisions.forEach ((collision) -> collision.initializeCollision ());
		
		//Solve collisions
		for (int i = 0; i < iterations; i++){
			collisions.forEach ((collision) -> collision.solve ());
		}
		
		//Integrate velocities
		bodies.forEach ((body) -> integrateVelocity (body));
		
		//Integrate forces
		bodies.forEach ((body) -> integrateForces (body)); //Hanna modification Euler's method is used!
		
		//Correct positions
		collisions.forEach ((collision) -> collision.correctPosition ());
		
		//Clear all forces
		bodies.forEach ((body) -> body.force.set (0f, 0f));
		collisions.clear ();
	}
	
	private void integrateForces (Body b){
		if (b.invertMass == 0.0f){
			return;
		}
		
		b.velocity.add (b.force, b.invertMass * MathPIE.FIXED_DELTA_TIME * 0.5f);
		b.velocity.add (MathPIE.GRAVITY, MathPIE.FIXED_DELTA_TIME * 0.5f);
	}
	
	private void integrateVelocity (Body b){
		if (b.invertMass == 0.0f){
			return;
		}
		
		b.position.add (b.velocity, MathPIE.FIXED_DELTA_TIME);
		
		integrateForces (b);
	}
	
	private static class WorldHolder{
		private final static World instance = new World ();
	}
	
	private World (){
		bodies = new LinkedList <> ();
		mayBeCollision = new LinkedList <> ();
		collisions = new LinkedList <> ();
		
		xAxisProjection = new LinkedList <> ();
		yAxisProjection = new LinkedList <> ();
		activeList =  new LinkedList <> ();
	}
	
	
	public static World getInstance (){
		return WorldHolder.instance;
	}
	
	public void update (float deltaTime){
		//TODO добавить линейную интерполяцию
		
		accumulator += deltaTime;
		
		if (accumulator > MathPIE.DEAD_LOOP_BORDER){ //предотвращение петли смерти
			accumulator = MathPIE.DEAD_LOOP_BORDER;
		}
		
		while (accumulator > MathPIE.FIXED_DELTA_TIME){
			step (); //обновление физики всегда происходит через равный промежуток времени
			accumulator -= MathPIE.FIXED_DELTA_TIME;
		}
	}
	
	public void draw (Graphics graphics){
		bodies.forEach ((body) -> body.shape.render (graphics));
	}
	
	public void addBody (Shape shape){
		bodies.add (shape.body);
		
		xAxisProjection.add (shape.body);
		yAxisProjection.add (shape.body);
	}
	
	public int getAmountBodies (){
		return bodies.size ();
	}
	
	public void setIterations (int iterations){
		this.iterations = iterations;
	}
}