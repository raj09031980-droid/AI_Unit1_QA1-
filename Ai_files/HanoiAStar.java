import java.util.*;

public class HanoiAStar {

    /**
     * Represents a single state in the Tower of Hanoi puzzle.
     */
    static class State implements Comparable<State> {
        // pegs = peg A (index 0), pegs = peg B (index 1), pegs = peg C (index 2)
        // Each list stores disks, with the smallest at the top (end of list).
        List<Integer>[] pegs;
        int g_cost; // Cost from start (number of moves)
        int h_cost; // Heuristic estimate to goal
        int f_cost; // g_cost + h_cost
        State parent; // To reconstruct the path
        // Variables to describe the move that led to this state
        int moveFromPeg = -1;
        int moveToPeg = -1;
        int movedDisk = -1;
        int numDisks;
        int goalPegIndex;

        // Constructor for the initial state
        @SuppressWarnings("unchecked")
        public State(int numDisks, int goalPegIndex) {
            this.numDisks = numDisks;
            this.goalPegIndex = goalPegIndex;
            pegs = new List[3];
            for (int i = 0; i < 3; i++) {
                pegs[i] = new ArrayList<>();
            }
            // Start with all disks on peg A (index 0), largest on bottom
            for (int i = numDisks; i >= 1; i--) {
                pegs[0].add(i);
            }
            this.g_cost = 0;
            this.h_cost = calculateHeuristic();
            this.f_cost = this.g_cost + this.h_cost;
            this.parent = null;
        }

        // Constructor for subsequent states
        @SuppressWarnings("unchecked")
        public State(State previousState, int fromPeg, int toPeg) {
            this.numDisks = previousState.numDisks;
            this.goalPegIndex = previousState.goalPegIndex;
            pegs = new List[3];
            for (int i = 0; i < 3; i++) {
                pegs[i] = new ArrayList<>(previousState.pegs[i]);
            }
            this.movedDisk = pegs[fromPeg].remove(pegs[fromPeg].size() - 1);
            pegs[toPeg].add(this.movedDisk);
            this.moveFromPeg = fromPeg;
            this.moveToPeg = toPeg;
            this.g_cost = previousState.g_cost + 1;
            this.h_cost = calculateHeuristic();
            this.f_cost = this.g_cost + this.h_cost;
            this.parent = previousState;
        }

        // Heuristic function: number of disks not on the destination peg
        private int calculateHeuristic() {
            int count = 0;
            for (int i = 1; i <= numDisks; i++) {
                boolean onGoalPeg = false;
                for (int disk : pegs[goalPegIndex]) {
                    if (disk == i) {
                        onGoalPeg = true;
                        break;
                    }
                }
                if (!onGoalPeg) {
                    count++;
                }
            }
            return count;
        }

        // Check if the current state is the goal state (all disks on peg C)
        public boolean isGoalState() {
            return pegs[goalPegIndex].size() == numDisks;
        }

        // Compare by f_cost, then h_cost for ties
        @Override
        public int compareTo(State other) {
            return Integer.compare(this.f_cost, other.f_cost);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.deepEquals(pegs, state.pegs);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(pegs);
        }
    }

    public static void solve(int numDisks, int goalPegIndex) {
        PriorityQueue<State> openSet = new PriorityQueue<>();
        Set<State> closedSet = new HashSet<>();

        State initialState = new State(numDisks, goalPegIndex);
        openSet.add(initialState);

        while (!openSet.isEmpty()) {
            State currentState = openSet.poll();

            if (currentState.isGoalState()) {
                printSolution(currentState);
                return;
            }

            closedSet.add(currentState);

            // Generate possible next states (moves)
            for (int fromPeg = 0; fromPeg < 3; fromPeg++) {
                for (int toPeg = 0; toPeg < 3; toPeg++) {
                    if (fromPeg == toPeg) continue;

                    List<Integer> source = currentState.pegs[fromPeg];
                    List<Integer> destination = currentState.pegs[toPeg];

                    // Check if move is legal (source not empty and top disk constraint)
                    if (!source.isEmpty() && (destination.isEmpty() || source.get(source.size() - 1) < destination.get(destination.size() - 1))) {
                        State nextState = new State(currentState, fromPeg, toPeg);

                        if (closedSet.contains(nextState)) continue;

                        // Check if better path to an existing state in openSet
                        boolean inOpenSet = false;
                        for (State openState : openSet) {
                            if (openState.equals(nextState) && openState.g_cost <= nextState.g_cost) {
                                inOpenSet = true;
                                break;
                            }
                        }

                        if (!inOpenSet) {
                            openSet.add(nextState);
                        }
                    }
                }
            }
        }
        System.out.println("No solution found.");
    }

    private static void printSolution(State goalState) {
        List<State> path = new ArrayList<>();
        State current = goalState;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);

        System.out.println("\nSolution found in " + goalState.g_cost + " moves (Optimal path length).");
        System.out.println("Move-by-move output:");

        char[] pegNames = {'A', 'B', 'C'};

        for (int i = 0; i < path.size(); i++) {
            State s = path.get(i);
            if (i == 0) {
                System.out.printf("Stage %d (Initial): State A:%s B:%s C:%s\n",
                        i, s.pegs[0], s.pegs[1], s.pegs[2]);
            } else {
                System.out.printf("Stage %d (Move disk %d from %c to %c): State A:%s B:%s C:%s\n",
                        i, s.movedDisk, pegNames[s.moveFromPeg], pegNames[s.moveToPeg],
                        s.pegs[0], s.pegs[1], s.pegs[2]);
            }
        }
    }


    public static void main(String[] args) {
        // --- TITLE ---
        System.out.println("Tower of Hanoi Using A* Strategy.");
        // --- Heuristics Mentioned ---
        System.out.println("Heuristic Used: The number of disks that are currently *not* on the target (destination) peg.");
        System.out.println("This is an admissible heuristic (never overestimates the cost).");
        System.out.println("-----------------------------------------------------------------");
        // ----------------------------
        int numberOfDisks = 3;
        // 0 for A, 1 for B, 2 for C. Goal is peg C (index 2).
        int goalPeg = 2;

        System.out.println("Solving for " + numberOfDisks + " discs to Peg C using A* search:");
        solve(numberOfDisks, goalPeg);
    }
}
