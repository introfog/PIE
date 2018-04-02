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
		
		if (me.getButton () == MouseEvent.BUTTON1){
			/*Circle circle;
			float rand = (float) Math.random ();
			circle = new Circle (rand * 20f + 5f, mouseX, mouseY, rand * 15f + 1f);
			World.getInstance ().addBody (circle);*/
		}
		else if (me.getButton () == MouseEvent.BUTTON3){
			AABB aabb;
			float rand = (float) Math.random ();
			float height = rand * 80f + 20f;
			rand = (float) Math.random ();
			float width = rand * 80f + 20f;
			aabb = new AABB (mouseX - width / 2f, mouseY - height / 2, width, height, rand * 15f + 1f);
			World.getInstance ().addBody (aabb);
		}
	}
	
	@Override
	public void mouseReleased (MouseEvent me){ }
	
	@Override
	public void mouseDragged (MouseEvent me){ }
	
	@Override
	public void mouseMoved (MouseEvent me){ }
}
