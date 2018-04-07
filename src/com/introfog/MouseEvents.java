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
		
		/*AABB aabb = new AABB (330f, 350f, 80f, 100f, 1f); //1 круг касается прямоугольника, центр круга за прямоугольником
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (20f, 320f, 370f, 1f);
		World.getInstance ().addBody (circle);*/
		
		/*AABB aabb = new AABB (330f, 350f, 80f, 100f, 1f); //2 центр круга внутри AABB, но он торчит из него
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (20f, 350f, 360f, 1f);
		World.getInstance ().addBody (circle);*/
		
		/*AABB aabb = new AABB (340f, 200f, 80f, 100f, 1f); //4 AABB заходит в круг
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (10f, 360f, 370f, 1f);
		World.getInstance ().addBody (circle);*/
		
		/*AABB aabb = new AABB (320f, 320f, 80f, 100f, 1f); //5 круг целиком в AABB и центры совпадают
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (20f, 360f, 370f, 1f);
		World.getInstance ().addBody (circle);*/
		
		/*AABB aabb = new AABB (340f, 350f, 40f, 20f, 1f); //6 AABB заходит в круг
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (40f, 360f, 370f, 1f);
		World.getInstance ().addBody (circle);*/
		
		/*AABB aabb = new AABB (340f, 350f, 40f, 40f, 1f); //8 AABB в круге, цетнры совпадают
		World.getInstance ().addBody (aabb);
		Circle circle = new Circle (40f, 360f, 370f, 1f);
		World.getInstance ().addBody (circle);*/
		
		
		if (me.getButton () == MouseEvent.BUTTON1){
			Circle circle;
			float rand = (float) Math.random ();
			circle = new Circle (rand * 20f + 5f, mouseX, mouseY,  1f);
			World.getInstance ().addBody (circle);
		}
		else if (me.getButton () == MouseEvent.BUTTON3){
			AABB aabb;
			float rand = (float) Math.random ();
			float height = rand * 80f + 20f;
			rand = (float) Math.random ();
			float width = rand * 80f + 20f;
			aabb = new AABB (mouseX - width / 2f, mouseY - height / 2, width, height, 1f);
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
