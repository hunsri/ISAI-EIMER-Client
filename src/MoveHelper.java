import java.util.HashMap;

public class MoveHelper {

    // public static HashMap<Integer, Integer> INVERSE = obtainInverse();

    // public static HashMap<Integer, Integer>  obtainInverse() {
    //     HashMap<Integer, Integer> inverse = new HashMap<Integer, Integer>();
        
    //     inverse.put(0, -1);
    //     inverse.put(-1, 0);
    //     inverse.put(1, -2);
    //     inverse.put(-2, 1);

    //     return inverse;
    // }

    /**
     * Returns the inverse of a given move.
     * <p>
     * The mapping is as follows:
     * <ul>
     *   <li>0 &rarr; -1</li>
     *   <li>-1 &rarr; 0</li>
     *   <li>1 &rarr; -2</li>
     *   <li>-2 &rarr; 1</li>
     *   <li>2 &rarr; 1000 (indicates that 2 has no valid inverse)</li>
     * </ul>
     * For any other input, an IllegalArgumentException is thrown.
     *
     * @param move the move to invert
     * @return the inverse of the given move, or 1000 if the move is 2 (has no inverse)
     * @throws IllegalArgumentException if the move is not one of the handled cases
     */
    public static int inverseOf(int move) {
        switch (move) {
            case 0:
                return -1;
            case -1:
                return 0;
            case 1:
                return -2;
            case -2:
                return 1;
            case 2:
                return 1000; // returning a non existing move, since 2 has no inverse
            default:
                throw new IllegalArgumentException();
        }
    }
}
