package student_player;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
    
	private static Node root;
	private static ArrayList<Node> queue;
	
	// Make the graph
	public static void makeGraph(TablutBoardState boardState, boolean first){	
		root = new Node(null, boardState, null, first);
		queue = new ArrayList<Node>();
		queue.add(root);
		while(!queue.isEmpty()){
			//get a new node in queue, init their children and store them in queue
			Node cur = queue.remove(0);
			if(cur.getDepth() == 3){
				break;
			}
			ArrayList<TablutMove> options = cur.getBoardState().getAllLegalMoves();
			int i = 0;
			// find its children
			for(TablutMove move: options){
				TablutBoardState cloneBS = (TablutBoardState)cur.getBoardState().clone();
	            cloneBS.processMove(move);
	            if(cloneBS.getWinner() != Board.NOBODY || i%2 == 0){
		            Node child = new Node(cur, cloneBS, move, first);
		            queue.add(child);
		            cur.getNext().add(child);
		            if(!first && cloneBS.getWinner() == TablutBoardState.SWEDE){
		            	break;
		            }
		            else if(first && cloneBS.getWinner() == TablutBoardState.MUSCOVITE){
		            	break;
		            }
	            }
	            i++;
			}
		}
	}
	
	// minmax
	public static Node minimax(Node node, int depth, boolean maxPlayer){
		if(depth == 0){
			return node;
		}
		if(maxPlayer){
			Node bestValue = root;
			if(node.getNext().size() != 0){
				for(int i=0; i<node.getNext().size(); i++){
					Node v = minimax(node.getNext().get(i), depth-1, false);
					if(v.getHeuristic() < bestValue.getHeuristic()){
						bestValue = v;
					}
				}	
			}
			return bestValue;
		}
		else{
			Node bestValue = root;
			if(node.getNext().size() != 0){
				for(int i=0; i<node.getNext().size(); i++){
					Node v = minimax(node.getNext().get(i), depth-1, true);
					if(v.getHeuristic() > bestValue.getHeuristic() || bestValue.getHeuristic() == 10000){
						bestValue = v;
					}
				}
			}
			return bestValue;
		}
	}
	
	public static Node getRoot(){
		return root;
	}
	
	
	
	
}
