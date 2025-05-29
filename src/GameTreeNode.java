import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTreeNode {

    public static int createdNodes = 0;

    public int alpha;
    public int beta;

    private int currentDepth = 0;

    private GameTreeNode parent;
    public GameTreeNode[] children;

    private int globalFavoredPlayer;
    private int currentFavoredPlayer;

    private int cummulativeBoardValue;

    public boolean cut = false;

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

        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        cummulativeBoardValue = 0;
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
        this.currentFavoredPlayer = MoveHelper.nextPlayer(move.player); //next move favors this player 
        this.currentDepth = depth;
        this.globalFavoredPlayer = parent.globalFavoredPlayer;
        
        //setting the board to its new state for this node
        board = parent.board.clone();
        board.moveStoneForPlayer(move.player, move.first);
        board.moveStoneForPlayer(move.player, move.second);

        alpha = parent.alpha;
        beta = parent.beta;

        cummulativeBoardValue = currentCummulativeBoardValue(this);
    }

    /**
     * Recursive call of {@link #generateChildren(GameTreeNode)}
     * Stops once the given max_depth has been reached or no valid moves are available anymore.
     *
     */
    public static void generateChildrenRecursively(GameTreeNode gtn, int max_depth) {
        
        createdNodes++;

        //end conditions for recursion
        //leafs reached
        if(gtn.currentDepth >= max_depth) {
            generateAlphaBetaValueForLeafs(gtn);
            updateAlphaBetaVertically(gtn);
            return;
        }

        //Recursively building the tree by adding children to this node
        if(!generateChildren(gtn)) { //...but only if there are valid moves
            updateAlphaBetaVertically(gtn);
            return;   
        }
        //Tree building
        for(int i = 0; i < gtn.children.length; i++) {
            
            generateChildrenRecursively(gtn.children[i], max_depth);

            //Updating the current node value, based on the children
            gtn.alpha = Math.max(gtn.alpha, gtn.children[i].alpha);
            gtn.beta = Math.min(gtn.beta, gtn.children[i].beta);
            

            if(shouldCut(gtn)) {
                // System.out.println("cut!");
                break;
            }
        }

        //Updating the value of the parent, to ensure that siblings stay updated as well.
        //Siblings may reference parent value for horizontal updates
        if(gtn.parent != null) {
            gtn.parent.alpha = Math.max(gtn.alpha, gtn.parent.alpha);
            gtn.parent.beta = Math.min(gtn.beta, gtn.parent.beta);
        }
    }

    private static void updateAlphaBetaVertically(GameTreeNode gtn) {
        while (gtn.parent != null) {
            if (gtn.parent.currentFavoredPlayer == gtn.parent.globalFavoredPlayer) { // maximizing
                gtn.parent.alpha = Math.max(gtn.parent.alpha, gtn.alpha);
            } else { // minimizing
                gtn.parent.beta = Math.min(gtn.parent.beta, gtn.beta);
            }

            // gtn.parent.alpha = Math.max(gtn.parent.alpha, gtn.alpha);
            // gtn.parent.beta = Math.min(gtn.parent.beta, gtn.beta);
            gtn = gtn.parent;
        }
    }

    /**
     * Should be called after the first child element has been evaluated
     * 
     * @param gtn
     * @return
     */
    private static boolean shouldCut(GameTreeNode gtn){

        boolean ret = false;

        if(gtn.parent == null)
            return false;

        //ALPHA CUT
        if(gtn.parent.currentFavoredPlayer == gtn.parent.globalFavoredPlayer)
            if(gtn.parent.alpha > gtn.alpha)
                ret = true;
        
        if(gtn.children.length > 0)
        //BETA CUT
        //needs to ensure that when a better alpha value is among one child, the tree gets cut
        if(gtn.children[0].currentFavoredPlayer == gtn.children[0].globalFavoredPlayer) {
            if(gtn.alpha > gtn.parent.alpha)
                return true;
        }

        //marking this node as having a cut
        if(ret){
            gtn.cut = true;
        }

        return ret;
    }

    /**
     * Automatically picks either the max (alpha) or min (beta) value, depending on whos turn it is.
     * This forms the basis for the Alpha Beta search.
     * 
     * @param gtn The {@link #GameTreeNode} to propagate the values up to
     */
    private static void generateAlphaBetaValueForLeafs(GameTreeNode gtn) {
        
        if(gtn.children == null || gtn.children.length == 0) {
            int value = currentCummulativeBoardValue(gtn);
            gtn.alpha = value;
            gtn.beta = value;
        }
    }

    /**
     * Generates Children for the given node, as long as there are valid moves left.
     * 
     * @param gtn The {@link GameTreeNode} to populate
     * @return If valid moves were found
     */
    public static boolean generateChildren(GameTreeNode gtn) {
        
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

    private static int currentCummulativeBoardValue(GameTreeNode gtn) {

        if(gtn.parent == null)
            return 0; //root has no parent

        //evaluate the current score, based on who moved last
        int boardScore = gtn.parent.cummulativeBoardValue;
        if(gtn.parent.currentFavoredPlayer != gtn.parent.globalFavoredPlayer) {
            boardScore -= BoardAnalyzer.evaluatePlayerPosition(gtn.board, gtn.parent.currentFavoredPlayer); //lower score
        }

        boardScore += BoardAnalyzer.evaluatePlayerPosition(gtn.board, gtn.parent.globalFavoredPlayer); //push score
        return boardScore;
    }

    @Override
    public String toString() {
    return "GameTreeNode{" +
            "move=" + move +
            ", alpha=" + alpha +
            ", beta=" + beta +
            ", cummulativeBoardValue=" + cummulativeBoardValue +
            ", currentDepth=" + currentDepth +
            ", currentFavoredPlayer=" + currentFavoredPlayer +
            ", globalFavoredPlayer=" + globalFavoredPlayer +
            ", children=" + (children != null ? children.length : 0) +
            '}';
    }

    public int getCurrentFavoredPlayer() {
        return currentFavoredPlayer;
    }

    public int getGlobalFavoredPlayer() {
        return globalFavoredPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public int getDepth() {
        return currentDepth;
    }
}
