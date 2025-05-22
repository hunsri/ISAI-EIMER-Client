import java.util.HashMap;
import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTree {

    //four rounds
    public static int MAX_DEPTH = 2*GameState.MAX_PLAYERS;

    private GameTreeNode root;

    private HashMap<String, GameTreeNode> archive;

    private LinkedList<Move> bestPath = new LinkedList<Move>();

    public GameTree(int favoredPlayer){
        Board board = new Board(false);
        root = new GameTreeNode(null, board, favoredPlayer);

        buildTree();
        System.out.println("DONE!");
    }

    private void buildTree() {
        GameTreeNode.generateChildrenRecusively(root, MAX_DEPTH);
        
        GameTreeNode gtn = root;

        while(gtn != null) {
            bestPath.add(gtn.getBestNextMove());
            gtn = gtn.getBestNextMoveNode();
        }
        
        printMoveList(bestPath);
    }

    private void printMoveList(LinkedList<Move> moves) {
    for (Move move : moves) {
        System.out.println("================================");
        System.out.println(move);
    }
}
}
