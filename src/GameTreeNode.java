import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTreeNode {

    public int alpha;
    public int beta;

    private int currentDepth = 0;

    private GameTreeNode parent;
    public GameTreeNode[] children;

    private int globalFavoredPlayer;
    private int currentFavoredPlayer;

    private int cummulativeBoardValue;

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
        
        //end conditions for recursion
        //leafs reached
        if(gtn.currentDepth >= max_depth) {
            generateAlphaBetaValueForLeafs(gtn);
            updateAlphaBetaVertically(gtn);
            return;
        }
        
        // alpha/beta cut
        // if(shouldCut(gtn)){
        //     updateAlphaBetaVertically(gtn);
        //     return;
        // }

        //Recursively building the tree from this node
        if(!generateChildren(gtn)) { //...but only if there are valid moves
            updateAlphaBetaVertically(gtn);
            return;   
        }
        //Tree building
        for(int i = 0; i < gtn.children.length; i++) {
            
            //check if a cut should happen, based on the found values within the siblings
            // updateAlphaBetaHorizontally(gtn, i);
            if(shouldCut(gtn)) {
                // System.out.println("cut!");
                break;
            }
            
            generateChildrenRecursively(gtn.children[i], max_depth);

            //TODO OPTIMIZE!
            if (gtn.children != null && gtn.children.length > 0) {
                gtn.alpha = Integer.MIN_VALUE;
                gtn.beta = Integer.MAX_VALUE;
                for (GameTreeNode child : gtn.children) {
                    gtn.alpha = Math.max(gtn.alpha, child.alpha);
                    gtn.beta = Math.min(gtn.beta, child.beta);
                }
            }
        }
    }

    /**
     * Updates the Alpha Beta value based on the siblings
     * 
     * @param gtn
     * @param ownChildIndex
     */
    private static void updateAlphaBetaHorizontally(GameTreeNode gtn, int ownChildIndex){
        // if(gtn.parent == null || ownChildIndex == 0)
        //     return;
        
        // if(gtn.currentFavoredPlayer == gtn.globalFavoredPlayer) {
        //     gtn.alpha =  Math.max(gtn.parent.children[ownChildIndex-1].alpha, gtn.alpha);
        // } else {
        //     gtn.beta = Math.min(gtn.parent.children[ownChildIndex-1].beta, gtn.beta);
        // }
        

        if(gtn.parent == null)
            return;
        
        if(gtn.currentFavoredPlayer == gtn.globalFavoredPlayer) {
            gtn.alpha = gtn.parent.alpha;
        } else {
            gtn.beta = gtn.parent.beta;
        }


        // if(gtn.currentFavoredPlayer == gtn.globalFavoredPlayer) {
        //     gtn.alpha = fetchAlphaFromSibling(gtn, ownChildIndex);
        // } else {
        //     gtn.beta = fetchBetaFromSibling(gtn, ownChildIndex);
        // }
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

    private static boolean shouldCut(GameTreeNode gtn){

        if(gtn.parent == null)
            return false;

        if(gtn.parent.currentFavoredPlayer == gtn.parent.globalFavoredPlayer)
            return gtn.parent.alpha > gtn.alpha;
        else //TODO check beta cut!
            return gtn.alpha > gtn.parent.alpha;
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
            
            //more textbook approach
            // if (gtn.currentFavoredPlayer == gtn.globalFavoredPlayer) {
            //     gtn.alpha = value;
            // } else {
            //     gtn.beta = value;
            // }
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

    //FIXME potentially revert to original
    private static int currentCummulativeBoardValue(GameTreeNode gtn) {

        if(gtn.parent == null)
            return 0; //root has no parent

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
