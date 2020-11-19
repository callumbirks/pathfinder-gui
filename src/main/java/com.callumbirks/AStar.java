package com.callumbirks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/*
    This class contains the A* algorithm. This algorithm has been written
    in a modular way so that with the Node class, it is entirely separable
    from the GUI portion of the application. Given the same grid, same start
    node and same end node, the algorithm will always give the same result.
 */
public class AStar {
    /*
        This is the only public function of the class, the one that runs the
        algorithm. It takes parameters of; a 2D Node array representing the grid,
        the start node, and the end node. It returns a Node list representing the
        optimal path.
     */
    public static List<Node> run(Node[][] grid, Node start, Node end) {
        /* Calculate the width of the grid based on the number of rows and
        assign it to a variable */
        final int width = grid.length;
        /* Calculate the height of the grid based on the number of columns in the
        first row (as all rows have the same number of columns) and assign it to a variable */
        final int height = grid[0].length;
        /*
            Loop through all of the nodes in the grid, resetting the values of neighbours, f(n),
            g(n), h(n) and previous. The reason these values are reset are because if this grid
            has already been put through the algorithm, the values could have been changed which
            could cause the algorithm to not run correctly. This ensures the algorithm runs as
            it should every time, regardless of if this grid has already been modified by a
            previous run of the algorithm.
         */
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].setNeighbours(width, height, grid);
                grid[x][y].setF((int) Double.POSITIVE_INFINITY);
                grid[x][y].setG((int) Double.POSITIVE_INFINITY);
                grid[x][y].setPrevious(null);
                grid[x][y].setH(calculateH(x,y,end));
            }
        }

        /*
            Set the g(n) value of the start node to 0, as g(n) represents the distance
            between the given node and the start node. And the distance between the start node
            and itself is 0.
         */
        start.setG(0);
        /*
            f(n) = g(n) + h(n) for a given node, and as g(n) of the start node is 0, f(n) = h(n),
            so here it calculates the h(n) value of the start node and assigns it to the f(n) value.
         */
        start.setF(calculateH(start.getX(), start.getY(), end));
        /*
            Create a variable to hold the openSet. The openSet is a list of the next nodes to be
            tested to find the optimal path. I am using a PriorityQueue for the openSet to improve
            efficiency. When retrieving a node from this PriorityQueue, the node with the lowest f(n)
            value is retrieved, therefore the node with the lowest f(n) value in the set will always
            be tested first. The f(n) value in this algorithm is given by g(n) + h(n), g(n) being the
            cost from the start node to the current node and h(n) being the euclidean distance from the
            current node to the end node.
         */
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getF));
        /*
            Add the start node to the open set as this is the first node in the path and we need
            to traverse through the grid from the start node to the end node in order to find
            the optimal path
         */
        openSet.add(start);

        // While there are still nodes to be tested
        while (!openSet.isEmpty()) {
            /* Get the top value of this queue, which in this case will be the node with the lowest f(n)
               and assign to current (the node currently being tested) */
            Node current = openSet.peek();
            // If this node is the end node
            if (current.equals(end)) {
                // Run the reconstructPath function to reconstruct the path
                return reconstructPath(current);
            }
            // Remove the current node from the openSet (It does not need to be tested again)
            openSet.remove(current);
            // For each of the current node's neighbours
            for (Node neighbour : current.getNeighbours()) {
                // If the neighbour is not a wall
                if(!neighbour.isWall()) {
                    // Increment the g(n) value by 1 as this will be the g(n) value of the neighbour
                    int tempG = current.getG() + 1;
                    // If the new g(n) value is lower than the current g(n) value of the neighbour
                    if (tempG < neighbour.getG()) {
                        // Set the previous node of the neighbour to be the current node
                        neighbour.setPrevious(current);
                        // Set the neighbour's g(n) value to the new g(n) value
                        neighbour.setG(tempG);
                        // Calculate the neighbour's f(n) value and assign to their f(n) value
                        neighbour.setF(tempG + neighbour.getH());
                        // If the neighbour is not already in the openSet
                        if (!openSet.contains(neighbour))
                            // Add them to the open set as we will need to test their neighbour's next
                            openSet.add(neighbour);
                    }
                }
            }
        }
        /*
            If we have not called reconstructPath and returned that value, and the loop has finished,
            this means that openSet is now empty. Therefore we have not yet reached the end node from
            the start node and there are no more nodes left to be tested. This means there is no
            available path between the start node and end node. Therefore we return the path as null to
            reflect this.
         */
        return null;
    }

    // Calculate the h(n) value for a node at a given co-ordinate and given the end node
    private static int calculateH(int x, int y, Node end) {
        /*
            Using Pythagoras, calculate the euclidean distance between the given node and the end node.
            Math.ceil rounds up, rounding is necessary as we are casting the result from a double to an
            integer which would otherwise cause truncation which is less precise than rounding.
         */
        return (int) Math.ceil(Math.sqrt(((end.getX() - x)*(end.getX()) - x) + ((end.getY() - y)*(end.getY() - y))));
    }

    /*
        Reconstruct the path from the current node (which would be the end node) back to the start node.
        The arguments passed in is a Node representing the current node.
        The value returned is the completed path (A list of nodes).
     */
    private static List<Node> reconstructPath(Node current) {
        // Create a local variable to hold the path, using an ArrayList
        List<Node> path = new ArrayList<>();
        // Add the current node to the path
        path.add(current);
        /*
            While the current node has a previous node (when we reach a node in the path without a
            previous node, this is the start node)
         */
        while(current.getPrevious() != null) {
            // Set the current node to be this node's previous node
            current = current.getPrevious();
            /*
                Add the current node to the start of the path (as we are working backwards,
                we add each node to the start so that the path will end up in the correct order).
             */
            path.add(0,current);
        }
        // Return the completed path
        return path;
    }
}
