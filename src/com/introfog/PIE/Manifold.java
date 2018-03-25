package com.introfog.PIE;

public class Manifold{
	public float penetration;
	public int contactCount;
	public Vector2f normal;
	public final Vector2f[] contacts = {new Vector2f (), new Vector2f ()};
	public Body a;
	public Body b;
	
	
	public Manifold (Body a, Body b){
		this.a = a;
		this.b = b;
	}
	
	public void solve (){
		if (a.shape == Body.Shape.circle && b.shape == Body.Shape.circle){
			Circle A = (Circle) a;
			Circle B = (Circle) b;
			
			//вектор нормали коллизии
			Vector2f normal = B.centre.sub (A.centre);
			
			float distSqrt = normal.lengthSqrt ();
			float radius = A.radius + B.radius;
			
			//нет контакта
			if (distSqrt >= radius * radius){
				contactCount = 0;
				return;
			}
			
			float distance = (float) Math.sqrt (distSqrt);
			contactCount = 1;
			
			if (distance == 0.0f){
				penetration = A.radius;
				this.normal.set (1.0f, 0.0f);
				contacts[0].set (A.centre);
			}
			else{
				penetration = radius - distance;
				this.normal.set (normal);
				this.normal.divi (distance);
				contacts[0].set (this.normal);
				contacts[0].muli (A.radius);
				contacts[0].addi (A.centre);
			}
		}
	}
}
