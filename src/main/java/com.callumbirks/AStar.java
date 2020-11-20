package com.callumbirks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/*
    This class contains the A* algorithm. This algorithm has been written
    in a modular way so that with the Node class, it is entirely separable
    from the GUI portion of the application.
 */
public class AStar {
    // A 2D array of Node objects which represents the grid
    private final Node[][] grid;
    // A Node object which represents the start node
    private Node start = null;
    // A Node object which represents the end node
    private Node end = null;
    // A List of Node objects which represents the path found by the algorithm
    private List<Node> path = null;

    /*
        The primary and only constructor for the class, it takes the width and height of the
        desired grid as parameters. The constructor is simply used to initialise the grid.
     */
    public AStar(int width, int height) {
        // Create a new 2D Node array with 'width' columns and 'height' rows, and assign it to the grid variable
        grid = new Node[width][height];
        // Loop through each element of the grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Set each element on the grid to a new Node with the relevant co-ordinates as parameters
                grid[x][y] = new Node(x, y);
            }
        }
        // Loop through each element of the grid again
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Use the setNeighbours function to calculate and set the neighbours of each node in the grid
                grid[x][y].setNeighbours(width, height, grid);
            }
        }
    }

    /*
        Setter for the start node, takes the desired x and y co-ordinates for the start node as parameters
        and sets 'start' equal to the Node object at the given co-ordinates in the grid.
     */
    public void setStart(int x, int y) {
        start = grid[x][y];
    }

    // Getter for the start node
    public Node getStart() {
        return start;
    }

    /*
        Setter for the end node, takes the desired x and y co-ordinates for the end node as parameters
        and sets 'end' equal to the Node object at the given co-ordinates in the grid.
     */
    public void setEnd(int x, int y) {
        end = grid[x][y];
    }

    // Getter for the end node
    public Node getEnd() {
        return end;
    }

    // Getter for the path, if the path has not been found then this returns null
    public List<Node> getPath() {
        return path;
    }

    /*
        Setter for whether each node in the grid is a wall. Parameters are; the x and y co-ordinates
        for the relevant node, and a boolean determining whether this node is to be set as a wall
        (false means this node will be made to not be a wall, true means it will be made to be a wall)
     */
    public void setWall(int x, int y, boolean wall) {
        grid[x][y].setWall(wall);
    }

    // Getter for the wall boolean of a node at the given x and y co-ordinates of the grid
    public boolean isWall(int x, int y) {
        return grid[x][y].isWall();
    }

    // Check whether the node at the given x and y co-ords is the start node
    public boolean isStart(int x, int y) {
        return grid[x][y].equals(start);
    }

    // Check whether the node at the given x and y co-ords is the end node
    public boolean isEnd(int x, int y) {
        return grid[x][y].equals(end);
    }

    // Check if the path has been set
    public boolean isPathSet() {
        return path != null;
    }

    // Check whether the path has been set, and then if the node at the given x and y co-ords is on the path
    public boolean isOnPath(int x, int y) {
        return isPathSet() && path.contains(grid[x][y]);
    }

    // Check whether the node at the given x and y co-ordinates is within the bounds of the grid
    public boolean isInGrid(int x, int y) {
        return x < getGridWidth() && y < getGridHeight() && x >= 0 && y >= 0;
    }

    /*
        Loop through all of the nodes in the grid, resetting the values of f(n), g(n), h(n)
        and previous. The reason these values are reset is to ensure that the algorithm runs
        consistently every time.
    */
    private void resetValues() {
        // For each node in the grid
        for (int x = 0; x < getGridWidth(); x++) {
            for (int y = 0; y < getGridHeight(); y++) {
                // Reset f(n) to infinity
                grid[x][y].setF((int) Double.POSITIVE_INFINITY);
                // Reset g(n) to infinity
                grid[x][y].setG((int) Double.POSITIVE_INFINITY);
                // Reset and calculate h(n)
                grid[x][y].setH(AStar.calculateH(x,y,end));
                // Reset 'previous'
                grid[x][y].setPrevious(null);
            }
        }
    }

    // Calculate and return the width of the grid based on the number of columns
    private int getGridWidth() {
        return grid.length;
    }

    // Calculate and return the height of the grid based on the number of rows in the first column
    private int getGridHeight() {
        return grid[0].length;
    }


    /*
        This function runs the algorithm itself. It takes no parameters and returns nothing, as it
        relies entirely upon the values of this instance of the AStar class, therefore one must
        ensure that the start and end nodes are not null before calling this function. It is not
        necessary to check that grid is not null, as grid cannot be null if this class has been
        instantiated. If the optimal path is found by this algorithm, it will be stored in the
        'path' variable, which can be obtained using the 'getPath' function.
     */
    public void run() {
        // If the start node or end node are null, throw an IllegalArgumentException with the relevant message
        if(start == null)
            throw new IllegalArgumentException("Start node has not been set.");
        if(end == null)
            throw new IllegalArgumentException("End node has not been set");

        // Call the resetValues function to ensure that the algorithm will run consistently
        resetValues();

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
                // Run the reconstructPath function to reconstruct the path and assign the result to the 'path' variable
                path = reconstructPath(current);
                // Exit the function early as the optimal path has been located
                return;
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
            If the loop finishes this means that openSet is now empty and we have not been able to reach
            the end node from the start node and there are no more nodes left to be tested. This means
            there is no available path between the start node and end node. Therefore the path variable
            will be null
         */
        path = null;
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
