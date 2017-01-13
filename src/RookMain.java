// RookMain.java
// by Zack Waterson

import javax.swing.*;

public class RookMain 
{
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame ("Rook Board");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		Rook rook = new Rook ();
		frame.getContentPane().add(rook);
		frame.pack();
		frame.setVisible(true);
	}
}