import lenz.htw.eimer.Move;

public class GameState {

    public static final int MAX_PLAYERS = 3;

    private int round = 0;

    private Board board = new Board(false);

    private int[] playerScores;

    public GameState() {
        playerScores = new int[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++) {
            playerScores[i] = 0;
        }
    }

    public void incrementRound() {
        round++;
    }

    public void moveStones(Move move) {
        board.moveStoneForPlayer(move.player, move.first);
        board.moveStoneForPlayer(move.player, move.second);
    }

    public Board getBoard() {
        return board;
    }

    public Board getBoardByValue() {
        return board.clone();
    }

    public int getRound() {
        return round;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------GAMESTATE----------------").append("\n");
        sb.append("Round: ").append(round).append("\n");

        for (int i = 0; i < MAX_PLAYERS; i++) {
            sb.append("Player ").append(i + 1).append(": ");
            for (int j = 0; j < 4; j++) {
                sb.append(board.getStonesOnRingForPlayer(i, j)).append(" ");
                //sb.append(stones[i][j]).append(" ");
            }
            sb.append("\n");
        }
        sb.append("Scores: ");
        for (int i = 0; i < MAX_PLAYERS; i++) {
            sb.append(playerScores[i]).append(" ");
        }

        return sb.toString();
    }
}
