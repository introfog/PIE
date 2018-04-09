package com.introfog.PIE;

public class MathPIE{
	public static float signum (float value){
		if (value == 0f){
			return 1;
		}
		return Math.signum (value);
	}
}
