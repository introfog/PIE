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
		circle = new Circle (40f, 240f, 350f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		World.getInstance ().addBody (circle);
		
		AABB aabb;
		aabb = new AABB (200f, 450f, 300f, 20f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		World.getInstance ().addBody (aabb);
		
		World.getInstance ().setIterations (1);
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
		
		g.drawString ("FPS: " + (int) (1 / deltaTime), 2, 12);
		g.drawString ("Bodies: " + World.getInstance ().getAmountBodies (), 2, 24);
		g.drawString ("Version: 0.1.0 without rotation & friction", 2, 36);
		
		World.getInstance ().update (deltaTime);
		World.getInstance ().draw (g);
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}