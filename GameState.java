import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private int size;            // The number of stones
    private boolean[] stones;    // Game state: true for available stones, false for taken ones
    private int lastMove;        // The last move
    public static final double MAX_WIN_VALUE = 1.0;
    public static final double MIN_WIN_VALUE = -1.0;

    /**
     * Class constructor specifying the number of stones.
     */
    public GameState(int size) {

        this.size = size;

        //  For convenience, we use 1-based index, and set 0 to be unavailable
        this.stones = new boolean[this.size + 1];
        this.stones[0] = false;

        // Set default state of stones to available
        for (int i = 1; i <= this.size; ++i) {
            this.stones[i] = true;
        }

        // Set the last move be -1
        this.lastMove = -1;
    }

    /**
     * Copy constructor
     */
    public GameState(GameState other) {
        this.size = other.size;
        this.stones = Arrays.copyOf(other.stones, other.stones.length);
        this.lastMove = other.lastMove;
    }


    /**
     * This method is used to compute a list of legal moves
     *
     * @return This is the list of state's moves
     */
    public List<Integer> getMoves() {
        List<Integer> moves = new ArrayList<>();

        // if this is the first move, return list of odd numbers strictly less than n/2
        if (lastMove == -1) {
            int iter = (size % 2 == 0) ? (size / 2) - 1 : (size / 2);
            for (int i = 1; i <= iter; i++) {
                if (i % 2 == 1) {
                    moves.add(i);
                }
            }
        } else {
            // this is a subsequent move, check for its multiples/factors that are available to take

            // checking for factors
            for (int i = 1; i * i <= lastMove; i++) {
                if (lastMove % i == 0) {
                    if (getStone(i)) {
                        moves.add(i);
                    }
                    if (i != (lastMove / i)) {
                        if (getStone(lastMove / i)) {
                            moves.add(lastMove / i);
                        }
                    }
                }
            }

            // checking for multiples
            int multiple = 2 * lastMove;
            while (multiple <= size) {
                if (getStone(multiple)) {
                    moves.add(multiple);
                }
                multiple += lastMove;
            }
        }

        // return successors in increasing order
        Collections.sort(moves);
        return moves;
    }


    /**
     * This method is used to generate a list of successors
     * using the getMoves() method
     *
     * @return This is the list of state's successors
     */
    public List<GameState> getSuccessors() {
        return this.getMoves().stream().map(move -> {
            var state = new GameState(this);
            state.removeStone(move);
            return state;
        }).collect(Collectors.toList());
    }


    /**
     * This method is used to evaluate a game state based on
     * the given heuristic function
     *
     * @return double This is the static score of given state
     */
    public double evaluate() {
        if (!getPlayerTurn()) {
            // MIN's turn
            if (getMoves().isEmpty()) {
                // MAX's win
                return MAX_WIN_VALUE;
            } else {
                // stone 1 not taken, return 0
                if (getStone(1)) {
                    return 0;
                } else if (lastMove == 1) {
                    // if size of successors is odd, return -0.5 else 0.5
                    if (getMoves().size() % 2 == 1) {
                        return -0.5;
                    } else {
                        return 0.5;
                    }
                } else if (Helper.isPrime(lastMove)) {
                    int countOfPrimesMultiple = 0;
                    // find the count of the primes multiple in successors
                    for (Integer m : getMoves()) {
                        if (m % lastMove == 0) {
                            countOfPrimesMultiple++;
                        }
                    }
                    // if count is odd, return -0.7, else 0.7
                    if (countOfPrimesMultiple % 2 == 1) {
                        return -0.7;
                    } else {
                        return 0.7;
                    }
                } else if (!Helper.isPrime(lastMove)) {
                    // lastMove is composite number
                    int largestPrimeFactor = Helper.getLargestPrimeFactor(lastMove);
                    int countOfLargestPrimeFactorMultiples = 0;
                    for (Integer m : getMoves()) {
                        if (m % largestPrimeFactor == 0) {
                            countOfLargestPrimeFactorMultiples++;
                        }
                    }

                    // return -0.6 if number of multiples of largestPrimeFactor in successors is odd, else 0.6
                    if (countOfLargestPrimeFactorMultiples % 2 == 1) {
                        return -0.6;
                    } else {
                        return 0.6;
                    }
                }
            }
        } else {
            // MAX's turn
            if (getMoves().isEmpty()) {
                // MIN's win
                return MIN_WIN_VALUE;
            } else {
                // stone 1 not taken, return 0
                if (getStone(1)) {
                    return 0;
                } else if (lastMove == 1) {
                    // if size of successors is odd, return 0.5 else -0.5
                    if (getMoves().size() % 2 == 1) {
                        return 0.5;
                    } else {
                        return -0.5;
                    }
                } else if (Helper.isPrime(lastMove)) {
                    int countOfPrimesMultiple = 0;
                    // find the count of the primes multiple in successors
                    for (Integer m : getMoves()) {
                        if (m % lastMove == 0) {
                            countOfPrimesMultiple++;
                        }
                    }
                    // if count is odd, return 0.7, else -0.7
                    if (countOfPrimesMultiple % 2 == 1) {
                        return 0.7;
                    } else {
                        return -0.7;
                    }
                } else if (!Helper.isPrime(lastMove)) {
                    // lastMove is composite number
                    int largestPrimeFactor = Helper.getLargestPrimeFactor(lastMove);
                    int countOfLargestPrimeFactorMultiples = 0;
                    for (Integer m : getMoves()) {
                        if (m % largestPrimeFactor == 0) {
                            countOfLargestPrimeFactorMultiples++;
                        }
                    }

                    // return 0.6 if number of multiples of largestPrimeFactor in successors is odd, else -0.6
                    if (countOfLargestPrimeFactorMultiples % 2 == 1) {
                        return 0.6;
                    } else {
                        return -0.6;
                    }
                }
            }
        }

        // should not come here
        return 0.0;
    }

    /**
     * This method is used to take a stone out
     *
     * @param idx Index of the taken stone
     */
    public void removeStone(int idx) {
        this.stones[idx] = false;
        this.lastMove = idx;
    }

    /**
     * These are get/set methods for a stone
     *
     * @param idx Index of the taken stone
     */
    public void setStone(int idx) {
        this.stones[idx] = true;
    }

    public boolean getStone(int idx) {
        return this.stones[idx];
    }

    /**
     * These are get/set methods for lastMove variable
     *
     * @param move Index of the taken stone
     */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    public int getLastMove() {
        return this.lastMove;
    }

    /**
     * This is get method for game size
     *
     * @return int the number of stones
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns whose turn is it
     *
     * @return true if MAX's turn else false
     */
    boolean getPlayerTurn() {
        int numberOfStonesTaken = 0;
        for (int i = 1; i <= size; i++) {
            if (!getStone(i)) {
                numberOfStonesTaken++;
            }
        }

        // if number of stones taken is even, it is MAX's turn
        return (numberOfStonesTaken % 2 == 0);
    }

    /**
     * Prints the stones taken already
     */
    public void printStonesTaken() {
        System.out.print("Stones taken<");
        for (int i = 1; i <= size; i++) {
            System.out.print(stones[i] ? "" : i + " ");
        }
        System.out.print(">\n");
    }
}
