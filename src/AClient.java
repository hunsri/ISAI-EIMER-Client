import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.eimer.net.NetworkClient;
import lenz.htw.eimer.*;


public class AClient {
    public static void main(String[] args) throws IOException {


        String clientName = "AClient";
        int randomness = 0;
        int argumentsFound = 0;

        // Check if args[0] is of type String
        if (args.length > 0 && args[0] instanceof String) {
            clientName = args[0];
            argumentsFound += 1;
        }
        if(args.length > 1 && args[1] instanceof String) {
            try {
                randomness = Integer.parseInt(args[1]);
                argumentsFound += 1;
            } catch (NumberFormatException e) {
                System.err.println("Invalid randomness value, using default: " + randomness);
            }
        }
        System.out.println("Arguments found: "+ argumentsFound);

        NetworkClient client = new NetworkClient("127.0.0.1", clientName, ImageIO.read(new File("res/playerIconA.png")));

        GameState gameState = new GameState();
        int ownPlayerNumber = client.getMyPlayerNumber();

        client.getTimeLimitInSeconds();
        client.getExpectedNetworkLatencyInMilliseconds();

        Move move;

        System.out.println("Player Number: " + (ownPlayerNumber+1) + " ,index: "+ ownPlayerNumber);

        for (;;) {
            while ((move = client.receiveMove()) != null) {
                System.out.println("Move played: "+ move);
                gameState.moveStones(move);
                
                handleMove(client, move, ownPlayerNumber, gameState, randomness);
            }

            // ensuring that client is moving if it gets the first move of the game
            if(gameState.getRound() == 0 && ownPlayerNumber == 0) {
                move = optimalMove(ownPlayerNumber, gameState.getBoard().clone(), null);
                System.out.println("Sending Move: "+ move);
                client.sendMove(move);
            }

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

    private static boolean shouldRandom(float randomnessValue) {
        int randomValue = (int) (Math.random() * 100);
        return randomValue < randomnessValue;
    }

    private static void handleMove(NetworkClient client, Move lastMove, int ownPlayerNumber, GameState gameState, int randomnessValue) {
        if(!isMyTurn(lastMove.player, ownPlayerNumber)) {
            return;
        }
        
        Move move = null;
        PathFinder pathFinder = new PathFinder(ownPlayerNumber, gameState);

        if(shouldRandom(randomnessValue)) {
            move = pathFinder.pickRandomMove();
        } else {
            move = optimalMove(ownPlayerNumber, gameState.getBoard(), lastMove);
        }
        
        if(move != null) {
            System.out.println("Sending Move: "+ move);
            client.sendMove(move);
        }
    }
}
