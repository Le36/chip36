package com.chip8.ui;

import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;

public class Display extends Application {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;
    private boolean paused;

    public void start(Stage stage) {

        paused = false;
        final int width = 640;
        final int height = 320;

        PixelManager pixels = new PixelManager(64, 32);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();

        Button selectRom = new Button("Select ROM");
        Button resetRom = new Button("Reset ROM");
        Button pause = new Button("Pause ROM");
        Button nextStep = new Button("Next Instruction");
        Slider slider = new Slider(0, 100, 20);
        Label gameSpeedLabel = new Label("Game Speed: ");

        stage.setTitle("Chip8 Emulator");
        HBox hboxLeft = new HBox(4, selectRom, resetRom, pause, nextStep);
        HBox hboxRight = new HBox(4, gameSpeedLabel, slider);
        HBox hbox = new HBox(300, hboxLeft, hboxRight);


        Label currentInstruction = new Label("Current Instruction: ");
        Label indexRegister = new Label("Index Register: ");
        Label programCounter = new Label("Program Counter: ");
        Label delayTimer = new Label("Delay Timer: ");

        currentInstruction.setFont(new Font("Arial", 18));
        indexRegister.setFont(new Font("Arial", 18));
        programCounter.setFont(new Font("Arial", 18));
        delayTimer.setFont(new Font("Arial", 18));

        GridPane registers = new GridPane();

        Label v0 = new Label("V0: ");
        Label v1 = new Label("V1: ");
        Label v2 = new Label("V2: ");
        Label v3 = new Label("V3: ");
        Label v4 = new Label("V4: ");
        Label v5 = new Label("V5: ");
        Label v6 = new Label("V6: ");
        Label v7 = new Label("V7: ");
        Label v8 = new Label("V8: ");
        Label v9 = new Label("V9: ");
        Label vA = new Label("VA: ");
        Label vB = new Label("VB: ");
        Label vC = new Label("VC: ");
        Label vD = new Label("VD: ");
        Label vE = new Label("VE: ");
        Label vF = new Label("VF: ");

        v0.setMinSize(50, 20);
        v1.setMinSize(50, 20);
        v2.setMinSize(50, 20);
        v3.setMinSize(50, 20);

        registers.add(v0, 0, 0);
        registers.add(v1, 1, 0);
        registers.add(v2, 2, 0);
        registers.add(v3, 3, 0);
        registers.add(v4, 0, 1);
        registers.add(v5, 1, 1);
        registers.add(v6, 2, 1);
        registers.add(v7, 3, 1);
        registers.add(v8, 0, 2);
        registers.add(v9, 1, 2);
        registers.add(vA, 2, 2);
        registers.add(vB, 3, 2);
        registers.add(vC, 0, 3);
        registers.add(vD, 1, 3);
        registers.add(vE, 2, 3);
        registers.add(vF, 3, 3);

        registers.setHgap(10);
        registers.setVgap(10);
        registers.setMinSize(10.0, 10.0);
        registers.setGridLinesVisible(true);


        VBox vbox = new VBox(15, currentInstruction, indexRegister, programCounter, delayTimer, registers);


        Canvas canvas = new Canvas(width, height);
        GraphicsContext paint = canvas.getGraphicsContext2D();
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setTop(hbox);
        root.setRight(canvas);
        root.setLeft(vbox);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setResizable(false);
        stage.sizeToScene();

        // keyboard for emulator
        scene.addEventFilter(KeyEvent.ANY, keys::setKey);

        selectRom.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null || selectedFile.length() > 4096 || selectedFile.length() < 2) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
        });

        resetRom.setOnAction(e -> {
            if (selectedFile == null) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
        });

        pause.setOnAction(e -> {
            this.paused = !this.paused;
        });

        nextStep.setOnAction(e -> {
            executer.execute();
        });


        // currently AnimationTimer handling everything
        new AnimationTimer() {
            public void handle(long l) {

                if (!fileChosen) return;
                if (!paused) executer.execute();

                currentInstruction.setText("Current instruction: 0x" + Integer.toHexString((executer.getFetcher().getOpcode() & 0xFFFF)).toUpperCase());
                indexRegister.setText("Index register: 0x" + Integer.toHexString((executer.getMemory().getI() & 0xFFFF)).toUpperCase());
                programCounter.setText("Program counter: 0x" + Integer.toHexString((executer.getMemory().getPc() & 0xFFFF)).toUpperCase());
                delayTimer.setText("Delay timer: 0x" + Integer.toHexString((executer.getMemory().getDelayTimer() & 0xFF)).toUpperCase());

                v0.setText(" V0: 0x" + Integer.toHexString((executer.getMemory().getV()[0] & 0xFF)).toUpperCase());
                v1.setText(" V1: 0x" + Integer.toHexString((executer.getMemory().getV()[1] & 0xFF)).toUpperCase());
                v2.setText(" V2: 0x" + Integer.toHexString((executer.getMemory().getV()[2] & 0xFF)).toUpperCase());
                v3.setText(" V3: 0x" + Integer.toHexString((executer.getMemory().getV()[3] & 0xFF)).toUpperCase());
                v4.setText(" V4: 0x" + Integer.toHexString((executer.getMemory().getV()[4] & 0xFF)).toUpperCase());
                v5.setText(" V5: 0x" + Integer.toHexString((executer.getMemory().getV()[5] & 0xFF)).toUpperCase());
                v6.setText(" V6: 0x" + Integer.toHexString((executer.getMemory().getV()[6] & 0xFF)).toUpperCase());
                v7.setText(" V7: 0x" + Integer.toHexString((executer.getMemory().getV()[7] & 0xFF)).toUpperCase());
                v8.setText(" V8: 0x" + Integer.toHexString((executer.getMemory().getV()[8] & 0xFF)).toUpperCase());
                v9.setText(" V9: 0x" + Integer.toHexString((executer.getMemory().getV()[9] & 0xFF)).toUpperCase());
                vA.setText(" VA: 0x" + Integer.toHexString((executer.getMemory().getV()[10] & 0xFF)).toUpperCase());
                vB.setText(" VB: 0x" + Integer.toHexString((executer.getMemory().getV()[11] & 0xFF)).toUpperCase());
                vC.setText(" VC: 0x" + Integer.toHexString((executer.getMemory().getV()[12] & 0xFF)).toUpperCase());
                vD.setText(" VD: 0x" + Integer.toHexString((executer.getMemory().getV()[13] & 0xFF)).toUpperCase());
                vE.setText(" VE: 0x" + Integer.toHexString((executer.getMemory().getV()[14] & 0xFF)).toUpperCase());
                vF.setText(" VF: 0x" + Integer.toHexString((executer.getMemory().getV()[15] & 0xFF)).toUpperCase());

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
            }
        }.start();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

