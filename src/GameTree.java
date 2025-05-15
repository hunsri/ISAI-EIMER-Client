import java.util.HashMap;

public class GameTree {

    public static int MAX_DEPTH = 10;

    private GameTreeNode root;

    private HashMap<String, GameTreeNode> archive; 

    public GameTree(int favoredPlayer){
        Board board = new Board(false);

        // find all the possible first moves
        PathFinder.findAllLegalMoves(board, 0);

    }
}
