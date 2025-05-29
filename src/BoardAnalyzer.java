public class BoardAnalyzer {

    public static int evaluatePlayerPosition(Board board, int player) {

        int playerScore = calculatePointsForRound(board)[player];

        return playerScore;
    }

    private static int[] calculatePointsForRound(Board board) {
        
        int[] playerPointsForRound = new int[GameState.MAX_PLAYERS];  

        for(int ring = 0; ring < Board.RINGS; ring++) {
            int playerWithMostStones = board.getPlayerWithMostStones(ring);

            if(playerWithMostStones >= 0) {
                if(ring == 3) {
                    playerPointsForRound[playerWithMostStones] += 2;
                }
                else
                    playerPointsForRound[playerWithMostStones] += 1;
            }
        }
        return playerPointsForRound;
    }
}
