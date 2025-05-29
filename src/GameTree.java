import java.util.LinkedList;

import lenz.htw.eimer.Move;

public class GameTree {

    public GameTreeNode root;

    private LinkedList<GameTreeNode> bestNodePath = new LinkedList<GameTreeNode>();

    public GameTree(int favoredPlayer, int treeRoundDepth){
        Board board = new Board(false);
        root = new GameTreeNode(null, board, favoredPlayer);

        buildTree(treeRoundDepth*GameState.MAX_PLAYERS);

        fillBestMoveList(root);
        // printNodeList(bestNodePath);
    }

    public GameTree(int favoredPlayer, int treeRoundDepth, Board board, Move lastMove) {
        
        
        root = new GameTreeNode(lastMove, board.clone(), favoredPlayer);

        buildTree(treeRoundDepth*GameState.MAX_PLAYERS);

        fillBestMoveList(root);
    }

    private void buildTree(int treeDepth) {
        GameTreeNode.generateChildrenRecursively(root, treeDepth);

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

        int index = -1;

        // System.out.println("----"+ gtn.getDepth() +"----");

        for(int i = 0; i < gtn.children.length; i++) {
            // System.out.println("["+ i + "] ALPHA: "+gtn.children[i].alpha + " | " + gtn.alpha + " | " + gtn.beta + " |cut?: " + gtn.children[i].cut);
            if(gtn.children[i].alpha >= gtn.alpha)
                index = i;
        }
        
        return gtn.children[index];
    }

    private GameTreeNode pickBetaMove(GameTreeNode gtn) {
        if(gtn.children == null)
            return null;

        int index = -1;

        // System.out.println("----"+ gtn.getDepth() +"----");

        for(int i = 0; i < gtn.children.length; i++) {
            // System.out.println("["+ i + "] BETA: "+gtn.children[i].beta + " | " + gtn.alpha + " | " + gtn.beta + " |cut?: " + gtn.children[i].cut);
            if(gtn.children[i].beta <= gtn.beta)
                index = i;
        }

        return gtn.children[index];
    }

    public Move optimalMove() {
        return bestNodePath.get(1).move; //accessing the first move after the provided move;
    }

    private void printNodeList(LinkedList<GameTreeNode> gtns){
        for(GameTreeNode gtn : gtns) {
            System.out.println("================================");
            System.out.println(gtn);
            // System.out.println(gtn.getBoard());
        }
    }
}
