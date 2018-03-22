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
	
	private World world;
	
	
	public Display (){
		Timer timer = new Timer (0, this);
		timer.start ();
		
		
		world = new World ();
		AABB rect = new AABB (50f, 50f, 100f, 100f, 10);
		world.addBody (rect);
		rect = new AABB (25, 400, 300, 450, Body.INFINITY_MASS);
		world.addBody (rect);
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
		
		world.update (deltaTime);
		world.draw (g);
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}
