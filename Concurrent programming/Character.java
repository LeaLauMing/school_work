
import java.util.ArrayList;
import java.util.Random;

public class Character {

	private int rate;
	private volatile int nbMove = 0;
	private int id;
	private volatile Square location = null;
	private volatile Square goal = null;
	private Square[][] map;
	
	Character(int k, int id, Square start, Square[][] map){
		this.id = id;
		Random r = new Random();
		this.rate = k*(r.nextInt(3)+1);
		this.location = start;
		this.map = map;
	}
	
	public synchronized void move(){
		try{
			ArrayList<Square> adj = location.getAdjacentSquares();
			
			// find new goal
			if(goal.getCol() == location.getCol() && goal.getRow() == location.getRow()){
				Random rand = new Random();
				while(true){
					int maxX = location.getRow()+8;
					int minX = location.getRow()-8;
					int x = rand.nextInt(maxX-minX)-minX;
					int maxY = location.getCol()+8;
					int minY = location.getCol()-8;
					int y = rand.nextInt(maxY-minY)-minY;
					
					// verify x
					if(x > 29){
						x = 29;
					}
					else if(x < 0){
						x = 0;
					}
					// verify y
					if(y < 0){
						y = 0;
					}
					else if(y>29){
						y = 29;
					}
					
					if(!map[y][x].hasObstacle()){
						setGoal(map[y][x]);
						break;
					}
				}
			}
			
			// take best move to reach goal
			boolean takeRandomMove = true;
			int row = this.goal.getRow();
			int col = this.goal.getCol();
			Square bestMove = null;
			int minDistance = 100;
			
			for(Square cur: adj){
				int distance = (int)Math.sqrt((cur.getCol()-col)*(cur.getCol()-col) + (cur.getRow()-row)*(cur.getRow()-row));
				if(distance < minDistance){
					minDistance = distance;
					bestMove = cur;
				}
			}
			
			// try to move to best square
			if(!bestMove.isOccupied() && !bestMove.hasObstacle()){
				if(bestMove.moveTo()){
					this.location.leave();
					this.location = bestMove;
					this.nbMove++;
					takeRandomMove = false;
				}
			}
			
			// take another move because square is not empty
			Random r = new Random();
			while(takeRandomMove) {
				int index = r.nextInt(adj.size());
				Square newLocation = adj.get(index);
				if(!newLocation.isOccupied() && !newLocation.hasObstacle()){
					if(newLocation.moveTo()){
						this.location.leave();
						this.location = newLocation;
						this.nbMove++;
						break;
					}
				}
				wait(rate);
			}
			wait(rate);
		}
		catch(Exception e){
			System.out.println("ERROR in move " +e);
            e.printStackTrace();
		}
	}
	
	public int getNbMoves(){
		return this.nbMove;
	}
	
	public int getID(){
		return this.id;
	}
	
	public Square getLocation(){
		return this.location;
	}
	
	public synchronized void setGoal(Square g){
		this.goal = g;
	}
	
}
