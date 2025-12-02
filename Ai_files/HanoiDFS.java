public class HanoiDFS {

    public static void main(String[] args) {
        System.out.println("Tower of Hanoi with DFS Strategy.");
        int n = 3; // Number of disks
        // Source, Auxiliary, and Destination pegs
        hanoi(n, 'A', 'C', 'B');
    }

    /**
     * Recursive function to solve the Tower of Hanoi problem.
     *
     * @param n       The number of disks to move.
     * @param source  The source peg.
     * @param destination The destination peg.
     * @param auxiliary The auxiliary peg.
     */
    public static void hanoi(int n, char source, char destination, char auxiliary) {
        if (n == 1) {
            System.out.println("Move disk 1 from peg " + source + " to peg " + destination);
            return;
        }
        // Move n-1 disks from source to auxiliary peg, using destination as auxiliary
        hanoi(n - 1, source, auxiliary, destination);

        // Move the nth disk from source to destination peg
        System.out.println("Move disk " + n + " from peg " + source + " to peg " + destination);

        // Move the n-1 disks from auxiliary to destination peg, using source as auxiliary
        hanoi(n - 1, auxiliary, destination, source);
    }
}
