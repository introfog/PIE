package com.introfog.PIE;

public class MathPIE{
	public static final Vector2f GRAVITY = new Vector2f (0f, 50f); //9.807f
	public static final float FIXED_DELTA_TIME = 1f / 60f;
	public static final float DEAD_LOOP_BORDER = FIXED_DELTA_TIME * 20f;
	
	public static final float CORRECT_POSITION_PERCENT = 0.5f;
	public static final float MIN_BORDER_SLOP = 1f;
	
	public static final float INFINITY_MASS = 0f;
	
	
	public static float signum (float value){
		if (value == 0f){
			return 1;
		}
		return Math.signum (value);
	}
}
