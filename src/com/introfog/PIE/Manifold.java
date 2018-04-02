package com.introfog.PIE;

public class Manifold{
	public final float CORRECT_POSITION_PERCENT = 0.8f;
	
	public float penetration;
	public Vector2f normal;
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
			
			normal = Vector2f.sub (B.position, A.position);
			normal.normalize ();
			penetration = A.radius + B.radius - (float) Math.sqrt (
					Vector2f.distanceWithoutSqrt (B.position, A.position));
			
			// Вычисляем относительную скорость
			Vector2f rv = Vector2f.sub (B.velocity, A.velocity);
			
			// Вычисляем относительную скорость относительно направления нормали
			float velAlongNormal = Vector2f.dotProduct (rv, normal);
			
			// Не выполняем вычислений, если скорости разделены
			if (velAlongNormal > 0){
				return;
			}
			
			// Вычисляем упругость
			float e = Math.min (A.restitution, B.restitution);
			
			// Вычисляем скаляр импульса силы
			float j = -(1 + e) * velAlongNormal;
			j /= A.invertMass + B.invertMass;
			
			// Прикладываем импульс силы
			
			Vector2f impulse = Vector2f.mul (normal, j);
			A.velocity.sub (Vector2f.mul (impulse, A.invertMass));
			B.velocity.add (Vector2f.mul (impulse, B.invertMass));
		}
	}
	
	public void correctPosition (){
		Vector2f correction = Vector2f.mul (normal, penetration * CORRECT_POSITION_PERCENT);
		a.position.sub (Vector2f.mul (correction, a.invertMass));
		b.position.add (Vector2f.mul (correction, b.invertMass));
	}
}
