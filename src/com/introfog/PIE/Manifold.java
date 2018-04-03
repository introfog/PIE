package com.introfog.PIE;

public class Manifold{
	public final float CORRECT_POSITION_PERCENT = 0.8f;
	public final float BORDER_SLOP = 0.1f;
	
	public float penetration;
	public Vector2f normal;
	public Body a;
	public Body b;
	
	
	private void initializeCollision (){
		if (a.shape == Body.Shape.circle && b.shape == Body.Shape.circle){
			Circle A = (Circle) a;
			Circle B = (Circle) b;
			
			normal = Vector2f.sub (B.position, A.position);
			normal.normalize ();
			penetration = A.radius + B.radius - (float) Math.sqrt (Vector2f.distanceWithoutSqrt (B.position, A.position));
		}
		else if (a.shape == Body.Shape.AABB && b.shape == Body.Shape.AABB){
			AABB A = (AABB) a;
			AABB B = (AABB) b;
			
			A.updateCentre ();
			B.updateCentre ();
			normal = Vector2f.sub (B.centre, A.centre);
			//position == min
			//max == max
			
			// Вычисление половины ширины вдоль оси x для каждого объекта
			float aExtentX = A.width / 2f;
			float bExtentX = B.width / 2f;
			
			// Вычисление наложения по оси x
			float xOverlap = aExtentX + bExtentX - Math.abs (normal.x);
			
			// Проверка SAT по оси x
			if (xOverlap > 0f){
				// Вычисление половины ширины вдоль оси y для каждого объекта
				float aExtentY = A.height / 2f;
				float bExtentY = B.height / 2f;
				
				// Вычисление наложения по оси y
				float yOverlap = aExtentY + bExtentY - Math.abs (normal.y);
				
				// Проверка SAT по оси y
				if (yOverlap > 0f){
					// Определяем, по какой из осей проникновение наименьшее
					if (xOverlap < yOverlap){
						// Указываем в направлении B, зная, что n указывает в направлении от A к B
						normal.set (Math.signum (normal.x), 0f);
						penetration = xOverlap;
						return;
					}
					else{
						// Указываем в направлении B, зная, что n указывает в направлении от A к B
						normal.set (0f, Math.signum (normal.y));
						penetration = yOverlap;
						return;
					}
				}
			}
		}
	}
	
	
	public Manifold (Body a, Body b){
		this.a = a;
		this.b = b;
		
		normal = new Vector2f ();
	}
	
	public boolean isCollision (){
		if (a.shape == Body.Shape.circle && b.shape == Body.Shape.circle){
			return Circle.isIntersected ((Circle) a, (Circle) b);
		}
		else if (a.shape == Body.Shape.AABB && b.shape == Body.Shape.AABB){
			return AABB.isIntersected ((AABB) a, (AABB) b);
		}
		return false;
	}
	
	public void solve (){
		initializeCollision ();
		
		// Вычисляем относительную скорость
		Vector2f rv = Vector2f.sub (b.velocity, a.velocity);
		
		// Вычисляем относительную скорость относительно направления нормали
		float velAlongNormal = Vector2f.dotProduct (rv, normal);
		
		// Не выполняем вычислений, если скорости разделены
		if (velAlongNormal > 0){
			return;
		}
		
		// Вычисляем упругость
		float e = Math.min (a.restitution, b.restitution);
		
		// Вычисляем скаляр импульса силы
		float j = -(1 + e) * velAlongNormal;
		j /= a.invertMass + b.invertMass;
		
		// Прикладываем импульс силы
		Vector2f impulse = Vector2f.mul (normal, j);
		a.velocity.sub (Vector2f.mul (impulse, a.invertMass));
		b.velocity.add (Vector2f.mul (impulse, b.invertMass));
	}
	
	public void correctPosition (){
		if (penetration < BORDER_SLOP){
			return;
		}
		
		Vector2f correction = Vector2f.mul (normal, penetration * CORRECT_POSITION_PERCENT / (a.invertMass + b.invertMass));
		a.position.sub (Vector2f.mul (correction, a.invertMass));
		b.position.add (Vector2f.mul (correction, b.invertMass));
	}
}
