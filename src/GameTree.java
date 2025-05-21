import java.util.HashMap;
import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTree {

    //four rounds
    public static int MAX_DEPTH = 1*GameState.MAX_PLAYERS;

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
        LinkedList<Move> optimalPath = GameTreeNode.generateChildrenRecusively(root, MAX_DEPTH);
        printMoveList(optimalPath);
    }

    private void printMoveList(LinkedList<Move> moves) {
    for (Move move : moves) {
        System.out.println("================================");
        System.out.println(move);
    }
}
}
