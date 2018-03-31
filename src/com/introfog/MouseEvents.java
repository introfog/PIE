package com.introfog;

import com.introfog.PIE.*;

import java.awt.event.*;
import java.applet.*;

public class MouseEvents extends Applet implements MouseListener, MouseMotionListener{
	@Override
	public void init (){
		addMouseListener (this);
		addMouseMotionListener (this);
	}
	
	@Override
	public void mouseClicked (MouseEvent me){ }
	
	@Override
	public void mouseEntered (MouseEvent me){ }
	
	@Override
	public void mouseExited (MouseEvent me){ }
	
	@Override
	public void mousePressed (MouseEvent me){
		float mouseX = me.getX ();
		float mouseY = me.getY ();
		
		Circle circle;
		circle = new Circle ((float) Math.random () * 20f + 5f, mouseX, mouseY, 2f);
		World.getInstance ().addBody (circle);
	}
	
	@Override
	public void mouseReleased (MouseEvent me){ }
	
	@Override
	public void mouseDragged (MouseEvent me){ }
	
	@Override
	public void mouseMoved (MouseEvent me){ }
}
