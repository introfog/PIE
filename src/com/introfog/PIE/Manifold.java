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
		
		normal = new Vector2f ();
	}
	
	public boolean isCollision (){
		if (a.shape == Body.Shape.circle && b.shape == Body.Shape.circle){
			return Circle.isIntersected ((Circle) a, (Circle) b);
		}
		return false;
	}
	
	public void solve (){
		if (a.shape == Body.Shape.circle && b.shape == Body.Shape.circle){
			Circle A = (Circle) a;
			Circle B = (Circle) b;
			
			normal.x = B.position.x - A.position.x;
			normal.y = B.position.y - A.position.y;
			normal.normalize ();
			
			// Вычисляем относительную скорость
			Vector2f rv = B.velocity.sub (A.velocity);
			
			// Вычисляем относительную скорость относительно направления нормали
			float velAlongNormal = Vector2f.dotProduct (rv, normal); //TODO calculate normal
			
			// Не выполняем вычислений, если скорости разделены
			if(velAlongNormal > 0){
				return;
			}
			
			//TODO restitution = 0
			
			// Вычисляем скаляр импульса силы
			float j = -velAlongNormal;
			j /= A.invertMass + B.invertMass;
			
			// Прикладываем импульс силы
			normal.muli (j);
			Vector2f impulse = new Vector2f (normal);
			normal.muli (1/ j); //TODO add mullsi
			A.velocity.subi (impulse.returnMuli (A.invertMass));
			B.velocity.addi (impulse.returnMuli (B.invertMass));
		}
	}
}
