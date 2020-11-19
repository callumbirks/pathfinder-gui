package com.callumbirks;

import javafx.application.Application;

public class Main {

    /*
        Launch the JavaFX application
        It was necessary to separate this function into a separate class that does not
        extend Application in order to compile it properly into an Uber-JAR.
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
