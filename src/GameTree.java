import java.util.HashMap;
import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTree {

    //four rounds
    public static int MAX_DEPTH = 2*GameState.MAX_PLAYERS;

    private GameTreeNode root;

    private HashMap<String, GameTreeNode> archive;

    private LinkedList<GameTreeNode> bestNodePath = new LinkedList<GameTreeNode>();

    public GameTree(int favoredPlayer){
        Board board = new Board(false);
        root = new GameTreeNode(null, board, favoredPlayer);

        buildTree();
        System.out.println("DONE!");

        printNodeList(bestNodePath);
    }

    private void buildTree() {
        GameTreeNode.generateChildrenRecursively(root, MAX_DEPTH);
        
        GameTreeNode gtn = root;

        int index = 0;
        GameTreeNode node = root;
        while(node != null) {
            System.out.println(node);
            if(node.children != null && node.children.length > 0)
                node = node.children[index];
            else
                node = null;
        }
    }

    private void fillBestMoveList(GameTreeNode gtn) {
        while(gtn != null){
            bestNodePath.add(gtn);
            if(gtn.getCurrentFavoredPlayer() == gtn.getGlobalFavoredPlayer()){
                gtn = pickAlphaMove(gtn);
            } else {
                gtn = pickBetaMove(gtn);
            }
        }
    }

    private GameTreeNode pickAlphaMove(GameTreeNode gtn) {
        if(gtn.children == null)
            return null;

        for(int i = 0; i < gtn.children.length; i++) {
            if(gtn.children[i].alpha == gtn.alpha)
                return gtn.children[i];
        }
        return null;
    }

    private GameTreeNode pickBetaMove(GameTreeNode gtn) {
        if(gtn.children == null)
            return null;

        for(int i = 0; i < gtn.children.length; i++) {
            if(gtn.children[i].beta == gtn.beta)
                return gtn.children[i];
        }
        return null;
    }

    private void printNodeList(LinkedList<GameTreeNode> gtns){
        for(GameTreeNode gtn : gtns) {
            System.out.println("================================");
            System.out.println(gtn);
            System.out.println(gtn.getBoard());
        }
    }
}
