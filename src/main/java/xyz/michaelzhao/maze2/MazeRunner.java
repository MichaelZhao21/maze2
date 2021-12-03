package xyz.michaelzhao.maze2;

import java.io.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MazeRunner {
    MazeNode[][] maze;
    int n, m;

    public void generateMaze(int n, int m) {
        this.n = n;
        this.m = m;
        maze = MazeGenerator.generate(n, m);
    }

    /**
     * Load the saved maze from a file
     *
     * @param file File object chosen from runner
     */
    public void loadMaze(File file) throws IOException {
        // Instantiate BufferedReader
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Get n, m, and p
        String[] line = reader.readLine().split(" ");
        n = Integer.parseInt(line[0]);
        m = Integer.parseInt(line[1]);

        // Instantiate maze matrix
        maze = new MazeNode[n][m];

        // Get each line
        for (int i = 0; i < n; i++) {
            String[] row = reader.readLine().split(" ");
            for (int j = 0; j < m; j++) {
                maze[i][j] = new MazeNode(row[j], j, i);
            }
        }

        // Close reader
        reader.close();
    }

    public void saveMaze(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(n + " " + m + "\n");
        for (MazeNode[] row : maze) {
            for (MazeNode cell : row) {
                writer.write(String.format("%d%d%d%d ", cell.north ? 1 : 0, cell.east ? 1 : 0, cell.south ? 1 : 0, cell.west ? 1 : 0));
            }
            writer.newLine();
        }

        // Close writer
        writer.close();
    }

    public void printMaze() {
    }

    public boolean solveMaze() {
        // Clear the parent attribute for all cells
        clearMaze();

        // Instantiate BFS queue
        LinkedList<MazeNode> queue = new LinkedList<>();

        // Set first element's parent to a dummy head node
        MazeNode headDummy = new MazeNode(-1, -1, 0);
        maze[0][0].parent = headDummy;
        queue.add(maze[0][0]);

        // BFS
        while (!queue.isEmpty()) {
            // Pop the next element in the queue
            MazeNode curr = queue.pop();

            // End if reached final cell
            if (curr.x == m - 1 && curr.y == n - 1) break;

            // Check all 4 edges the cell can reach
            if (curr.y > 0 && curr.north && maze[curr.y - 1][curr.x].parent == null) {
                maze[curr.y - 1][curr.x].parent = curr;
                queue.add(maze[curr.y - 1][curr.x]);
            }
            if (curr.x < m - 1 && curr.east && maze[curr.y][curr.x + 1].parent == null) {
                maze[curr.y][curr.x + 1].parent = curr;
                queue.add(maze[curr.y][curr.x + 1]);
            }
            if (curr.y < n - 1 && curr.south && maze[curr.y + 1][curr.x].parent == null) {
                maze[curr.y + 1][curr.x].parent = curr;
                queue.add(maze[curr.y + 1][curr.x]);
            }
            if (curr.x > 0 && curr.west && maze[curr.y][curr.x - 1].parent == null) {
                maze[curr.y][curr.x - 1].parent = curr;
                queue.add(maze[curr.y][curr.x - 1]);
            }
        }

        // If we do not reach the end, fail to find path
        if (maze[n - 1][m - 1].parent == null) return false;

        // Convert to matrix and return success
        parentToChildren(headDummy);
        return true;
    }

    public boolean solveMazeWithBreaks(int penalty) {
        // Clear the parent attribute for all cells
        clearMaze();

        // Instantiate BFS queue
        PriorityQueue<MazeNode> queue = new PriorityQueue<>(1, Comparator.comparingInt(MazeNode::getCost));

        // Set first element's parent to a dummy head node
        MazeNode headDummy = new MazeNode(-1, -1, 0);
        maze[0][0].parent = headDummy;
        maze[0][0].cost = 0;
        queue.add(maze[0][0]);

        // BFS
        while (!queue.isEmpty()) {
            // Pop the next element in the queue
            MazeNode curr = queue.poll();

            // Check all 4 edges the cell can reach
            if (curr.y > 0) {
                int cost = curr.cost + (curr.north ? 1 : penalty);
                MazeNode next = maze[curr.y - 1][curr.x];
                if (cost < next.cost) {
                    next.parent = curr;
                    next.cost = cost;
                    queue.add(next);
                }
            }
            if (curr.x < m - 1) {
                int cost = curr.cost + (curr.east ? 1 : penalty);
                MazeNode next = maze[curr.y][curr.x + 1];
                if (cost < next.cost) {
                    next.parent = curr;
                    next.cost = cost;
                    queue.add(next);
                }
            }
            if (curr.y < n - 1) {
                int cost = curr.cost + (curr.south ? 1 : penalty);
                MazeNode next = maze[curr.y + 1][curr.x];
                if (cost < next.cost) {
                    next.parent = curr;
                    next.cost = cost;
                    queue.add(next);
                }
            }
            if (curr.x > 0) {
                int cost = curr.cost + (curr.west ? 1 : penalty);
                MazeNode next = maze[curr.y][curr.x - 1];
                if (cost < next.cost) {
                    next.parent = curr;
                    next.cost = cost;
                    queue.add(next);
                }
            }
        }

        // If we do not reach the end, fail to find path
        if (maze[n - 1][m - 1].parent == null) return false;

        // Convert to matrix and return success
        parentToChildren(headDummy);
        return true;
    }

    public void setPenalty() {
    }

    private void clearMaze() {
        for (MazeNode[] row : maze) {
            for (MazeNode cell : row) {
                cell.parent = null;
                cell.cost = Integer.MAX_VALUE;
            }
        }
    }

    private void parentToChildren(MazeNode headDummy) {
        // Reverse nodes so path goes from start -> end through "child" attribute
        MazeNode temp = maze[n - 1][m - 1];
        while (!temp.parent.equals(headDummy)) {
            temp.parent.child = temp;
            temp = temp.parent;
        }

    }
}
