package com.introfog.PIE;

import com.introfog.PIE.collisions.Collisions;

public class Manifold{
	public boolean areBodiesCollision = true;
	public float penetration;
	public Vector2f normal;
	public Polygon polygonA;
	public Polygon polygonB;
	public Circle circleA;
	public Circle circleB;
	public Body a;
	public Body b;
	
	
	public Manifold (Body a, Body b){
		this.a = a;
		this.b = b;
	}
	
	public void initializeCollision (){
		if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.circle){
			circleA = (Circle) a.shape;
			circleB = (Circle) b.shape;
		}
		else if (a.shape.type == Shape.Type.polygon && b.shape.type == Shape.Type.polygon){
			polygonA = (Polygon) a.shape;
			polygonB = (Polygon) b.shape;
		}
		else if (a.shape.type == Shape.Type.polygon && b.shape.type == Shape.Type.circle){
			polygonA = (Polygon) a.shape;
			circleB = (Circle) b.shape;
		}
		else if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.polygon){
			circleA = (Circle) a.shape;
			polygonB = (Polygon) b.shape;
		}
		
		Collisions.table[a.shape.type.ordinal ()][b.shape.type.ordinal ()].handleCollision (this);
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
		
		Vector2f correction = Vector2f.mul (normal,penetration * MathPIE.CORRECT_POSITION_PERCENT / (a.invertMass + b.invertMass));
		a.position.sub (Vector2f.mul (correction, a.invertMass));
		b.position.add (Vector2f.mul (correction, b.invertMass));
	}
}