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

        for (;;) {
            while ((move = client.receiveMove()) != null) {
                //System.out.println("Move: "+ move);
                gameState.moveStones(move);
                // Zug in meine Spielbrettrepräsentation einbauen
            }
            
            //move = besonders clever berechneter Zug
            // boolean dontMove = true;
            // while(dontMove){
            //     String input = System.console().readLine();
            //     if(input == "");
            //         dontMove = false;
            // }

            pathFinder = new PathFinder(playerNumber, gameState);
            move = pathFinder.pickRandomMove();
            //move = new Move(playerNumber, 0, 1);

            System.out.println("Sending Move: "+ move);
            client.sendMove(move);
            gameState.calculateScores();
            System.out.println(gameState);

            gameState.incrementRound();
        }
    }
}
