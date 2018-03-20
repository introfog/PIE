package com.introfog.PIE;

import com.sun.javafx.geom.Vec2f;

import java.awt.*;
import java.util.ArrayList;

public class World{
	public static Vec2f gravity = new Vec2f (0f, 50f); //9.807f
	private ArrayList<Circle> circles;
	
	
	public World (){
		circles = new ArrayList <> ();
	}
	
	public void addCircle (Circle circle){
		circles.add (circle);
	}
	
	public void update (float deltaTime){
		for (Circle circle : circles){
			circle.update (deltaTime);
		}
	}
	
	public void draw (Graphics graphics){
		for (Circle circle :circles){
			graphics.drawOval ((int) (circle.centre.x - circle.radius), (int) (circle.centre.y - circle.radius),
							   (int) circle.radius, (int) circle.radius);
			graphics.drawString (circle.velocity.x + " " + circle.velocity.y,300, 50);
		}
	}
}
