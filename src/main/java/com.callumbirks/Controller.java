package com.callumbirks;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/*
    The controller class is used to control GUI elements of the application
 */
public class Controller implements Initializable {
    /*
        A variable used to refer to the canvas (The part of the GUI which holds the grid in this application).
        The FXML tag is needed so that the compiler knows this is an FXML element (this variable refers to
        the canvas which can be found in the FXML file).
     */
    @FXML
    private Canvas canvas;
    // A variable used to control graphics elements of the canvas, such as drawing and clearing
    private GraphicsContext gc;
    /*
        A constant variable which determines the size of each square on the grid.
        The number of elements in the grid will dynamically change based on this.
     */
    public static final int PIXEL_SIZE = 10;

    // The variable used to store the width of the grid (number of columns)
    public static int WIDTH;
    // The variable used to store the height of the grid (number of rows)
    public static int HEIGHT;
    // The variable used to store the start node
//    private Node start;
//    // The variable used to store the end node
//    private Node end;
//    // The variable used to store the completed path once the algorithm has been run
//    private List<Node> path;
    private AStar aStar;
    /*
        The variable used to store what the last button pressed by the user was.
        I have used this because otherwise it causes issues with walls being drawn
        when the user is trying to place the start node, and other similar issues.
     */
    private String currentBtn = "";

    /*
        This method is overridden from the Initializable interface, it is similar
        to a constructor in that it will run when the Controller is first initialized
        (in this case when the program is run).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the WIDTH variable to the pixel width of the canvas divided by the PIXEL_SIZE
        WIDTH = (int) canvas.getWidth() / PIXEL_SIZE;
        // Set the HEIGHT variable to the pixel height of the canvas divided by the PIXEL_SIZE
        HEIGHT = (int) canvas.getHeight() / PIXEL_SIZE;
        /*
            Assign the graphics context of the canvas to the gc variable, so that the gc variable
            can be used to manipulate graphics elements of the canvas.
         */
        gc = canvas.getGraphicsContext2D();
        // Request focus for the canvas (so that mouse input can be captured properly).
        canvas.requestFocus();
        // Initialize the grid as a new 2D Node array with WIDTH number of columns and HEIGHT number of rows
        aStar = new AStar(WIDTH, HEIGHT);
        // Call the render function
        render();
    }

    /*
        This function is used in order to render the graphics inside the canvas,
        such as drawing the lines on the grid and filling in all wall squares as black.
        When drawing, the co-ordinates have to be multiplied by the PIXEL_SIZE because
        the co-ordinates are relative to the size of the grid in rows and columns, however
        when drawing it must be referred to by exact pixel position.
     */
    private void render() {
        // Loop through each node on the grid
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                // Clear the current node on the grid
                gc.clearRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                // If this node is a wall set the fill colour to black
                if (aStar.isWall(x,y)) gc.setFill(Color.BLACK);
                // Else if this node is the start node set the fill colour to green
                else if (aStar.isStart(x,y)) gc.setFill(Color.GREEN);
                // Else if this node is the end node set the fill colour to red
                else if (aStar.isEnd(x,y)) gc.setFill(Color.RED);
                // Else if this node is on the path set the fill colour to blue
                else if (aStar.isOnPath(x,y)) gc.setFill(Color.BLUE);
                // If this node fulfilled none of the above conditions, skip to the next node
                else continue;
                // Fill this node with the current fill colour
                gc.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
        // Set the fill colour to black in order to draw the grid lines
        gc.setFill(Color.BLACK);
        // Loop through the columns of the grid
        for (int x = 0; x < canvas.getWidth(); x += PIXEL_SIZE) {
            // Draw a line from the top to the bottom of the screen for each column
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        // Loop through the rows of the grid
        for (int y = 0; y < canvas.getHeight(); y += PIXEL_SIZE) {
            // Draw a line from the left to the right of the screen for each row
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    private boolean isInGrid(int x, int y) {
        return x < WIDTH && y < HEIGHT && x >= 0 && y >= 0;
    }

    /*
        A function triggered by the "Set Start" button which sets the start node to
        where the user next clicks on the grid.
     */
    public void setStart() {
        // Set the currentBtn variable to "setStart" so we know this is the last button that was clicked on
        currentBtn = "setStart";
        // Add a new mouse event to the canvas that triggers when the mouse is pressed in the canvas
        canvas.setOnMousePressed(mouseEvent -> {
            // If the last button pressed was Set Start
            if (currentBtn.equals("setStart")) {
                // Get the x position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable x
                int x = (int) mouseEvent.getX() / PIXEL_SIZE;
                // Get the y position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable y
                int y = (int) mouseEvent.getY() / PIXEL_SIZE;
                // If the node the user has clicked on is within the bounds of the grid, and it is not a wall
                if (isInGrid(x, y) && !aStar.getGrid()[x][y].isWall())
                    // Set the start node to the node the user clicked on
                    aStar.setStart(x,y);
                // Call the render function
                render();
            }
        });
    }

    /*
        A function triggered by the "Set End" button which sets the end node to
        where the user next clicks on the grid.
     */
    public void setEnd() {
        // Set the currentBtn variable to "setEnd" so we know this is the last button that was clicked on
        currentBtn = "setEnd";
        // Add a new mouse event to the canvas that triggers when the mouse is pressed in the canvas
        canvas.setOnMousePressed(mouseEvent -> {
            // If the last button pressed was Set End
            if (currentBtn.equals("setEnd")) {
                // Get the x position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable x
                int x = (int) mouseEvent.getX() / PIXEL_SIZE;
                // Get the y position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable y
                int y = (int) mouseEvent.getY() / PIXEL_SIZE;
                // If the node the user has clicked on is within the bounds of the grid, and it is not a wall
                if (isInGrid(x, y) && !aStar.isWall(x,y))
                    // Set the end node to the node the user clicked on
                    aStar.setEnd(x,y);
                // Call the render function
                render();
            }
        });
    }

    /*
        A function triggered by the "Draw Walls" button which allows the user to draw walls
        by clicking and dragging on the grid
     */
    public void drawWalls() {
        // Set the currentBtn variable to "drawWalls" so we know this is the last button that was clicked on
        currentBtn = "drawWalls";
        // Add a new mouse event to the canvas that triggers when the mouse is dragged in the canvas
        canvas.setOnMouseDragged(mouseEvent -> {
            // If the last button pressed was Draw Walls
            if (currentBtn.equals("drawWalls")) {
                // Get the x position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable x
                int x = (int) mouseEvent.getX() / PIXEL_SIZE;
                // Get the y position of the mouse, divide it by PIXEL_SIZE, and assign it to a temporary variable y
                int y = (int) mouseEvent.getY() / PIXEL_SIZE;
                // If the node the user has dragged the mouse over is within the bounds of the grid
                if (isInGrid(x, y))
                    // Set the node the user dragged over to being a wall
                    aStar.setWall(x,y,true);
                // Call the render function
                render();
            }
        });
    }

    // A function triggered by the "Run" button which runs the algorithm
    public void runAlgorithm() {
        // Set the currentBtn variable to "run" so we know this is the last button that was clicked on
        currentBtn = "run";
        aStar.run();
        // Call the render function
        render();
    }

    // A function triggered by the "Clear" button which clears the grid
    public void clearGrid() {
        // Set the currentBtn variable to "clearGrid" so we know this is the last button that was clicked on
        currentBtn = "clearGrid";
        aStar = new AStar(WIDTH,HEIGHT);
        // Call the render function
        render();
    }
}
