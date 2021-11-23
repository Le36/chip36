package com.chip8.ui;

import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Display extends Application {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;

    public void start(Stage stage) {

        final int width = 640;
        final int height = 320;

        PixelManager pixels = new PixelManager(64, 32);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();

        Button selectRom = new Button("Select ROM");
        Button resetRom = new Button("Reset ROM");
        ToggleButton pause = new ToggleButton("Pause ROM");
        Button nextStep = new Button("Next Instruction");

        ToggleButton fadeButton = new ToggleButton("Fade On");
        Slider fadeSlider = new Slider(0.0001, 0.3, 0.1);
        Slider slider = new Slider(1, 100, 20);
        Label gameSpeedLabel = new Label("ROM Speed: ");
        Label fadeSpeedLabel = new Label("Fade Speed: ");
        pause.setMinSize(80, 20);
        fadeButton.setMinSize(65, 20);
        gameSpeed = slider.getValue();

        ToolBar toolBar = new ToolBar();
        stage.setTitle("Chip8 Emulator");

        HBox hboxLeft = new HBox(4, selectRom, resetRom, pause, nextStep, fadeButton, new Separator(Orientation.VERTICAL));
        HBox hboxRight = new HBox(4, fadeSpeedLabel, fadeSlider, gameSpeedLabel, slider);
        HBox hbox = new HBox(90, hboxLeft, hboxRight);
        toolBar.getItems().add(hbox);

        Label currentInstruction = new Label("Current Instruction: 0x0");
        Label indexRegister = new Label("Index Register: 0x0");
        Label programCounter = new Label("Program Counter: 0x0");
        Label delayTimer = new Label("Delay Timer: 0x0");

        currentInstruction.setFont(new Font("Consolas", 18));
        indexRegister.setFont(new Font("Consolas", 18));
        programCounter.setFont(new Font("Consolas", 18));
        delayTimer.setFont(new Font("Consolas", 18));

        currentInstruction.setMinSize(290, 20);

        GridPane registers = new GridPane();

        ArrayList<Label> registerLabels = new ArrayList<>();

        for (int i = 0, first = 0, second = 0; i < 16; i++) {
            Label lab = new Label("V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x0");
            lab.setMinSize(75, 20);

            lab.setFont(new Font("Consolas", 14));
            registerLabels.add(lab);
            registers.add(lab, first, second);
            first++;
            if (first == 4) {
                second++;
                first = 0;
            }
        }

        registers.setHgap(5);
        registers.setVgap(5);
        registers.setMinSize(10.0, 10.0);

        Background bg = new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        VBox vbox = new VBox(currentInstruction, new Separator(Orientation.HORIZONTAL), indexRegister, new Separator(Orientation.HORIZONTAL), programCounter, new Separator(Orientation.HORIZONTAL), delayTimer, new Separator(Orientation.HORIZONTAL), registers);


        BorderPane bottomPane = new BorderPane();
        TextArea hexDumpArea = new TextArea();
        bottomPane.setRight(hexDumpArea);

        hexDumpArea.setMinSize(520, 150);
        hexDumpArea.setFont(new Font("Consolas", 12));
        hexDumpArea.setBackground(bg);
        hexDumpArea.setEditable(false);


        //ListView instructionList = new ListView();
        //bottomPane.setLeft(instructionList);

        Canvas canvas = new Canvas(width, height);
        GraphicsContext paint = canvas.getGraphicsContext2D();
        BorderPane root = new BorderPane();
        root.setBackground(bg);
        root.setTop(toolBar);
        root.setRight(canvas);
        root.setLeft(vbox);
        root.setBottom(bottomPane);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        //stage.setResizable(false);
        stage.sizeToScene();

        // keyboard for emulator
        scene.addEventFilter(KeyEvent.ANY, keys::setKey);

        selectRom.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null || selectedFile.length() > 4096 || selectedFile.length() < 2) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
            hexDumpArea.setText(executer.getLoader().hexDump());
        });

        resetRom.setOnAction(e -> {
            if (selectedFile == null) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
        });

        pause.setOnAction(e -> {
            if (!pause.isSelected()) {
                pause.setText("Pause ROM");
            } else {
                pause.setText("Unpause");
            }
        });

        nextStep.setOnAction(e -> {
            if (fileChosen) executer.execute();
        });

        fadeButton.setOnAction(e -> {
            if (!fadeButton.isSelected()) {
                fadeButton.setText("Fade On");
                pixels.setFade(true);
            } else {
                fadeButton.setText("Fade Off");
                pixels.setFade(false);
            }
        });


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep((long) gameSpeed);
                    if (!pause.isSelected() && fileChosen) executer.execute();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                    }
                });
            }
        }).start();


        new AnimationTimer() {
            public void handle(long l) {
                gameSpeed = slider.getValue();
                pixels.setFadeSpeed(fadeSlider.getValue());
                if (!fileChosen) return;

                currentInstruction.setText("Current instruction: 0x" + Integer.toHexString((executer.getFetcher().getOpcode() & 0xFFFF)).toUpperCase());
                indexRegister.setText("Index register: 0x" + Integer.toHexString((executer.getMemory().getI() & 0xFFFF)).toUpperCase());
                programCounter.setText("Program counter: 0x" + Integer.toHexString((executer.getMemory().getPc() & 0xFFFF)).toUpperCase());
                delayTimer.setText("Delay timer: 0x" + Integer.toHexString((executer.getMemory().getDelayTimer() & 0xFF)).toUpperCase());

                for (int i = 0; i < 16; i++) {
                    registerLabels.get(i).setText(" V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x" + Integer.toHexString((executer.getMemory().getV()[i] & 0xFF)).toUpperCase());
                }


                // paints everything black every cycle
                paint.setFill(Color.BLACK);
                paint.fillRect(0, 0, width, height);

                pixels.fade(); // fades all pixels that have been erased

                // draws fading pixels
                if (!fadeButton.isSelected()) {
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

            }
        }.start();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

