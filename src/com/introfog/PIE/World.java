package com.introfog.PIE;

import java.awt.*;
import java.util.*;

public class World{
	public static final Vector2f GRAVITY = new Vector2f (0f, 50f); //9.807f
	
	private final float FIXED_DELTA_TIME = 1f / 60f;
	private final float DEAD_LOOP_BORDER = FIXED_DELTA_TIME * 10f;
	
	private float accumulator;
	private LinkedList <Body> bodies;
	private LinkedList <Manifold> collisions;
	
	
	private void step (){ //симуляция физики
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
		
		// Integrate forces
		bodies.forEach ((body) -> integrateForces (body));
		
		// Solve collisions
		collisions.forEach ((collision) -> collision.solve ());
		
		// Integrate velocities
		bodies.forEach ((body) -> integrateVelocity (body));
		
		// Clear all forces
		bodies.forEach ((body) -> body.force.set (0f, 0f));
	}
	
	private void integrateForces (Body b){
		if (b.invertMass == 0.0f){
			return;
		}
		
		b.velocity.add (b.force, b.invertMass * FIXED_DELTA_TIME);
		b.velocity.add (GRAVITY, FIXED_DELTA_TIME);
	}
	
	private void integrateVelocity (Body b){
		if (b.invertMass == 0.0f){
			return;
		}
		
		b.position.add (b.velocity, FIXED_DELTA_TIME);
		
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
		
		if (accumulator > DEAD_LOOP_BORDER){ //предотвращение петли смерти
			accumulator = DEAD_LOOP_BORDER;
		}
		
		while (accumulator > FIXED_DELTA_TIME){
			step (); //обновление физики всегда происходит через равный промежуток времени
			accumulator -= FIXED_DELTA_TIME;
		}
	}
	
	public void draw (Graphics graphics){
		for (Body body : bodies){
			body.draw (graphics);
		}
	}
	
	public void addBody (Body body){
		bodies.add (body);
	}
}