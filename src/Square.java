// Square.java
// by Zack Waterson

public class Square 
{
	private int x, y;
	private boolean wall, rook, illegal;
	
	public Square (int xCoord, int yCoord)
	{
		x = xCoord;
		y = yCoord;
		wall = false;
		rook = false;
		illegal = false;
	}
	
	//toggles the wall when the user clicks on it
	public void toggleWall()
	{
		wall = !wall;
	}
	
	//resets the wall
	public void resetWall()
	{
		wall = false;
	}
	
	//add a rook to the square
	public void addRook()
	{
		rook = true;
	}
	
	//reset rook
	public void resetRook()
	{
		rook = false;
	}
	
	//marks the square as illegal
	public void markIllegal()
	{
		illegal = true;
	}
	
	//reset illegal marks
	public void resetIllegal()
	{
		illegal = false;
	}
	
	//returns true if there is a wall
	public boolean wallCheck()
	{
		return wall;
	}
	
	//returns true if there is a rook
	public boolean rookCheck()
	{
		return rook;
	}
	
	//returns true if a rook can hit this square
	public boolean illegalCheck()
	{
		return illegal;
	}
	
	//returns the x coordinate of the square
	public int getX()
	{
		return x;
	}
	
	//returns the y coordinate of the square
	public int getY()
	{
		return y;
	}
}