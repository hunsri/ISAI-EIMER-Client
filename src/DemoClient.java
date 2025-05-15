import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.eimer.net.NetworkClient;
import lenz.htw.eimer.*;

public class DemoClient {

    public static void main(String[] args) throws IOException {
        NetworkClient client = new NetworkClient("127.0.0.1", "DemoClient", ImageIO.read(new File("res/playerIconB.png")));

        GameState gameState = new GameState();
        PathFinder pathFinder = new PathFinder(0, gameState);

        int playerNumber = client.getMyPlayerNumber();
        client.getTimeLimitInSeconds();
        client.getExpectedNetworkLatencyInMilliseconds();

        Move move;
        // move.first;    //+0,1,2 -> Zug nach innen, -1,-2 -> Zug nach außen
        // move.second;
        // move.player;
        
        for (;;) {
            while ((move = client.receiveMove()) != null) {
                gameState.moveStones(move);
                // Zug in meine Spielbrettrepräsentation einbauen
            }
            //move = besonders clever berechneter Zug

            pathFinder = new PathFinder(playerNumber, gameState);
            move = pathFinder.pickRandomMove();
            System.out.println("Sending Move: "+ move);

            client.sendMove(move);

            gameState.incrementRound();
        }
    }
}