
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) {
		
		try{
			if (args.length<4){
				throw new Exception("Missing arguments, only "+args.length+" were specified!");
			}
			// parameter n, number of characters
			int n = Integer.parseInt(args[0]);
			// parameter p, max number of thread
			int p = Integer.parseInt(args[1]);
			// parameter r, number of obstacles
			int r = Integer.parseInt(args[2]);
			// parameter k, ms rate
			int k = Integer.parseInt(args[3]);
			
			Random rand = new Random();
			
			// create the map
			Square[][] map = new Square[30][30];
			int counter = 0;
			for(int i=0; i<30; i++){
				for(int j=0; j<30; j++){
					map[i][j] = new Square(counter, i, j);
					counter ++;
				}
			}
			for(int i=0; i<30; i++){
				for(int j=0; j<30; j++){
					Square cur = map[i][j];
					if(i == 0){
						if(j == 0){
							cur.getAdjacentSquares().add(map[i][1]);
							cur.getAdjacentSquares().add(map[i+1][0]);
							cur.getAdjacentSquares().add(map[i+1][1]);
						}
						else if(j == 29){
							cur.getAdjacentSquares().add(map[i][28]);
							cur.getAdjacentSquares().add(map[i+1][29]);
							cur.getAdjacentSquares().add(map[i+1][28]);
						}
						else{
							cur.getAdjacentSquares().add(map[0][j+1]);
							cur.getAdjacentSquares().add(map[0][j-1]);
							cur.getAdjacentSquares().add(map[1][j]);
							cur.getAdjacentSquares().add(map[1][j-1]);
							cur.getAdjacentSquares().add(map[1][j+1]);
						}
					}
					else if(i == 29){
						if(j == 0){
							cur.getAdjacentSquares().add(map[29][1]);
							cur.getAdjacentSquares().add(map[28][0]);
							cur.getAdjacentSquares().add(map[28][1]);
						}
						else if(j == 29){
							cur.getAdjacentSquares().add(map[29][28]);
							cur.getAdjacentSquares().add(map[28][29]);
							cur.getAdjacentSquares().add(map[28][28]);
						}
						else{
							cur.getAdjacentSquares().add(map[29][j+1]);
							cur.getAdjacentSquares().add(map[29][j-1]);
							cur.getAdjacentSquares().add(map[28][j]);
							cur.getAdjacentSquares().add(map[28][j+1]);
							cur.getAdjacentSquares().add(map[28][j-1]);
						}
					}
					else if(j == 0){
						cur.getAdjacentSquares().add(map[i][1]);
						cur.getAdjacentSquares().add(map[i-1][0]);
						cur.getAdjacentSquares().add(map[i-1][1]);
						cur.getAdjacentSquares().add(map[i+1][0]);
						cur.getAdjacentSquares().add(map[i+1][1]);
					}
					else if(j == 29){
						cur.getAdjacentSquares().add(map[i][28]);
						cur.getAdjacentSquares().add(map[i-1][29]);
						cur.getAdjacentSquares().add(map[i-1][28]);
						cur.getAdjacentSquares().add(map[i+1][28]);
						cur.getAdjacentSquares().add(map[i+1][29]);
					}
					else{
						cur.getAdjacentSquares().add(map[i][j+1]);
						cur.getAdjacentSquares().add(map[i][j-1]);
						cur.getAdjacentSquares().add(map[i+1][j]);
						cur.getAdjacentSquares().add(map[i+1][j+1]);
						cur.getAdjacentSquares().add(map[i+1][j-1]);
						cur.getAdjacentSquares().add(map[i-1][j]);
						cur.getAdjacentSquares().add(map[i-1][j+1]);
						cur.getAdjacentSquares().add(map[i-1][j-1]);
					}
				}
			}
			
			// add obstacles
			for(int i=0 ; i<r; i++){
				int x;
				int y;
				while(true){
					x = rand.nextInt(30);
					y = rand.nextInt(30);
					ArrayList<Square> neighbours = map[y][x].getAdjacentSquares();
					boolean obstacleInN = false;
					// make sure neighbours don't have a obstacle already
					for(Square s: neighbours){
						if(s.hasObstacle()){
							obstacleInN = true;
							break;
						}
					}
					if(!map[y][x].hasObstacle() && !obstacleInN){
						map[y][x].setObstacle();
						break;
					}
				}
			}
			
			// set characters
			ArrayList<Character> characters = new ArrayList<Character>();
			for(int i=0 ; i<n; i++){
				int x = rand.nextInt(30);
				int y = rand.nextInt(30);
				while(map[y][x].isOccupied() || map[y][x].hasObstacle()){
					x = rand.nextInt(30);
					y = rand.nextInt(30);
				}
				Character c = new Character(k, i, map[y][x], map);
				characters.add((Character)c);
			}
			
			//set goals for characters
			for(int i=0 ; i<n; i++){
				while(true){
					Character c = characters.get(i);
					int maxX = c.getLocation().getRow()+8;
					int minX = c.getLocation().getRow()-8;
					int x = rand.nextInt(maxX-minX)-minX;
					int maxY = c.getLocation().getCol()+8;
					int minY = c.getLocation().getCol()-8;
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
						c.setGoal(map[y][x]);
						break;
					}
					
				}
			}
			
			// thread pool for 120 000 ms (2 min)
			ExecutorService executor = Executors.newScheduledThreadPool(p);
			Runnable controller = new Task(characters);
			for(int i=0 ; i<p; i++){
				executor.submit(controller);
			}

			executor.shutdown();
			
			
			while (!executor.isTerminated()) { 	  }
			
			//report total number of moves
			for(Character character: characters){
				System.out.println("Character "+ character.getID() + " made " + character.getNbMoves() + " moves.");
			}
		}
		catch(Exception e){
			System.out.println("ERROR in main " +e);
            e.printStackTrace();
		}

	}

}
