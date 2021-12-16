package com.chip8.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * launch javafx application
 */
public class Ui extends Application {
    /**
     * starts the launcher of emulator
     *
     * @param stage current stage
     */
    public void start(Stage stage) {
        new Launcher();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

