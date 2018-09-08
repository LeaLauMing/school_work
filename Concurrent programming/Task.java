
import java.util.ArrayList;
import java.util.Random;

public class Task implements Runnable {

	private ArrayList<Character> characters;
	private long timeStart = System.currentTimeMillis();
	
	Task(ArrayList<Character> ch){
		characters = ch;
	}
	
	public void run(){
		try{
			//120000
			while(System.currentTimeMillis()-timeStart <= 120000){
				Random rand = new Random();
				Character cur = characters.get(rand.nextInt(characters.size()));
				cur.move();
			}
		}
		catch(Exception e){
			System.out.println("ERROR in run " +e);
            e.printStackTrace();
		}
	}
	
}
