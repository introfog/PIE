package com.introfog.PIE;

import com.introfog.PIE.collisionDetection.BroadPhase;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;

public class World{
	private int iterations = 1;
	private float accumulator;
	private LinkedList <Body> bodies;
	private LinkedList <Pair <Body, Body>> mayBeCollision;
	private LinkedList <Manifold> collisions;
	private BroadPhase broadPhase;
	
	public int amountMayBeCollisionBodies = 0;
	
	
	private void narrowPhase (){
		amountMayBeCollisionBodies = mayBeCollision.size ();
		
		mayBeCollision.forEach ((collision) -> {
			if (collision.getKey ().invertMass != 0f || collision.getValue ().invertMass != 0f){
				Manifold manifold = new Manifold (collision.getKey (), collision.getValue ());
				collisions.add (manifold);
			}
		});
	}
	
	private void step (){
		//broadPhase.bruteForce (mayBeCollision);
		//broadPhase.sweepAndPruneMyRealisation (mayBeCollision);
		//broadPhase.sweepAndPrune (mayBeCollision);
		broadPhase.spatialHashing (mayBeCollision);
		
		narrowPhase ();
		mayBeCollision.clear ();
		
		
		//Integrate forces
		bodies.forEach ((body) -> integrateForces (body)); //Hanna modification Euler's method is used!
		
		//Initialize collisions
		collisions.forEach ((collision) -> collision.initializeCollision ());
		
		//Solve collisions
		for (int i = 0; i < iterations; i++){
			collisions.forEach ((collision) -> {
				if (collision.areBodiesCollision){
					collision.solve ();
				}
			});
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
		b.angularVelocity += b.torque * b.invertInertia * MathPIE.FIXED_DELTA_TIME * 0.5f;
	}
	
	private void integrateVelocity (Body b){
		if (b.invertMass == 0.0f){
			return;
		}
		
		b.position.add (b.velocity, MathPIE.FIXED_DELTA_TIME);
		b.orientation += b.angularVelocity * MathPIE.FIXED_DELTA_TIME;
		b.setOrientation (b.orientation);
		
		integrateForces (b);
	}
	
	private static class WorldHolder{
		private final static World instance = new World ();
	}
	
	private World (){
		bodies = new LinkedList <> ();
		mayBeCollision = new LinkedList <> ();
		collisions = new LinkedList <> ();
		broadPhase = new BroadPhase (bodies);
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
	
	public void addShape (Shape shape){
		bodies.add (shape.body);
		broadPhase.addBody (shape);
	}
	
	public int getAmountBodies (){
		return bodies.size ();
	}
	
	public void setIterations (int iterations){
		this.iterations = iterations;
	}
}