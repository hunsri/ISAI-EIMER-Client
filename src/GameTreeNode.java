import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTreeNode {

    public int alpha;
    public int beta;
    public int nodeValue;

    private int currentDepth = 0;

    private GameTreeNode parent;
    private GameTreeNode[] children;

    private int globalFavoredPlayer;
    private int currentFavoredPlayer;

    Move move;

    private Board board;

    /**
     * Initializes the root at a set moment on the board.
     * The calculations will favor the globalFavoredPlayer.
     * 
     * @param move
     * @param board
     * @param globalFavoredPlayer
     */
    public GameTreeNode(Move move, Board board, int globalFavoredPlayer) {
        
        if(move == null) {
            //setting the last player so that the next player would be player 0
            //1000 is a mockup illegal move
            move = new Move(GameState.MAX_PLAYERS-1, 1000, 1000);
        }

        this.parent = null;
        this.board = board;
        
        this.move = move;
        this.currentFavoredPlayer = 0;
        this.globalFavoredPlayer = globalFavoredPlayer; 
        this.currentDepth = 0;

        alpha = Integer.MAX_VALUE;
        beta = Integer.MIN_VALUE;
    }

    /**
     * Constructor for initialization of child nodes
     * 
     * @param parent
     * @param move
     * @param depth
     */
    public GameTreeNode(GameTreeNode parent, Move move, int depth) {

        this.parent = parent;
        this.move = move;
        this.currentFavoredPlayer = MoveHelper.nextPlayer(move.player);
        this.currentDepth = depth;
        
        //setting the board to its new state for this node
        board = parent.board.clone();
        board.moveStoneForPlayer(move.player, move.first);
        board.moveStoneForPlayer(move.player, move.second);

        nodeValue = BoardAnalyzer.evaluatePlayerPosition(board, currentFavoredPlayer);
        
        if(globalFavoredPlayer == currentFavoredPlayer) {
            nodeValue = parent.nodeValue + nodeValue;
        } else {
            nodeValue = parent.nodeValue - nodeValue;
        }
    }

    /**
     * Recursive call of {@link #generateChildren(GameTreeNode)}
     * Stops once the given max_depth has been reached or no valid moves are available anymore.
     *
     */
    public static void generateChildrenRecusively(GameTreeNode gtn, int max_depth) {
        
        //end condition for recursion
        if(gtn.currentDepth >= max_depth){
            return;
        }
        
        //Recursively building the tree
        if(generateChildren(gtn)) {
            for(int i = 0; i < gtn.children.length; i++) {
                generateChildrenRecusively(gtn.children[i], max_depth);
            }
        }
    }

    /**
     * Generates Children for the given node, as long as there are valid moves left.
     * 
     * @param gtn The {@link GameTreeNode} to populate
     * @return If valid moves were found
     */
    public static boolean generateChildren(GameTreeNode gtn) {
        
        // if(gtn.currentDepth >= MAX_DEPTH)
        //     return false;

        //finding all legal moves for the next player
        LinkedList<Move> moves = PathFinder.findAllLegalMovesList(gtn.board, MoveHelper.nextPlayer(gtn.move.player));
        
        gtn.children = new GameTreeNode[moves.size()];

        for(int i = 0; i < moves.size(); i++) {
            
            gtn.children[i] = new GameTreeNode(gtn, moves.get(i), gtn.currentDepth+1);
        }
        
        //checks if valid moves exist
        if(gtn.children.length > 0)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
    return "GameTreeNode{" +
            "move=" + move +
            ", nodeValue=" + nodeValue +
            ", currentDepth=" + currentDepth +
            ", currentFavoredPlayer=" + currentFavoredPlayer +
            ", globalFavoredPlayer=" + globalFavoredPlayer +
            ", children=" + (children != null ? children.length : 0) +
            '}';
    }
}
