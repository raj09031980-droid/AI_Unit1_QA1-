import java.util.*;
public class HanoiBFS {
    /** Representation of a state: disks on 3 pegs */
    public static class State {
        List<Stack<Integer>> pegs;

        public State(List<Stack<Integer>> pegs) {
            this.pegs = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                Stack<Integer> s = new Stack<>();
                s.addAll(pegs.get(i));
                this.pegs.add(s);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof State)) return false;
            State other = (State) o;
            for (int i = 0; i < 3; i++) {
                if (!this.pegs.get(i).equals(other.pegs.get(i))) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return pegs.get(0).hashCode()
                 ^ pegs.get(1).hashCode()
                 ^ pegs.get(2).hashCode();
        }

        public boolean isGoal(int totalDisks) {
            Stack<Integer> dest = pegs.get(2);
            if (dest.size() != totalDisks) return false;
            int expected = totalDisks;
            for (int d : dest) {
                if (d != expected--) return false;
            }
            return true;
        }

        public List<MoveState> getNextStates() {
            List<MoveState> next = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Stack<Integer> fromPeg = pegs.get(i);
                if (fromPeg.isEmpty()) continue;
                int disk = fromPeg.peek();
                for (int j = 0; j < 3; j++) {
                    if (i == j) continue;
                    Stack<Integer> toPeg = pegs.get(j);
                    if (toPeg.isEmpty() || toPeg.peek() > disk) {
                        List<Stack<Integer>> newPegs = new ArrayList<>();
                        for (int k = 0; k < 3; k++) {
                            newPegs.add((Stack<Integer>) pegs.get(k).clone());
                        }
                        newPegs.get(i).pop();
                        newPegs.get(j).push(disk);
                        State newState = new State(newPegs);
                        next.add(new MoveState(newState, i, j, disk));
                    }
                }
            }
            return next;
        }
    }

    /** Represents a move leading to a new state */
    public static class MoveState {
        public State state;
        public int fromPeg, toPeg, disk;
        public MoveState(State s, int fromPeg, int toPeg, int disk) {
            this.state = s;
            this.fromPeg = fromPeg;
            this.toPeg = toPeg;
            this.disk = disk;
        }
    }

    /** BFS to find shortest sequence of moves from start → goal */
    public static List<MoveState> bfsSolve(State start, int totalDisks) {
        Queue<List<MoveState>> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        queue.add(new ArrayList<>());
        visited.add(start);

        while (!queue.isEmpty()) {
            List<MoveState> path = queue.poll();
            State cur = (path.isEmpty() ? start : path.get(path.size() - 1).state);

            if (cur.isGoal(totalDisks)) {
                return path;
            }

            for (MoveState ms : cur.getNextStates()) {
                if (!visited.contains(ms.state)) {
                    visited.add(ms.state);
                    List<MoveState> newPath = new ArrayList<>(path);
                    newPath.add(ms);
                    queue.add(newPath);
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println("=== Tower of Hanoi Problem Using BFS Strategy ===");

        final int N = 3;  // number of disks

        List<Stack<Integer>> pegs = new ArrayList<>();
        Stack<Integer> s0 = new Stack<>();
        for (int d = N; d >= 1; d--) {
            s0.push(d);
        }
        pegs.add(s0);
        pegs.add(new Stack<>());
        pegs.add(new Stack<>());

        State start = new State(pegs);

        List<MoveState> solution = bfsSolve(start, N);
        if (solution != null) {
            for (MoveState ms : solution) {
                System.out.printf("Move disk %d from peg %d to peg %d%n",
                                  ms.disk, ms.fromPeg, ms.toPeg);
            }
            System.out.printf("Solved in %d moves.%n", solution.size());
        } else {
            System.out.println("No solution found.");
        }
    }
}
