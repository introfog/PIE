package com.introfog.PIE;

public class Manifold{ //TODO добавить таблицу переходов
	private float penetration;
	private Vector2f normal;
	private AABB aabbA;
	private AABB aabbB;
	private Circle circleA;
	private Circle circleB;
	private Body a;
	private Body b;
	
	
	private void circleVsCircle (Circle A, Circle B){
		normal = Vector2f.sub (B.body.position, A.body.position);
		penetration = A.radius + B.radius - (float) Math.sqrt (
				Vector2f.distanceWithoutSqrt (B.body.position, A.body.position));
	}
	
	private void AABBvsAABB (AABB A, AABB B){
		A.updateCentre ();
		B.updateCentre ();
		
		normal = Vector2f.sub (B.centre, A.centre);
		
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
					normal.set (MathPIE.signumWithoutZero (normal.x), 0f);
					penetration = xOverlap;
				}
				else{
					// Указываем в направлении B, зная, что n указывает в направлении от A к B
					normal.set (0f, MathPIE.signumWithoutZero (normal.y));
					penetration = yOverlap;
				}
			}
		}
	}
	
	private void AABBvsCircle (AABB A, Circle B, boolean areRevertObjects){
		A.updateCentre ();
		
		Vector2f tmpNormal = Vector2f.sub (B.body.position, A.centre);
		Vector2f closest = new Vector2f ();
		
		float xExtent = A.width / 2;
		float yExtent = A.height / 2;
		
		closest.x = MathPIE.clamp (-xExtent, xExtent, tmpNormal.x);
		closest.y = MathPIE.clamp (-yExtent, yExtent, tmpNormal.y);
		
		boolean inside = false;
		
		if (tmpNormal.equals (closest)){
			inside = true;
			
			if (Math.abs (tmpNormal.x) > Math.abs (tmpNormal.y)){
				closest.x = MathPIE.signumWithoutZero (closest.x) * xExtent;
			}
			else{
				closest.y = MathPIE.signumWithoutZero (closest.y) * yExtent;
			}
		}
		
		normal = Vector2f.sub (tmpNormal, closest);
		float distance = normal.lengthWithoutSqrt ();
		
		if (distance > B.radius * B.radius && !inside){
			penetration = 0f;
			if (B.body.velocity.x > A.body.velocity.x){
				normal.set (1f, 0f);
			}
			else{
				normal.set (-1f, 0f);
			}
			return;
		}
		
		distance = (float) Math.sqrt (distance);
		
		penetration = B.radius - distance;
		if (inside){
			normal.mul (-1f);
			if (penetration <= 0){
				penetration = distance;
			}
		}
		
		if (areRevertObjects){
			normal.mul (-1f);
		}
	}
	
	
	public Manifold (Body a, Body b){
		this.a = a;
		this.b = b;
	}
	
	public void initializeCollision (){
		if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.circle){
			circleVsCircle (circleA, circleB);
		}
		else if (a.shape.type == Shape.Type.AABB && b.shape.type == Shape.Type.AABB){
			AABBvsAABB (aabbA, aabbB);
		}
		else if (a.shape.type == Shape.Type.AABB && b.shape.type == Shape.Type.circle){
			AABBvsCircle (aabbA, circleB, false);
		}
		else{
			AABBvsCircle (aabbB, circleA, true);
		}
		
	}
	
	public boolean isCollision (){
		if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.circle){
			circleA = (Circle) a.shape;
			circleB = (Circle) b.shape;
			return Circle.isIntersected (circleA, circleB);
		}
		else if (a.shape.type == Shape.Type.AABB && b.shape.type == Shape.Type.AABB){
			aabbA = (AABB) a.shape;
			aabbB = (AABB) b.shape;
			return AABB.isIntersected (aabbA, aabbB);
		}
		else if (a.shape.type == Shape.Type.AABB && b.shape.type == Shape.Type.circle){
			aabbA = (AABB) a.shape;
			circleB = (Circle) b.shape;
			return AABB.isIntersected (aabbA, circleB);
		}
		else if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.AABB){
			circleA = (Circle) a.shape;
			aabbB = (AABB) b.shape;
			return AABB.isIntersected (aabbB, circleA);
		}
		return false;
	}
	
	public void solve (){
		normal.normalize ();
		
		// Вычисляем относительную скорость
		Vector2f rv = Vector2f.sub (b.velocity, a.velocity); //relativeVelocity
		
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
		
		
		
		//--------Работа с трением
		
		//Перерасчет относительной скорости, после приложения нормального импульса
		rv = Vector2f.sub (b.velocity, a.velocity);
		
		//Вычисялем касательный вектор: tangent = rb - dotProduct (rv, normal) * normal
		Vector2f t = Vector2f.sub (rv, Vector2f.mul (normal, Vector2f.dotProduct (rv, normal)));
		t.normalize ();
		
		//Вычисляем величину, прилагаемую вдоль вектора трения
		float jt = -Vector2f.dotProduct (rv, t);
		jt /= a.invertMass + b.invertMass;
		
		//Вычисляем Мю,
		float Mu = (float) Math.sqrt (a.staticFriction * a.staticFriction + b.staticFriction * b.staticFriction);
		//Статическое трение - величина, показывающая сколько нужно приложить энергии что бы свдинуть тела, т.е. это
		//порог, если энергия ниже, то тела покоятся, если выше, то они сдвинулись
		//Динамическое трение - трение в обычном понимании, когда тела труться друг об друга, они теряют часть своей
		//энергии друг об друга
		
		Vector2f frictionImpulse;
		if (Math.abs (jt) < j * Mu){ //Закон Амонтона — Кулона (если велечина j слишком маленькая, то тела должны покояться)
			frictionImpulse = Vector2f.mul (t, jt);
		}
		else{
			float dynamicFriction = (float) Math.sqrt (a.dynamicFriction * a.dynamicFriction + b.dynamicFriction * b.dynamicFriction);
			frictionImpulse = Vector2f.mul (t, -j * dynamicFriction);
		}
		
		//Пркладываем
		a.velocity.sub (Vector2f.mul (frictionImpulse, a.invertMass));
		b.velocity.add (Vector2f.mul (frictionImpulse, b.invertMass));
	}
	
	public void correctPosition (){
		if (penetration < MathPIE.MIN_BORDER_SLOP){
			return;
		}
		
		Vector2f correction = Vector2f.mul (normal,
											penetration * MathPIE.CORRECT_POSITION_PERCENT / (a.invertMass + b.invertMass));
		a.position.sub (Vector2f.mul (correction, a.invertMass));
		b.position.add (Vector2f.mul (correction, b.invertMass));
	}
}
