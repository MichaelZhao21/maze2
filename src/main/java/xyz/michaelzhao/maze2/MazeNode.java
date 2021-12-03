package xyz.michaelzhao.maze2;

public class MazeNode implements Comparable<MazeNode> {
    public boolean north, east, south, west;
    public MazeNode parent, child;
    public long distance;
    public int x, y, root, cost;

    /**
     * Node used for maze generation in a disjoint set union find structure
     *
     * @param x    the x coordinate in the maze grid
     * @param y    the y coordinate in the maze grid
     * @param root the root of the disjoint set tree
     */
    public MazeNode(int x, int y, int root) {
        this.x = x;
        this.y = y;
        this.root = root;
        north = false;
        east = false;
        south = false;
        west = false;
        parent = null;
        child = null;
        distance = 0;
        cost = Integer.MAX_VALUE;
    }

    public MazeNode() {
        north = false;
        east = false;
        south = false;
        west = false;
        parent = null;
        child = null;
        distance = 0;
        cost = Integer.MAX_VALUE;
    }

    public MazeNode(String nodeString, int x, int y) throws IllegalArgumentException {
        if (nodeString.length() != 4)
            throw new IllegalArgumentException("Node strings should be of the format xxxx where x = {0, 1}");

        // This is defined as a set of 4 boolean values, where a 0 represents a wall and a 1 represents an open space
        char[] input = nodeString.toCharArray();
        this.north = input[0] != '0';
        this.east = input[1] != '0';
        this.south = input[2] != '0';
        this.west = input[3] != '0';
        this.x = x;
        this.y = y;
        parent = null;
        child = null;
        distance = 0;
        cost = Integer.MAX_VALUE;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public int compareTo(MazeNode o) {
        // Compare row value first then column value
        if (y == o.y)
            return x - o.x;
        return y - o.y;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) -> %d%d%d%d", x, y, north ? 1 : 0, east ? 1 : 0, south ? 1 : 0, west ? 1 : 0);
    }
}
