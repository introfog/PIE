package com.introfog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Display extends JPanel implements ActionListener{
	private boolean isFirstUpdate = true;
	private float deltaTime;
	private float x;
	private long previousTime = 0L;
	
	
	public Display (){
		Timer timer = new Timer (0, this);
		timer.start ();
		
		x = 0;
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
		
		g.drawString ("" + (int) (1 / deltaTime), 200, 200);
		g.drawLine ((int) x, 0, 100, 100);
		x += deltaTime * 10L;
	}
	
	@Override
	public void actionPerformed (ActionEvent e){
		repaint ();
	}
}
