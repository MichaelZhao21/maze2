package xyz.michaelzhao.maze2;

import java.util.*;

public class MazeGenerator {
    private final MazeNode[] root;
    private final int n;
    private final int m;

    public MazeGenerator(int n, int m) {
        // The root array will contain each "node" in the maze
        // A node (y, x) is represented by its index i in parent, where
        // y = i / m and x = i % m
        root = new MazeNode[n * m];

        // Instantiate all nodes with the parent index pointing to itself
        // Due to the initial disjoint set containing all sets of size 1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int index = i * m + j;
                root[index] = new MazeNode(j, i, index);
            }
        }

        this.m = m;
        this.n = n;
    }

    /**
     * Find with path compression (TODO: add path compression?)
     * @param x The current index of the node in the root array
     * @return The parent root node of the entire disjoint set
     */
    public int find(int x) {
        if (x == root[x].root)
            return x;
        root[x].root = find(root[x].root);
        return root[x].root;
    }

    /**
     * Creates a union between the sets a and b by making
     * the root of b the root of a as well if they are not in the same set
     * The
     * @param a First item to merge
     * @param d Offset from first item to second
     */
    public void union(int a, int d) {
        // Calculate second index relative to first index to determine the wall to break
        int b;
        if (d == 0) b = a - m;
        else if (d == 1) b = a + 1;
        else if (d == 2) b = a + m;
        else b = a - 1;

        // Break if a or b is out of the array
        if (a >= root.length || b >= root.length || a < 0 || b < 0) return;

        // Break if a and b are not connected
        if ((b % m == 0 && a % m == m - 1) || (a % m == 0 && b % m == m - 1)) return;

        // Calculate the roots of the 2 and compare; setting one root to the other
        int ra = find(a);
        int rb = find(b);
        if (ra != rb) {
            // Set root of a to b's root
            root[ra].root = rb;

            // BREAK THE WALL >:DDDD
            // NOTE: do this with a and b and NOT the roots of them
            if (d == 0) {
                root[a].north = true;
                root[b].south = true;
            }
            else if (d == 1) {
                root[a].east = true;
                root[b].west = true;
            }
            else if (d == 2) {
                root[a].south = true;
                root[b].north = true;
            }
            else {
                root[a].west = true;
                root[b].east = true;
            }
        }
    }

    /**
     * Gets all disjoint sets
     * @return an ArrayList of disjoint sets
     */
    public int count() {
        int num = 0;
        for (int i = 0; i < root.length; i++) {
            if (find(root[i].root) == i)
                num++;
        }
        return num;
    }

    @Override
    public String toString() {
        HashMap<Integer, ArrayList<MazeNode>> map = new HashMap<>();

        // Iterate through all root values
        for (MazeNode node : root) {
            // Upsert the node into the arraylist at the given root value in the HashMap
            ArrayList<MazeNode> temp;
            if (map.containsKey(node.root))
                temp = map.get(node.root);
            else
                temp = new ArrayList<>();
            temp.add(node);
            map.put(node.root, temp);
        }

        // Create the output string
        StringBuilder sb = new StringBuilder("Maze Gen Disjoint Set: ");
        for (Map.Entry<Integer, ArrayList<MazeNode>> entry : map.entrySet()) {
            sb.append(String.format("{%d}: ", entry.getKey()));
            sb.append(entry);
        }

        return sb.toString();
    }

    /**
     * Generate maze using an implementation of the Disjoint Set Union Find data structure
     * @param n the number of rows in the maze
     * @param m the number of cols in the maze
     * @return the matrix that represents the n x m maze, made up of MazeNode objects
     */
    public static MazeNode[][] generate(int n, int m) {
        // Instantiate Union find class
        MazeGenerator mazeGenerator = new MazeGenerator(n, m);

        // Instantiate random object
        Random rand = new Random();

        // Tracker for number of disjoint sets remaining
        int count = n * m;

        // Break condition if random generation doesn't work
        int loopExit = 0;

        while (count != 1 && loopExit++ < 100) {
            // The randomWalls array list will contain a list of pairs of random
            // numbers within the disjoint set. This may or may not form a new
            // path within the maze, but it provides a random set of walls to knock down
            LinkedList<Pair<Integer>> randomWalls = breakRandomWalls(n, m, rand);

            // Start breaking down walls >:)))
            for (Pair<Integer> pair : randomWalls)
                mazeGenerator.union(pair.first, pair.second);

            // Recount
            count = mazeGenerator.count();
        }

        // Create maze matrix
        MazeNode[][] output = new MazeNode[n][m];
        for (int i = 0; i < mazeGenerator.root.length; i++)
            output[i / m][i % m] = mazeGenerator.root[i];

        return output;
    }

    public static LinkedList<Pair<Integer>> breakRandomWalls(int n, int m, Random rand) {
        LinkedList<Pair<Integer>> randomWalls = new LinkedList<>();

        // Create the set of random walls. This will be size n * m and can
        // union any of the two cells together for now.
        for (int i = 0; i < n * m; i++) {
            // Get a random cell and choose one of the 4 directions randomly
            int index = rand.nextInt(n * m);
            int d = rand.nextInt(4);

            // Add this pair to the randomWalls ArrayList
            randomWalls.add(new Pair<>(index, d));
        }

        return randomWalls;
    }
}
