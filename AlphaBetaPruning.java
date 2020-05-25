public class AlphaBetaPruning {

    private GameState root;
    private int depth;

    // stats to be printed
    private Choice bestChoice = null;
    private int numberOfNodesVisited = 0;
    private int numberOfNodesEvaluated = 0;
    private int maxDepthReached = 0;
    private int numberOfTimesInternalNodeExpanded = 0;

    public AlphaBetaPruning() {
    }

    /**
     * This function will print out the information to the terminal,
     * as specified in the homework description.
     */
    public void printStats() {
        System.out.println("Move: " + this.bestChoice.move);
        System.out.println("Value: " + this.bestChoice.value);
        System.out.println("Number of Nodes Visited: " + this.numberOfNodesVisited);
        System.out.println("Number of Nodes Evaluated: " + this.numberOfNodesEvaluated);
        System.out.println("Max Depth Reached: " + this.maxDepthReached);

        // number of internal nodes = numberOfNodesVisited - numberOfNodesEvaluated
        // branching factor = numberOfTimesInternalNodeExpanded / (number of internal nodes)
        int numberOfInternalNodes = this.numberOfNodesVisited - this.numberOfNodesEvaluated;
        if (numberOfInternalNodes != 0) {
            System.out.format("Avg Effective Branching Factor: %.1f\n", ((double) this.numberOfTimesInternalNodeExpanded) / numberOfInternalNodes);
        } else {
            System.out.println("Avg Effective Branching Factor: 0");
        }
    }

    /**
     * This function will start the alpha-beta search
     *
     * @param state This is the current game state
     * @param depth This is the specified search depth
     */
    public void run(GameState state, int depth) {
        this.root = state;
        this.depth = depth;

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        this.bestChoice = alphabeta(state, depth, alpha, beta, state.getPlayerTurn());
    }

    /**
     * This method is used to implement alpha-beta pruning for both 2 players
     *
     * @param state     This is the current game state
     * @param depth     Current depth of search
     * @param alpha     Current Alpha value
     * @param beta      Current Beta value
     * @param maxPlayer True if player is Max Player; Otherwise, false
     * @return Choice with best move and the value associated with the move
     */
    private Choice alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer) {
        // update max depth searched and number of nodes visited
        this.maxDepthReached = Math.max(this.maxDepthReached, this.depth - depth);
        this.numberOfNodesVisited++;

        // if depth to which game needs to be played has reached or we have reached an end game state
        if (depth == 0 || state.getMoves().isEmpty()) {
            this.numberOfNodesEvaluated++;
            double eval = state.evaluate();
            return new Choice(eval, -1); // -1 move denotes terminal state, no move
        } else {
            // if MAX's turn
            if (maxPlayer) {
                double currentValue = Double.NEGATIVE_INFINITY;
                double currentAlpha = alpha;
                double currentBeta = beta;
                int bestMove = -1;
                for (GameState gameState : state.getSuccessors()) {
                    GameState nextState = new GameState(gameState);
                    Choice currentChoice = alphabeta(nextState, depth - 1, currentAlpha, currentBeta, !maxPlayer);
                    if (currentChoice.value > currentValue) {
                        currentValue = currentChoice.value;
                        bestMove = nextState.getLastMove();
                    } else if (currentChoice.value == currentValue) {
                        // pick smaller numbered stone if value is same
                        currentValue = currentChoice.value;
                        if (bestMove == -1) {
                            // this is the first child returning, should not reach here as this will be handled with initial
                            // value of currentValue = Double.NEGATIVE_INFINITY, but still putting it here
                            bestMove = nextState.getLastMove();
                        } else {
                            bestMove = Math.min(bestMove, nextState.getLastMove());
                        }
                    }

                    // increment numberOfTimesInternalNodeExpanded
                    this.numberOfTimesInternalNodeExpanded++;

                    if (currentValue >= currentBeta) {
                        return new Choice(currentValue, bestMove);
                    }

                    currentAlpha = Math.max(currentValue, currentAlpha);
                }
                return new Choice(currentValue, bestMove);
            } else {
                // MIN's turn
                double currentValue = Double.POSITIVE_INFINITY;
                double currentAlpha = alpha;
                double currentBeta = beta;
                int bestMove = -1;
                for (GameState gameState : state.getSuccessors()) {
                    GameState nextState = new GameState(gameState);
                    Choice currentChoice = alphabeta(nextState, depth - 1, currentAlpha, currentBeta, !maxPlayer);
                    if (currentChoice.value < currentValue) {
                        currentValue = currentChoice.value;
                        bestMove = nextState.getLastMove();
                    } else if (currentChoice.value == currentValue) {
                        // pick smaller numbered move if value is same
                        if (bestMove == -1) {
                            // this is the first child returning, should not reach here as this will be handled with initial
                            // value of currentValue = Double.POSITIVE_INFINITY, but still putting it here
                            bestMove = nextState.getLastMove();
                        } else {
                            bestMove = Math.min(bestMove, nextState.getLastMove());
                        }
                    }

                    // increment numberOfTimesInternalNodeExpanded
                    this.numberOfTimesInternalNodeExpanded++;

                    if (currentValue <= currentAlpha) {
                        return new Choice(currentValue, bestMove);
                    }

                    currentBeta = Math.min(currentValue, currentBeta);
                }
                return new Choice(currentValue, bestMove);
            }
        }
    }

    /**
     * Helper class which stores the best move and the corresponding value of the move
     */
    class Choice {
        double value;
        int move;

        public Choice(double value, int move) {
            this.value = value;
            this.move = move;
        }
    }
}
