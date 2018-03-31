package com.introfog;

import com.introfog.PIE.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Display extends JPanel implements ActionListener{
	private boolean isFirstUpdate = true;
	private float deltaTime;
	private long previousTime = 0L;
	
	
	private void initializeBodies (){
		Circle circle;
		circle = new Circle (50f, 100f, 400f, Body.INFINITY_MASS);
		World.getInstance ().addBody (circle);
		circle = new Circle (40f, 200f, 400f, Body.INFINITY_MASS);
		World.getInstance ().addBody (circle);
		circle = new Circle (40f, 500f, 400f, Body.INFINITY_MASS);
		World.getInstance ().addBody (circle);
	}
	
	
	public Display (){
		Timer timer = new Timer (0, this);
		timer.start ();
		addMouseListener (new MouseEvents ());
		
		initializeBodies ();
	}
	
	public void paint (Graphics g){
		if (isFirstUpdate){
			isFirstUpdate = false;
			previousTime = System.nanoTime ();
			return;
		}
		
		long currentTime = System.nanoTime ();
		deltaTime = (currentTime - previousTime) / 1_000_000_000f;
		previousTime = currentTime;
		
		
		g.setColor (Color.WHITE);
		g.fillRect (0,0, this.getWidth (), this.getHeight ());
		g.setColor (Color.BLACK);
		
		g.drawString ("" + (int) (1 / deltaTime), 0, 10);
		
		World.getInstance ().update (deltaTime);
		World.getInstance ().draw (g);
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}