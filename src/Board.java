public class Board {

    private int[][] stones = new int[3][4];

    public final static int RINGS = 4;

    public Board(boolean blank) {
        // take board representation out of gamestate
        
        if(!blank)
            initBoard();
    }

    public void moveStoneForPlayer(int playerID, int move) {
        
        boolean moveInside = move >= 0 ? true : false;
        move = Math.abs(move);

        stones[playerID][move]--;

        if(moveInside) {
            stones[playerID][move + 1]++;
        } else {
            stones[playerID][move - 1]++;
        }
    }

    private void initBoard() {
        //i - players
        //j - rings
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                
                if (j == 3)
                    stones[i][j] = 0;
                else
                    stones[i][j] = 2;
            }
        }
    }

    public int getPlayerWithMostStones(int ring) {
        int playerWithMostStones = -1;
        int current_max = 0;

        // Calculate for the given ring
        for(int i = 0; i < GameState.MAX_PLAYERS; i++) {
            if(stones[i][ring] == current_max)
                playerWithMostStones = -1; // Tie, no one gets the score

            if(stones[i][ring] > current_max)
            {
                current_max = stones[i][ring];
                playerWithMostStones = i;
            }
        }

        return playerWithMostStones;
    }

    public int getStonesOnRingForPlayer(int player, int ring) {
        return stones[player][ring];
    }

    @Override
    public Board clone() {
        Board ret = new Board(true);
        
        //deep copy to mitigate call by reference
        ret.stones = new int[this.stones.length][this.stones[0].length];
        for (int i = 0; i < this.stones.length; i++) {
            System.arraycopy(this.stones[i], 0, ret.stones[i], 0, this.stones[i].length);
        }
        return ret;
    }

    private int playerScoreForBoard(int player) {

        int scoreForThisBoard = 0;

        for(int i = 0; i < RINGS; i++)
        {
            if(getPlayerWithMostStones(i)== player)
            {
                if(i == 3)
                    scoreForThisBoard += 2;
                else
                    scoreForThisBoard += 1;
            }
        }
        return scoreForThisBoard;
    }
}
