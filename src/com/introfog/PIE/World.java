package com.introfog.PIE;

import java.awt.*;
import java.util.ArrayList;

public class World{
	public static Vector2f gravity = new Vector2f (0f, 50f); //9.807f
	public static final float INIFINITY_MASS = 0f;
	
	private final float FIXED_DELTA_TIME = 1f / 60f;
	private final float DEAD_LOOP_BORDER = FIXED_DELTA_TIME * 10f;
	
	private float accumulator;
	private ArrayList<Circle> circles;
	
	
	private void step (float deltaTime){ //симуляция физики
		for (Circle circle : circles){
			circle.update (deltaTime);
		}
	}
	
	
	public World (){
		circles = new ArrayList <> ();
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
		for (Circle circle :circles){
			graphics.drawOval ((int) (circle.centre.x - circle.radius), (int) (circle.centre.y - circle.radius),
							   (int) circle.radius, (int) circle.radius);
			//graphics.drawString (circle.velocity.x + " " + circle.velocity.y,300, 50);
		}
	}
	
	public void addCircle (Circle circle){
		circles.add (circle);
	}
}
