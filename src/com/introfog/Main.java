package com.introfog;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("JustGame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize (800, 600);
		frame.add(new Display ());
		frame.setVisible(true);
	}
}