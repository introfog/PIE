package com.introfog;

import com.introfog.PIE.*;
import com.introfog.PIE.Polygon;
import com.introfog.PIE.collisionDetection.BroadPhase;
import com.introfog.PIE.math.*;

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
	
	private float TIMER = 0.05f;
	private float timer = TIMER;
	private Polygon rectangle;
	private Circle circle;
	private float SIZE = 10f;
	
	private float START_Y_GENERATE_BODY = 100f;
	private float currYNewBody = START_Y_GENERATE_BODY;
	private float currXNewBody = 0f;
	private PrintWriter out;
	
	
	private void initializeBodies (){
		Circle circle;
		circle = new Circle (40f, 220f, 350f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		World.getInstance ().addShape (circle);
		
		circle = new Circle (30f, 300f, 300f, MathPIE.STATIC_BODY_DENSITY, 0.5f);
		World.getInstance ().addShape (circle);
		
		World.getInstance ().setIterations (1);
		
		Vector2f[] vertices = {new Vector2f (20f, -20f), new Vector2f (40f, 20f), new Vector2f (0f, 60f), new Vector2f (-60f, 40f), new Vector2f (-40f, 0f)};
		Polygon polygon = new Polygon (MathPIE.STATIC_BODY_DENSITY, 0.3f, 200f, 100f, vertices);
		World.getInstance ().addShape (polygon);
		
		rectangle = Polygon.generateRectangle (600f, 150f, 80f, 60f, MathPIE.STATIC_BODY_DENSITY, 0.3f);
		//World.getInstance ().addShape (rectangle);
	}
	
	private void testProductivity (){
		if (currYNewBody > (float) Main.WINDOW_HEIGHT - 3 * SIZE){
			currYNewBody = START_Y_GENERATE_BODY;
		}
		
		if (timer <= 0){
			timer = TIMER;
			if (currXNewBody * (SIZE + 1f) + SIZE >= (float) Main.WINDOW_WIDTH){
				currXNewBody = 0f;
				currYNewBody += SIZE + 1f;
			}
			
			if (World.getInstance ().getAmountBodies () % 2 == 0){
				rectangle = Polygon.generateRectangle (currXNewBody * (SIZE + 1f) + SIZE / 2f, currYNewBody + SIZE / 2f, SIZE / 2f, SIZE / 2f, 0.4f,
													   0.5f);
				World.getInstance ().addShape (rectangle);
				float dt = deltaTime * 100000;
				dt = Math.round (dt);
				dt /= 100000;
				out.print ("Bodies: " + World.getInstance ().getAmountBodies () + "\tdt: " + dt);
				out.println (
						"\tMay be collision bodies: " + World.getInstance ().amountMayBeCollisionBodies + "\tIntersects oper.:" + BroadPhase.INTERSECTED_COUNTER);
				out.flush ();
			}
			else{
				circle = new Circle (SIZE / 2f, currXNewBody * (SIZE + 1f) + SIZE / 2f, currYNewBody + SIZE / 2f, 0.4f,
									 0.5f);
				World.getInstance ().addShape (circle);
			}
			currXNewBody++;
		}
		timer -= deltaTime;
	}
	
	private void testBodiesPenetration (){
		if (timer <= 0){
			timer = TIMER * 10f;
			
			rectangle = Polygon.generateRectangle (400f, currYNewBody, SIZE, SIZE, 0.4f, 0.5f);
			World.getInstance ().addShape (rectangle);
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
		g.fillRect (0, 0, this.getWidth (), this.getHeight ());
		g.setColor (Color.BLACK);
		
		g.drawString ("FPS: " + (int) (1 / deltaTime), 2, 12);
		g.drawString ("Bodies: " + World.getInstance ().getAmountBodies (), 2, 24);
		g.drawString ("Version: 0.2.0 without some collision", 2, 36);
		
		//testProductivity ();
		//testBodiesPenetration ();
		
		//rectangle.setOrientation (rectangle.body.orientation + 0.001f);
		
		World.getInstance ().update (deltaTime);
		World.getInstance ().draw (g);
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}