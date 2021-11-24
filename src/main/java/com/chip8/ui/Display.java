package com.chip8.ui;

import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import com.chip8.emulator.PixelManager;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Display extends Application {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;
    final int width = 640;
    final int height = 320;

    public void start(Stage stage) {
        stage.setTitle("Chip8 Emulator");
        PixelManager pixels = new PixelManager(64, 32);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();

        UiElements uiElements = new UiElements();

        Button selectRom = uiElements.makeButton("Select ROM");
        Button resetRom = uiElements.makeButton("Reset ROM");
        ToggleButton pause = uiElements.makeToggleButton("Pause ROM");
        Button nextStep = uiElements.makeButton("Next Instruction");
        ToggleButton fadeButton = uiElements.makeToggleButton("Fade On");

        Slider fadeSlider = uiElements.makeSlider(0.0001, 0.3, 0.1);
        Slider slider = uiElements.makeSlider(1, 100, 20);
        Label gameSpeedLabel = uiElements.makeLabel("ROM Speed: ", LabelType.TOOLBAR);
        Label fadeSpeedLabel = uiElements.makeLabel("Fade Speed: ", LabelType.TOOLBAR);

        HBox hboxLeft = new HBox(4, selectRom, resetRom, pause, nextStep, fadeButton);
        HBox hboxRight = new HBox(4, fadeSpeedLabel, fadeSlider, gameSpeedLabel, slider);
        HBox hbox = new HBox(35, hboxLeft, hboxRight);
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(hbox);
        toolBar.getStylesheets().add("toolbar.css");

        Label currentInstruction = uiElements.makeLabel("Current Instruction: 0x0", LabelType.LABEL);
        Label indexRegister = uiElements.makeLabel("Index Register: 0x0", LabelType.LABEL);
        Label programCounter = uiElements.makeLabel("Program Counter: 0x0", LabelType.LABEL);
        Label delayTimer = uiElements.makeLabel("Delay Timer: 0x0", LabelType.LABEL);

        GridPane registers = new GridPane();
        ArrayList<Label> registerLabels = new ArrayList<>();
        for (int i = 0, first = 0, second = 0; i < 16; i++) {
            Label lab = uiElements.makeLabel("V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x0", LabelType.REGISTER);
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

        TextArea currentDetailed = uiElements.makeTextArea();
        currentDetailed.setPrefSize(290, 105);

        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        VBox vbox = new VBox(currentInstruction, new Separator(Orientation.HORIZONTAL), indexRegister, new Separator(Orientation.HORIZONTAL), programCounter, new Separator(Orientation.HORIZONTAL), delayTimer, new Separator(Orientation.HORIZONTAL), registers, currentDetailed);

        BorderPane bottomPane = new BorderPane();
        TextArea hexDumpArea = uiElements.makeTextArea();
        hexDumpArea.setPrefSize(520, 145);
        bottomPane.setRight(hexDumpArea);

        ListView instructionList = uiElements.makeListView();
        bottomPane.setLeft(instructionList);

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

                if (!fileChosen) return;

                currentInstruction.setText("Current instruction: 0x" + Integer.toHexString((executer.getFetcher().getOpcode() & 0xFFFF)).toUpperCase());
                indexRegister.setText("Index register: 0x" + Integer.toHexString((executer.getMemory().getI() & 0xFFFF)).toUpperCase());
                programCounter.setText("Program counter: 0x" + Integer.toHexString((executer.getMemory().getPc() & 0xFFFF)).toUpperCase());
                delayTimer.setText("Delay timer: 0x" + Integer.toHexString((executer.getMemory().getDelayTimer() & 0xFF)).toUpperCase());

                for (int i = 0; i < 16; i++) {
                    registerLabels.get(i).setText(" V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x" + Integer.toHexString((executer.getMemory().getV()[i] & 0xFF)).toUpperCase());
                }

                currentDetailed.setText(executer.getDecoder().getDetailed());

                instructionList.getItems().clear();
                short pc = executer.getMemory().getPc();
                for (int i = 0; i < 7; i++) {
                    short opcode = executer.getFetcher().seek(pc);
                    executer.getDecoder().decode(opcode, true);
                    String instruction = Integer.toHexString((opcode & 0xFFFF)).toUpperCase();
                    String base = "0x";
                    if (instruction.length() == 1) {
                        base = "0x000";
                    } else if (instruction.length() == 2) {
                        base = "0x00";
                    } else if (instruction.length() == 3) {
                        base = "0x0";
                    }
                    instructionList.getItems().add(base + instruction + " | " + executer.getDecoder().getSeekString());
                    pc += 2;
                }
            }
        }.start();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

