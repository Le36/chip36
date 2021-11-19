package com.chip8.ui;

import com.chip8.emulator.Executer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;

public class Display extends Application {

    private Executer executer;

    public void start(Stage stage) {

        PixelManager pixels = new PixelManager(64, 32);
        // craft file chooser here etc... or below actually
        // IBM test rom, prints just IBM-logo
        String rom = "IBM";
        this.executer = new Executer(rom, pixels);

        final int width = 640;
        final int height = 320;

        stage.setTitle("Chip8 Emulator: " + rom);

        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        Canvas canvas = new Canvas(width, height);
        root.getChildren().add(canvas);

        GraphicsContext paint = canvas.getGraphicsContext2D();

        // currently AnimationTimer handling everything
        new AnimationTimer() {

            public void handle(long l) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }

                executer.execute(); // runs one cpu cycle
                pixels.fade(); // fades all pixels that have been erased

                // paints everything black every cycle
                paint.setFill(Color.BLACK);
                paint.fillRect(0, 0, width, height);
                // paints the current pixels that are actually on
                boolean[][] display = pixels.getDisplay();
                paint.setFill(Color.WHITE);
                for (int x = 0; x < 32; x++) {
                    for (int y = 0; y < 64; y++) {
                        if (display[y][x]) {
                            paint.fillRect(y * 10, x * 10, 10, 10);
                        }
                    }
                }

                // draws fading pixels
                HashMap<Integer, HashMap<Integer, Double>> fadeMap = pixels.getFadeMap();
                for (int x = 0; x < fadeMap.size(); x++) {
                    for (int y = 0; y < fadeMap.get(x).size(); y++) {
                        if (fadeMap.get(x).get(y) > 0.0) {
                            double fading = Math.min(0.95, fadeMap.get(x).get(y));

                            Color color = new Color(fading, fading, fading, 1);

                            paint.setFill(color);
                            paint.fillRect(x * 10, y * 10, 10, 10);
                        }
                    }
                }
                // press any key and it clears the screen for testing purposes
                scene.addEventFilter(KeyEvent.ANY, keyEvent -> {
                    executer.forceOpcode((short) 0x00E0);
                });
            }
        }.start();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

