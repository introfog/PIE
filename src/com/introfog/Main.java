package com.introfog;

import javax.swing.*;

public class Main {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Demo PIE");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize (800, 600);
		frame.add(new Display ());
		frame.setVisible(true);
	}
}