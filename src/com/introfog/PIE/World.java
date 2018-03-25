package com.introfog.PIE;

import java.awt.*;
import java.util.ArrayList;

public class World{
	public static Vector2f gravity = new Vector2f (0f, 50f); //9.807f
	
	private final float FIXED_DELTA_TIME = 1f / 60f;
	private final float DEAD_LOOP_BORDER = FIXED_DELTA_TIME * 10f;
	
	private float accumulator;
	private ArrayList <Body> bodies;
	private ArrayList <Manifold> collisions;
	
	
	private void step (float deltaTime){ //симуляция физики
		for (int i = 0; i < bodies.size (); i++){
			Body a = bodies.get (i);
			for (int j = i + 1; j < bodies.size (); ++j){
				Body b = bodies.get (j);
				
				if (a.invertMass == 0 && b.invertMass == 0){
					continue;
				}
				
				Manifold m = new Manifold (a, b);
				m.solve ();
				
				if (m.contactCount > 0){
					collisions.add (m);
				}
			}
		}
		
		for (Body body : bodies){
			body.update (deltaTime);
		}
	}
	
	
	private static class WorldHolder{
		private final static World instance = new World ();
	}
	
	private World (){
		bodies = new ArrayList <> ();
		collisions = new ArrayList <> ();
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
			step (FIXED_DELTA_TIME); //обновление физики всегда происходит через равный промежуток времени
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
