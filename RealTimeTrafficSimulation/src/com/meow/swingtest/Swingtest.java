package com.meow.swingtest;
import javax.swing.*;


public class Swingtest {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Swing Demo");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello Swing!", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
	}

}
