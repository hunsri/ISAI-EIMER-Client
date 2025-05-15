import java.nio.file.Path;

import lenz.htw.eimer.Move;

public class GameTreeNode {

    public int alpha;
    public int beta;

    public final static int MAX_DEPTH = 20; 
    private int currentDepth = 0;

    private GameTreeNode parent;
    private GameTreeNode[] children;

    // private int currentPlayer;
    private int favoredPlayer;

    // int lastFirstMove = 1000;
    // int lastSecondMove = 1000;

    Move move;

    private Board board;

    /**
     * Initializes the root at a set moment on the board.
     * The calculations will favor the current player.
     * 
     * @param currentPlayer
     * @param board
     */
    public GameTreeNode(Move move, Board board, int favoredPlayer) {
        
        this.parent = null;
        this.board = board;
        
        this.move = move;
        this.favoredPlayer = favoredPlayer; 
        this.currentDepth = 0;

        alpha = Integer.MAX_VALUE;
        beta = Integer.MIN_VALUE;
    }

    public GameTreeNode(GameTreeNode parent, int favoredPlayer, Move move, int depth) {
        this.parent = parent;

        //this.currentPlayer = currentPlayer;
        this.move = move;
        this.favoredPlayer = favoredPlayer;
        this.currentDepth = depth;
    }

    public void generateChildren(GameTreeNode gtn) {
        
        boolean[][] moves = PathFinder.findAllLegalMoves(board, move.player);
    }
}
