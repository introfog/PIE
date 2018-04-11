package com.introfog.PIE;

import java.awt.*;
import java.util.*;

public class World{
	private int iterations = 1;
	private float accumulator;
	private LinkedList <Body> bodies;
	private LinkedList <Manifold> collisions;
	
	
	private void step (){ //physic simulation
		for (int i = 0; i < bodies.size (); i++){
			Body a = bodies.get (i);
			for (int j = i + 1; j < bodies.size (); ++j){
				Body b = bodies.get (j);
				
				if (a.invertMass == 0 && b.invertMass == 0){
					continue;
				}
				
				Manifold m = new Manifold (a, b);
				
				if (m.isCollision ()){
					collisions.add (m);
				}
			}
		}
		
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
		collisions = new LinkedList <> ();
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
	}
	
	public int getAmountBodies (){
		return bodies.size ();
	}
	
	public void setIterations (int iterations){
		this.iterations = iterations;
	}
}