package com.introfog;

import com.introfog.PIE.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Display extends JPanel implements ActionListener{
	private boolean isFirstUpdate = true;
	private float deltaTime;
	private long previousTime = 0L;
	
	private float TIMER = 0.1f;
	private float timer = TIMER;
	private AABB aabb;
	private Circle circle;
	private float SIZE = 10f;
	private float currYNewBody = 100f;
	private float currXNewBody = 0f;
	private PrintWriter out;
	
	
	private void initializeBodies (){
		Circle circle;
		circle = new Circle (40f, 220f, 350f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		World.getInstance ().addBody (circle);
		
		AABB aabb;
		aabb = new AABB (200f, 450f, 300f, 20f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		World.getInstance ().addBody (aabb);
		
		/*circle = new Circle (30f, 400f, 200f, 0.4f, 0.5f);
		World.getInstance ().addBody (circle);*/
		
		World.getInstance ().setIterations (1);
	}
	
	private void generateBodies (){
		if (timer <= 0){
			timer = TIMER;
			if (currXNewBody * (SIZE + 1f) + SIZE >= (float) Main.WINDOW_WIDTH){
				currXNewBody = 0f;
				currYNewBody += SIZE + 1f;
			}
			
			if (World.getInstance ().getAmountBodies () % 2 == 0){
				aabb = new AABB (currXNewBody * (SIZE + 1f), currYNewBody, SIZE, SIZE, 0.4f, 0.5f);
				World.getInstance ().addBody (aabb);
				float dt = deltaTime * 100000;
				dt = Math.round (dt);
				dt /= 100000;
				out.println ("Amount of bodies: " + World.getInstance ().getAmountBodies () + " deltaTime: " + dt);
				out.flush ();
			}
			else{
				circle = new Circle (SIZE / 2f, currXNewBody * (SIZE + 1f) + SIZE / 2f, currYNewBody + SIZE / 2f, 0.4f, 0.5f);
				World.getInstance ().addBody (circle);
			}
			currXNewBody++;
		}
		timer -= deltaTime;
	}
	
	
	public Display (){
		Timer timer = new Timer (0, this);
		timer.start ();
		addMouseListener (new MouseEvents ());
		
		initializeBodies ();
		
		try{
			out = new PrintWriter (new FileWriter (".\\tests\\Test something.txt"));
		}
		catch (IOException e){
			System.out.println ("Error with new FileWriter");
		}
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
		
		generateBodies ();
		
		World.getInstance ().update (deltaTime);
		World.getInstance ().draw (g);
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}