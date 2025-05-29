public class Tester {
    public static void main(String[] args) {

        int playerNumber = 0;

        GameTree gameTree = new GameTree(playerNumber, 2);

        if(gameTree.root.alpha == 20){
            System.out.println("ALPHA SUCCESS");
        } else {
            System.out.println("ALPHA FAIL");
        }


        if(gameTree.root.beta == -8){
            System.out.println("BETA SUCCESS");
        } else {
            System.out.println("BETA FAIL");
        }

        System.out.println("Created Nodes: "+GameTreeNode.createdNodes);

    }
}
