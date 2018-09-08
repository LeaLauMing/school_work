package student_player;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260690496");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	Move myMove = null;
        //if player is white
        if(player_id == TablutBoardState.SWEDE){
        	// make the graph for us being the white pawns with heuristic being min distance of the king to a corner + nb of opponent pawns
        	MyTools.makeGraph(boardState, false);
        	Node best = MyTools.minimax(MyTools.getRoot(), 2, true);
        	System.out.println(best.getHeuristic());
        	while(!(best.getParent().equals(MyTools.getRoot()))){
        		best = best.getParent();
        	}
        	myMove = best.getMove();
        	return myMove;
        	
        }
    	// using minmax algorithm 
        else{
        	// make the graph for us being the white pawns with heuristic being min distance of the king to a corner + nb of opponent pawns
        	MyTools.makeGraph(boardState, true);
        	Node best = MyTools.minimax(MyTools.getRoot(), 2, true);
        	while(!(best.getParent().equals(MyTools.getRoot()))){
        		best = best.getParent();
        	}
        	myMove = best.getMove();
        	return myMove;
        }
    }
}