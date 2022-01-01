package com.chip8.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URL;

import static javafx.scene.text.Font.*;

/**
 * launch javafx application
 */
public class Ui extends Application {
    /**
     * starts the launcher of emulator
     * loads the font from resources for the emulator to use
     *
     * @param stage current stage
     */
    public void start(Stage stage) {
        URL path = getClass().getClassLoader().getResource("fonts/Inconsolata_SemiExpanded-Bold.ttf");
        loadFont(path.toString(), 12);
        path = getClass().getClassLoader().getResource("fonts/Inconsolata_SemiExpanded-Regular.ttf");
        loadFont(path.toString(), 12);
        new Launcher();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

