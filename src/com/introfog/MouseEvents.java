package com.introfog;

import com.introfog.PIE.*;

import java.awt.event.*;
import java.applet.*;

public class MouseEvents extends Applet implements MouseListener{
	@Override
	public void init (){
		addMouseListener (this);
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
			Circle circle;
			float rand = (float) Math.random ();
			circle = new Circle (rand * 20f + 5f, mouseX, mouseY,  0.4f, 0.5f);
			World.getInstance ().addBody (circle);
		}
		else if (me.getButton () == MouseEvent.BUTTON3){
			AABB aabb;
			float rand = (float) Math.random ();
			float height = rand * 80f + 20f;
			rand = (float) Math.random ();
			float width = rand * 80f + 20f;
			aabb = new AABB (mouseX - width / 2f, mouseY - height / 2, width, height, 0.4f, 0.5f);
			World.getInstance ().addBody (aabb);
		}
	}
	
	@Override
	public void mouseReleased (MouseEvent me){ }
}
