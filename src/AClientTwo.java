import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.eimer.net.NetworkClient;
import lenz.htw.eimer.*;


public class AClientTwo {
    public static void main(String[] args) throws IOException {

        NetworkClient client = new NetworkClient("127.0.0.1", "BAClient", ImageIO.read(new File("res/playerIconA.png")));

        GameState gameState = new GameState();
        int ownPlayerNumber = client.getMyPlayerNumber();
        
        // GameTree gameTree = new GameTree(ownPlayerNumber, 1);
        
        // PathFinder pathFinder = new PathFinder(ownPlayerNumber, gameState);

        client.getTimeLimitInSeconds();
        client.getExpectedNetworkLatencyInMilliseconds();

        Move move;

        System.out.println("Player Number: " + (ownPlayerNumber+1) + " ,index: "+ ownPlayerNumber);

        for (;;) {
            while ((move = client.receiveMove()) != null) {
                System.out.println("Move played: "+ move);
                gameState.moveStones(move);
                
                if(isMyTurn(move.player, ownPlayerNumber)) {
                    move = optimalMove(ownPlayerNumber, gameState.getBoard().clone(), move);

                    System.out.println("Sending Move: "+ move);
                    client.sendMove(move);
                }
                // Zug in meine Spielbrettrepräsentation einbauen
            }

            if(gameState.getRound() == 0 && ownPlayerNumber == 0) {
                move = optimalMove(ownPlayerNumber, gameState.getBoard().clone(), null);
                System.out.println("Sending Move: "+ move);
                client.sendMove(move);
            }

            //move = besonders clever berechneter Zug
            // boolean dontMove = true;
            // while(dontMove){
            //     String input = System.console().readLine();
            //     if(input == "");
            //         dontMove = false;
            // }

            // move = pathFinder.pickRandomMove();

            gameState.incrementRound();
        }
    }

    private static boolean isMyTurn(int lastPlayer, int ownPlayerNumber) {
        if(MoveHelper.nextPlayer(lastPlayer) == ownPlayerNumber)
            return true;
        
        return false;
    }

    private static Move optimalMove(int ownPlayerNumber, Board board, Move lastMove) {
        int treeDepth = 2;

        GameTree gameTree = new GameTree(ownPlayerNumber, treeDepth, board, lastMove);
        return gameTree.optimalMove();
    }
}
