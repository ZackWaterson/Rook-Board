// Rook.java
// by Zack Waterson

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class Rook extends JPanel
{
	//these are the rows of the game board, they are a special class Square that keeps track of
	//if there are walls, rooks, or if it is illegal to place a rook on that spot
	private Square[][] squares; //a double row for easy
	private Square[] allSquares; //a single list of all the arrays for easier iteration
	private ImageIcon wallIcon, rookIcon;
	private boolean boardActive; //this is used to disable the board and calculate button until reset
	private JButton calculate, reset;
	private JTextArea textArea;
	private ArrayList<Square> tempList, longestList; //used to keep track of the most legal placements
	
	public Rook() 
	{
		setPreferredSize(new Dimension(300, 435));
		setLayout(null);
		
		boardActive = true;
		
		//keeps track of longest list
		tempList = new ArrayList<Square>();
		longestList = new ArrayList<Square>();
		
		calculate = new JButton("Calculate");
		calculate.setBounds(30, 350, 240, 25);
		calculate.addActionListener(new CalculateAction());
		add(calculate);
		
		reset = new JButton("Reset");
		reset.setBounds(30, 390, 240, 25);
		reset.addActionListener(new ResetButton());
		add(reset);
		
		textArea = new JTextArea();
		textArea.setText("Place walls then calculate how many legal rook placements are possible.");
		textArea.setBounds(30, 300, 240, 40);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		add(textArea);
		
		//gets the two icons
		wallIcon = new ImageIcon(getClass().getResource("/resource/WallIcon.png"));
		rookIcon = new ImageIcon(getClass().getResource("/resource/RookIcon.png"));
		
		addMouseListener(new ClickChecker());
				
		//sets up the squares and adds them to squares[]
		squares = new Square[4][4];
		for (int i = 0; i < 4; i++)
		{
			for (int x = 0; x < 4; x++)
			{
				squares[i][x] = new Square(20 + (65 * x), 20 + (65 * i));
			}
		}
		
		//add the squares to another one dimensional square array for easier access to all of them
		allSquares = new Square[16];
		int counter = 0;
		for (int s = 0; s < 4; s++){
			for (Square square : squares[s]){
				allSquares[counter] = square;
				counter++;
			}
		}
	}
	
	public void paintComponent (Graphics page)
	{
		super.paintComponent(page);
		
		//first it paints each square, then it checks if a wall
		//or rook should be painted, and does so
		for (Square square : allSquares){
			page.setColor(Color.WHITE);
			page.fillRect(square.getX(), square.getY(), 65, 65);
		
			if (square.wallCheck())
				wallIcon.paintIcon(this, page, square.getX() + 1, square.getY() + 1);
			else if (square.rookCheck()){
				rookIcon.paintIcon(this, page, square.getX() + 1, square.getY() + 1);
			}
			
			//paints borders last so they are on top of the edge of the icons
			page.setColor(Color.BLACK);
			page.drawRect(square.getX(), square.getY(), 65, 65);
		}
	}
	
	/* This part happens when the user clicks the calculate button.
	 * It first starts up all the counters. x and y are used in counting
	 * the index of the array, and z and w are used to count x and y up or down.
	 * u is set to whatever y's starting position is, so that y can be set to that each time x changes.
	 * v is the same for x's starting position.
	 * When starting from the top left, both x and y start at 0 and count up by one.
	 * When starting from the top right, x starts at 0 and y starts at 3. x counts up, y down.
	 * When starting from the bottom left, x starts at 3 and y at 0. x counts down, and y up.
	 * When starting from the bottom right, both x and y start at 3, and both count down.
	 * When starting from the middle left row y = 1 and will count the 0 row at the end, x is 0 or 3
	 * depending on if we are starting from the top or bottom
	 * When starting from the middle right row y = 2 and will count the 3 row at the end, x is the same
	 * as the middle left counting.
	 * z and w are switched between 1 and -1 depending on if x and y need to count up or down.
	 * x, w and y, z are paired. The outer for loop is to complete the whole thing 8 times,
	 * once for each corner. The middle for loop count through the x part of the 2d array.
	 * And the inner for loop counts through the y part of the 2d array.
	 * Keeps track of the max placements using two ArrayLists. Originally sets longestList
	 * to the top left placements, but will replace that if one of the other corners has more.
	 * Resets rooks and illegal placements each time. At the end, puts the rooks back on the board.
	 * Disables the board and calculate button at the end and changes the text until the 
	 * reset button is pressed again. Then the board will be back to its original state. */
	private class CalculateAction implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			//the array counters
			int x = 0, y = 0;
			//used for reversing x and y to count down rather than up
			int z = -1, w = 1;
			//u and v are used for setting x and y back to starting position respectively
			int u = 0, v = 0;
			
			//this loop makes it count rooks 8 times, each from a different corner of the board, and the middle top and bottom squares
			for (int i = 0; i < 8; i++){
				//when we need to start counting from the bottom row this flips some numbers
				if (i == 4)
				{
					u = 3;	//sets x's starting position to 3 so we start from the bottom row
					v = 0;	//sets y's starting position back to 0 so we start from the left again
					w = -1;	//sets w to -1 so x starts counting down
				}
				x = u;		//sets x back to its starting position
				z *= -1;	//z alternates between 1 and -1 to count y right or left
				
				//counts through the x portion of the 2d array
				for(int j = 0; j < 4; j++){
					//sets y back to what it needs for each run through
					y = v;
					
					//counts through the y portion of the 2d array
					for(int k = 0; k < 4; k++){
						//if the square is blank (not illegal and no walls)
						if(!squares[x][y].wallCheck() && !squares[x][y].illegalCheck()){
							squares[x][y].addRook(); 		//add a rook to the square
							tempList.add(squares[x][y]); 	//add the square to tempList
							markIllegalSquares(x, y); 		//mark squares that can be hit by the rook as illegal
						}
						//z is either 1 or -1 depending on whether y is counting up or down
						y += z;
						//this is to check the squares starting from the middle rows
						//it gets the first or last square after y has checked the others
						if (y == -1)
							y = 3;
						else if (y == 4)
							y = 0;
					}
					//w is also either 1 or -1 depending on whether x is counting up or down
					x += w;
				}
				v++;
				//sets longestList to the count from the top left corner first,
				//then will change it if one of the other corners has a higher count of rooks
				if (tempList.size() > longestList.size() || longestList.isEmpty()){
					longestList.clear();
					for (Square square : tempList)
						longestList.add(square);
				}
				//clears tempList so it can be added to again
				//and resets all the rooks on the field
				//as well as all the illegal placement booleans in the squares
				tempList.clear(); 
				resetRooks();
			}
			
			//places the rooks back on the board by re-enabling the rook boolean
			//in the squares that were part of the highest count
			for (Square square : longestList)
				square.addRook();
			
			//changes the text to say how many rooks can be placed legally
			//disables the board so no walls can be placed and disables the calculate button
			//it will remain this way until reset is pressed
			textArea.setText("There is a max of " + longestList.size() + " legal rook placements.");
			boardActive = false;
			calculate.setEnabled(false);
			repaint();
		}
		
	}
	
	//checks rows to mark squares as illegal if a rook can hit them
	public void markIllegalSquares(int x, int y)
	{
		// we start i at -1 so that we can do x (and y) + -1 and then we add 2 to i
		// so we can do x (y) + 1 on the second loop with the same statement
		for (int i = -1; i < 2; i += 2)
		{
			//checks the vertical rows for illegal squares
			int xCheck = x + i;
			while(0 <= xCheck && xCheck < 4){
				if (squares[xCheck][y].wallCheck())
					break;
				else
					squares[xCheck][y].markIllegal();
				xCheck += i;
			}
			
			//checks the horizontal rows for illegal squares
			int yCheck = y + i;
			while(0 <= yCheck && yCheck < 4)
			{
				if (squares[x][yCheck].wallCheck())
					break;
				else
					squares[x][yCheck].markIllegal();
				yCheck += i;
			}
		}
	}
	
	//resets the rooks and illegal marks
	public void resetRooks()
	{
		for (Square square : allSquares)
		{
			square.resetRook();
			square.resetIllegal();
		}
	}
	
	//resets the whole board
	private class ResetButton implements ActionListener
	{
		//clears all the squares to their default booleans
		public void actionPerformed(ActionEvent event) {
			for (Square square : allSquares){
				square.resetWall();
				square.resetRook();
				square.resetIllegal();
			}
			
			//resets the text, clears longestList for future use, enables the calculate button
			//and sets the board to active again so that wall can be placed
			textArea.setText("Place walls then calculate how many legal rook placements are possible.");
			longestList.clear();
			calculate.setEnabled(true);
			boardActive = true;
			
			repaint();
		}
	}
	
	/* Checks where to place walls when the user clicks.
	 * First checks if the board is active. The board will be active
	 * until the user calculates, and will then remain inactive until
	 * the reset button is pushed. Then it goes through each square and
	 * checks if the click was inside its area. If it is, it will toggle
	 * the wall, repaint, and break. */
	private class ClickChecker extends MouseAdapter
	{
		public void mouseClicked(MouseEvent event) {
			if (boardActive){
				for (Square square : allSquares){
					if (square.getX() <= event.getPoint().x && event.getPoint().x <= square.getX() + 65){
						if (square.getY() <= event.getPoint().y && event.getPoint().y <= square.getY() + 65){
							square.toggleWall();
							repaint();
							break;
						}
					}
				}
			}
		}
	}
}