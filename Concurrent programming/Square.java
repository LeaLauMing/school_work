
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Square {

	private ArrayList<Square> adjacentSquares = new ArrayList<Square>();
	private volatile AtomicBoolean occupied = new AtomicBoolean(false);
	private Boolean hasObstacle = false;
	private int index;
	private int row;
	private int column; 
	
	Square(int index, int row, int col){
		this.index = index;
		this.row = row;
		this.column = col;
	}
	
	public boolean moveTo(){
		return occupied.compareAndSet(false, true);
	}
	
	public boolean leave(){
		return occupied.compareAndSet(true, false);
	}
	
	public boolean isOccupied(){
		return occupied.get();
	}
	
	public ArrayList<Square> getAdjacentSquares(){
		return this.adjacentSquares;
	}
	
	public boolean hasObstacle(){
		return this.hasObstacle;
	}
	
	public void setObstacle(){
		this.hasObstacle = true;
	}
	
	public void removeObstacle(){
		this.hasObstacle = false;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public int getCol(){
		return this.column;
	}
	
}
