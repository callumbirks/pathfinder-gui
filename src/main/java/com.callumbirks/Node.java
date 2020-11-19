package com.callumbirks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
    The Node class is used to represent each node of the A* algorithm.
    Each node has; an x and a y co-ordinate on the grid, f, g and h values
    for the algorithm, a variable which stores its previous node along the
    path, a list of its neighbours, and a boolean determining whether it is
    a wall.
 */
public class Node {
    /*
       The x and y co-ordinates are final because
       once they have been set they will not need
       to be changed, the final property just ensures
       they can not be changed.
    */
    // The node's x co-ordinate on the grid
    private final int x;
    // The node's y co-ordinate on the grid
    private final int y;
    // The node's f(n) value
    private int f;
    // The node's g(n) value
    private int g;
    // The node's h(n) value
    private int h;
    // The previous node to this one along the path
    private Node previous = null;
    /*
        Suppress warnings by the compiler telling us that the neighbours field
        may be final, as it will in fact be modified when the algorithm is run
     */
    @SuppressWarnings("FieldMayBeFinal")
    // A list of this node's neighbours
    private List<Node> neighbours;
    // A boolean determining if this node is a wall
    private boolean wall;

    /* The constructor for the node, taking in parameters for
       its x and y co-ordinates on the grid. */
    public Node(int x, int y) {
        // Set the x co-ordinate of this node to the passed in x value
        this.x = x;
        // Set the y co-ordinate of this node to the passed in y value
        this.y = y;
        /*
            The reason for setting f(n), g(n) and h(n) to infinity by
            default is because these values are compared against the
            same values of other nodes to find the minimum, and any
            other value will always be lower than infinity.
        */
        // Set the f(n) value of this node to Infinity
        this.f = (int) Double.POSITIVE_INFINITY;
        // Set the g(n) value of this node to Infinity
        this.g = (int) Double.POSITIVE_INFINITY;
        // Set the h(n) value of this node to Infinity
        this.h = (int) Double.POSITIVE_INFINITY;
        /*
            Initialise the neighbours list as a new ArrayList with an initial
            capacity of 4. The initial capacity is set to 4 to save on some
            memory, as the default initial capacity of an ArrayList is 10
            but each node will only ever have a maximum of 4 neighbours
         */
        this.neighbours = new ArrayList<>(4);
        // Set the wall boolean to be false (each node is not a wall by default)
        this.wall = false;
    }

    // Getter for the x co-ordinate of this node
    public int getX() {
        return x;
    }

    // Getter for the y co-ordinate of this node
    public int getY() {
        return y;
    }

    // Getter for the f(n) value of this node
    public int getF() {
        return f;
    }

    // Setter for the f(n) value of this node
    public void setF(int f) {
        this.f = f;
    }

    // Getter for the g(n) value of this node
    public int getG() {
        return g;
    }

    // Setter for the g(n) value of this node
    public void setG(int g) {
        this.g = g;
    }

    // Getter for the h(n) value of this node
    public int getH() {
        return h;
    }

    // Setter for the h(n) value of this node
    public void setH(int h) {
        this.h = h;
    }

    // Getter for the wall boolean of this node
    public boolean isWall() {
        return wall;
    }

    // Setter for the wall boolean of this node
    public void setWall(boolean wall) {
        this.wall = wall;
    }

    // Get the value representing the previous node along the path
    public Node getPrevious() {
        return previous;
    }

    // Set the previous node to this node along the path
    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    /*
        Calculate and set the neighbours of this node.
        Neighbours that are within bounds of the grid will be added,
        and ones that are not will be ignored.
        The parameters are the width and height of the grid, and the
        grid itself.
     */
    public void setNeighbours(int width, int height, Node[][] grid) {
        /* Check if this node is at the very top of the grid, if it
           isn't then add the node above this one as a neighbour */
        if (y > 0) neighbours.add(grid[x][y - 1]);
        /* Check if this node is at the very right of the grid, if it
           isn't then add the node to the right of this one as a neighbour */
        if (x < width - 1) neighbours.add(grid[x + 1][y]);
        /* Check if this node is at the very bottom of the grid, if it
           isn't then add the node below this one as a neighbour */
        if (y < height - 1) neighbours.add(grid[x][y + 1]);
        /* Check if this node is at the very left of the grid, if it
           isn't then add the node to the left of this one as a neighbour */
        if (x > 0) neighbours.add(grid[x - 1][y]);
    }

    // Getter for the list of this node's neighbours
    public List<Node> getNeighbours() {
        return neighbours;
    }

    /* Equals function for this class, checks all variables against
       each other to see if this node and the given node are exactly
       equal */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x &&
                y == node.y &&
                f == node.f &&
                g == node.g &&
                h == node.h &&
                Objects.equals(previous, node.previous) &&
                Objects.equals(neighbours, node.neighbours) &&
                Objects.equals(wall, node.wall);
    }

    // hashCode function must be overridden in all classes that override equals.
    @Override
    public int hashCode() {
        return Objects.hash(x, y, f, g, h, previous, neighbours);
    }
}
