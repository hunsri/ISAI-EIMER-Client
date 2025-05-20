import java.util.HashMap;

public class GameTree {

    //four rounds
    public static int MAX_DEPTH = 2*GameState.MAX_PLAYERS;

    private GameTreeNode root;

    private HashMap<String, GameTreeNode> archive; 

    public GameTree(int favoredPlayer){
        Board board = new Board(false);
        root = new GameTreeNode(null, board, favoredPlayer);

        // find all the possible first moves
        //PathFinder.findAllLegalMoves(board, 0);
        buildTree();
        System.out.println("DONE!");
    }

    private void buildTree() {
        GameTreeNode.generateChildrenRecusively(root, MAX_DEPTH);
    }
}
