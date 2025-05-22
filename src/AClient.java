import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.eimer.net.NetworkClient;
import lenz.htw.eimer.*;


public class AClient {
    public static void main(String[] args) throws IOException {

        NetworkClient client = new NetworkClient("127.0.0.1", "AClient", ImageIO.read(new File("res/playerIconA.png")));

        GameState gameState = new GameState();
        int playerNumber = client.getMyPlayerNumber();
        
        GameTree gameTree = new GameTree(playerNumber);
        
        PathFinder pathFinder = new PathFinder(playerNumber, gameState);

        client.getTimeLimitInSeconds();
        client.getExpectedNetworkLatencyInMilliseconds();

        Move move;

        System.out.println("Player Number: " + (playerNumber+1) + " ,index: "+ playerNumber);
        System.out.println("Unique available moves:"+ (PathFinder.findAllLegalMovesList(gameState.getBoard(), playerNumber)).size());

        for (;;) {
            while ((move = client.receiveMove()) != null) {
                System.out.println("Move played: "+ move);
                gameState.moveStones(move);

                System.out.println("Moves available for next Player: "+ PathFinder.findAllLegalMovesList(gameState.getBoard(), MoveHelper.nextPlayer(move.player)) + "\n");
                // Zug in meine Spielbrettrepräsentation einbauen
            }
            
            //move = besonders clever berechneter Zug
            // boolean dontMove = true;
            // while(dontMove){
            //     String input = System.console().readLine();
            //     if(input == "");
            //         dontMove = false;
            // }

            move = pathFinder.pickRandomMove();

            System.out.println("Sending Move: "+ move);
            client.sendMove(move);
            gameState.calculateScores();

            gameState.incrementRound();
        }
    }
}
