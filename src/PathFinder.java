import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import lenz.htw.eimer.Move;

public class PathFinder {

    private int playerID;
    private GameState gameState;

    private static final int VALIDITY_INDEX = 5; 

    public PathFinder(int playerID, GameState gameState) {
        this.playerID = playerID;
        this.gameState = gameState;
    } 

    private int highestEnemyCount(int ring) {
        
        int ret = 0;
        
        for(int i = 0; i < GameState.MAX_PLAYERS; i++) {
            if(i == playerID)
                continue;
            ret = gameState.getBoard().getStonesOnRingForPlayer(i, ring);
        }

        return ret;
    }

    public Move pickRandomMove() {
        Move move = new Move(playerID, 0, 0);

        boolean[][] legalMoves = findAllLegalMoves(gameState.getBoard(), playerID);
        
        int firstRandomIndex = -1;
        int secondRandomIndex = -1;

        // quits search after the set amount of max searches
        int maxSearch = 20;
        int searched = 0;
        
        while(firstRandomIndex < 0 && searched < maxSearch) {
            firstRandomIndex = ThreadLocalRandom.current().nextInt(0, 5);
            //resets search if there are no legal secondary moves
            if(legalMoves[firstRandomIndex][5] == false)
                firstRandomIndex = -1;
            searched++;
        }

        if(searched >= maxSearch){
            return forceMove();
        }

        while(secondRandomIndex < 0) {
            secondRandomIndex = ThreadLocalRandom.current().nextInt(0, 5);
            //resets if the move isn't legal
            if(legalMoves[firstRandomIndex][secondRandomIndex] == false)
                secondRandomIndex = -1;
        }

        move.first = convertIndexToMove(firstRandomIndex);
        move.second = convertIndexToMove(secondRandomIndex);

        return move;
    }

    public static LinkedList<Move> findAllLegalMovesList(Board board, int player) {
        return convertToMoveList(findAllLegalMoves(board, player), player);
    }

    /**
     * Finds all a legal Moves for the current GameState.
     * 
     * The returned booleans indicate the legal moves
     * The first dimension for the first move
     * <ul>
     * <li> <b> index -> move </b> </li>
     * <li> 0 -> 0 </li>
     * <li> 1 -> 1 </li>
     * <li> 2 -> -1 </li>
     * <li> 3 -> 2 </li>
     * <li> 4 -> -2 </li>
     * </ul>
     * In the second dimension <b>Index 5</b> holds whether the primary move has a valid second move.<br>
     * A returned index of <b>[5] == true</b> indicates that one or more secondary moves are available 
     * 
     * @param board The board setup to be analyzed
     * @return The available moves
     */
    private static boolean[][] findAllLegalMoves(Board board, int playerID) {

        boolean[][] moves = new boolean[5][6];

        LinkedList<Board> usedStates = new LinkedList<Board>(); 
        usedStates.add(board.clone());

        if(board.getStonesOnRingForPlayer(playerID, 0) > 0) {
            moves[0] = findSecondaryValidMove(board, playerID, 0, usedStates);
        }
        if(board.getStonesOnRingForPlayer(playerID, 1) > 0) {
            moves[1] = findSecondaryValidMove(board, playerID, 1, usedStates);
            moves[2] = findSecondaryValidMove(board, playerID, -1, usedStates);
        }
        if(board.getStonesOnRingForPlayer(playerID, 2) > 0) {
            moves[3] = findSecondaryValidMove(board, playerID, 2, usedStates);
            moves[4] = findSecondaryValidMove(board, playerID, -2, usedStates);
        }
        return moves;
    }

    /**
     * Index 5 holds whether the primary move has a valid second move
     * A returned index of [5] == true indicates that one or more secondary moves are available
     * 
     * @param board The board setup to be analyzed
     * @param playerID
     * @param firstMove The move that this second move is dependent on
     * @return All allowed secondary moves, given a first move
     */
    private static boolean[] findSecondaryValidMove(Board board, int playerID, int firstMove, LinkedList<Board> forbiddenStates){     
        
        boolean[] secondMoves = new boolean[6];

        Board tempBoard = board.clone();
        tempBoard.moveStoneForPlayer(playerID, firstMove);

        //5 possible moves on the board
        for(int i = 0; i < 5; i++) {
            Board boardAfterSecondMove = canMoveStoneCheck(tempBoard, playerID, i, forbiddenStates);
            if(boardAfterSecondMove != null){
                secondMoves[i] = true;
                secondMoves[VALIDITY_INDEX] = true; //setting the second move to valid

                forbiddenStates.add(boardAfterSecondMove); //once accepted adds to the list of unavailable ones to avoid duplicates
            }
        }
        return secondMoves;
    }

    private static Board canMoveStoneCheck(Board board, int playerID, int moveIndex, LinkedList<Board> forbiddenStates){
        Board tempBoard = board.clone();

        boolean legit_move = tempBoard.moveStoneForPlayer(playerID, convertIndexToMove(moveIndex));

        if(!forbiddenStates.contains(tempBoard) && legit_move)
            return tempBoard;
        else
            return null;
    }

    /**
     * Converts the index representation back to a valid move representation
     * 
     * <ul>
     * <li> <b> index -> move </b> </li>
     * <li> 0 -> 0 </li>
     * <li> 1 -> 1 </li>
     * <li> 2 -> -1 </li>
     * <li> 3 -> 2 </li>
     * <li> 4 -> -2 </li>
     * </ul>
     * 
     * 
     * @param index
     * @return The valid move representation
     */
    private static int convertIndexToMove(int index) {
        
        switch(index) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return -1;
            case 3:
                return 2;
            case 4:
                return -2;
            default:
                throw new IllegalArgumentException("The given index (" + index + ") has no move representation!");
        }
    }

    private static LinkedList<Move> convertToMoveList(boolean[][] moves, int player)
    {
        LinkedList<Move> moveList = new LinkedList<Move>();

        for(int i = 0; i < moves.length; i++) {
            if(!moves[i][VALIDITY_INDEX])
                continue; //to the next entry if move is invalid

            // -1 since we don't want to include the validity index
            for(int j = 0; j < moves[i].length-1; j++) {
                if(moves[i][j] == true) {
                    moveList.add(new Move(player, convertIndexToMove(i) , convertIndexToMove(j)));
                }
            }
        }

        return moveList;
    }


    /**
     * Forces a quick move. Will attempt to move stones inwards.
     * 
     * @return A quick legal move.
     */
    private Move forceMove() {

        Move move = new Move(playerID, 1000, 1000);

        Board tempBoard = gameState.getBoard().clone();

        for(int i = 0; i < Board.RINGS-1; i++) {
            if(gameState.getBoard().getStonesOnRingForPlayer(playerID, i) > 0) {
                move.first = i;
                tempBoard.moveStoneForPlayer(playerID, move.first);
            }
        }

        for(int i = 0; i < Board.RINGS-1; i++) {
            if(tempBoard.getStonesOnRingForPlayer(playerID, i) > 0) {
                move.second = i;
            }
        }

        return move;
    }
}
