public class BoardAnalyzer {

    //if issues arise, change so that only the position of the given player gets evaluated 
    public static int evaluatePlayerPosition(Board board, int player) {

        return calculatePointsForRound(board)[player];

        // int[] playerPointsThisRound = calculatePointsForRound(board);

        // int topOpponentPointsThisRound = 0;

        // for(int i = 0; i < GameState.MAX_PLAYERS; i++) {
        //     if(i == player) //don't calculate for the evaluated player, to enable comparisons
        //         continue;

        //     if (playerPointsThisRound[i] > topOpponentPointsThisRound) {
        //         topOpponentPointsThisRound = playerPointsThisRound[i];
        //     }
        // }

        // // returned value not smaller than 0
        // return Math.max(0, playerPointsThisRound[player] - topOpponentPointsThisRound);
    }

    private static int[] calculatePointsForRound(Board board) {
        
        int[] playerPointsForRound = new int[GameState.MAX_PLAYERS];  

        for(int ring = 0; ring < Board.RINGS; ring++) {
            int playerWithMostStones = board.getPlayerWithMostStones(ring);

            if(playerWithMostStones >= 0) {

                if(ring == 3)
                    playerPointsForRound[playerWithMostStones] += 2;
                else
                    playerPointsForRound[playerWithMostStones] += 1;
            }
        }
        return playerPointsForRound;
    }
}
