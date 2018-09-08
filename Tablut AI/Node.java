package student_player;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutBoardState.Piece;
import tablut.TablutMove;
import tablut.TablutPlayer;

public class Node {
    
	private TablutBoardState boardState;
	private Node parent = null;
	private ArrayList<Node> next = new ArrayList<Node>();
	private TablutMove move;
	private int heuristic = 10000;
	private int depth = 0;
	
	Node(Node parent, TablutBoardState cur, TablutMove move, boolean first){
		this.parent = parent;
		this.move = move;
		this.boardState = cur;
		//heuristic if black pawns
		if(first){
			if(parent != null){
				this.heuristic = 100;
				this.depth = parent.getDepth()+1;
				Coord kcoord = cur.getKingPosition();
				//go close to the king to capture it
				if(kcoord != null){
					for(Coord kingNeighbour : Coordinates.getNeighbors(kcoord)){
						if(kcoord.equals(Coordinates.get(4, 4)) || kcoord.equals(Coordinates.get(3, 4)) || kcoord.equals(Coordinates.get(4, 3)) || kcoord.equals(Coordinates.get(4, 5)) || kcoord.equals(Coordinates.get(5, 4))){
							this.heuristic -= 8;
						}
						else if(cur.getPieceAt(kingNeighbour).equals(Piece.BLACK)){
							this.heuristic -= 15;
						}
					}
				}
				// kill
				if(parent.getBoardState().getNumberPlayerPieces(TablutBoardState.SWEDE) - cur.getNumberPlayerPieces(TablutBoardState.SWEDE) > 0){
					this.heuristic -= 10;
				}
				// be killed
				if(parent.getBoardState().getNumberPlayerPieces(TablutBoardState.MUSCOVITE) - cur.getNumberPlayerPieces(TablutBoardState.MUSCOVITE) > 0){
					this.heuristic += 10;
				}
				//pawn that can be killed with one pawn
				if(isPawnVulnerable()){
					this.heuristic += 2;
				}
				// if we lose don't go there
				if(boardState.getWinner() == TablutBoardState.SWEDE){
					this.heuristic += 100;
				}
				// if we can win, move there!
				if(boardState.getWinner() == TablutBoardState.MUSCOVITE){
					this.heuristic = 0;
				}
			}
		}
		//heuristic if white pawns
		else{
			if(parent != null){
				this.heuristic = 0;
				this.depth = parent.getDepth()+1;
				Coord kcoord = cur.getKingPosition();
				int min = 0;
				if(kcoord != null){
					min = Coordinates.distanceToClosestCorner(kcoord);
					for(Coord c : Coordinates.getNeighbors(kcoord))
						if(!cur.coordIsEmpty(c)){
							this.heuristic += 5;
						}
				}
				
				// be killed
				if(parent.getBoardState().getNumberPlayerPieces(TablutBoardState.SWEDE) - cur.getNumberPlayerPieces(TablutBoardState.SWEDE) > 0){
					this.heuristic += 15;
				}
				//distance
				this.heuristic += min*2;
				
				// don't move to a vulnerable place, don't let the king be next to an opponent, don't kill yourself basically
				if( kcoord != null && isOpponentNextToKing(kcoord)){
					this.heuristic += 5;
				}
				//pawn that can be killed with one pawn
				if(isPawnVulnerable()){
					this.heuristic += 2;
				}
				// if we can win, move there!
				if(boardState.getWinner() == TablutBoardState.SWEDE){
					this.heuristic = 0;
				}
				
				if(boardState.getWinner() == TablutBoardState.MUSCOVITE){
					this.heuristic += 100;
				}
			}
		}
	}
	
	private boolean isOpponentNextToKing(Coord kcoord){
		if(kcoord == null){
			return true;
		}
		if(kcoord.x == 8 ){
			if(kcoord.y == 8){
				if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
					return true;
				}
			}
			else if(kcoord.y == 0){
				if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y))){
					return true;
				}
			}
			else if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
				return true;
			}
		}
		else if(kcoord.x == 0){
			if(kcoord.y == 8){
				if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
					return true;
				}
			}
			else if(kcoord.y == 0){
				if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1))){
					return true;
				}
			}
			else if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
				return true;
			}
		}
		else if(kcoord.y == 8){
			if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
				return true;
			}
		}
		else if(kcoord.y == 0){
			if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y))){
				return true;
			}
		}
		else{
			if(this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x+1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y+1)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x-1, kcoord.y)) || this.boardState.isOpponentPieceAt(Coordinates.get(kcoord.x, kcoord.y-1))){
				return true;
			}
		}
		
		return false;
	}
	
	// pawns can be killed with one pawn
	private boolean isPawnVulnerable(){
		if(move.getEndPosition().equals(Coordinates.get(0, 1)) || move.getEndPosition().equals(Coordinates.get(1, 0)) || move.getEndPosition().equals(Coordinates.get(7, 0)) || move.getEndPosition().equals(Coordinates.get(8, 1)) || move.getEndPosition().equals(Coordinates.get(1, 8)) || move.getEndPosition().equals(Coordinates.get(0, 7)) || move.getEndPosition().equals(Coordinates.get(8, 7)) || move.getEndPosition().equals(Coordinates.get(7, 8)) || move.getEndPosition().equals(Coordinates.get(3, 4)) || move.getEndPosition().equals(Coordinates.get(4, 3)) || move.getEndPosition().equals(Coordinates.get(4, 5)) || move.getEndPosition().equals(Coordinates.get(5, 4))){
			return true;
		}
		return false;
	}
	
	public TablutBoardState getBoardState(){
		return this.boardState;
	}
	
	public ArrayList<Node> getNext(){
		return this.next;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public int getHeuristic(){
		return this.heuristic;
	}
	
	public Node getParent(){
		return this.parent;
	}
	
	public Move getMove(){
		return this.move;
	}
	
}
