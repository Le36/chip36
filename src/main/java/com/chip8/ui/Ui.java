package com.chip8.ui;

import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import com.chip8.emulator.PixelManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Ui extends Application {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;
    final int width = 640;
    final int height = 320;
    private boolean soundPlaying;

    public void start(Stage stage) {
        stage.setTitle("Chip8 Emulator");
        PixelManager pixels = new PixelManager(64, 32);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();
        soundPlaying = false;

        UiElements uiElements = new UiElements();

        Disassembler disassembler = uiElements.makeDisassembler();

        Button selectRom = uiElements.makeButton("Select ROM");
        Button resetRom = uiElements.makeButton("Reset ROM");
        ToggleButton pause = uiElements.makeToggleButton("Pause ROM");
        Button nextStep = uiElements.makeButton("Next Instruction");
        ToggleButton fadeButton = uiElements.makeToggleButton("Fade On");

        Slider fadeSlider = uiElements.makeSlider(0.0001, 0.3, 0.1);
        Slider slider = uiElements.makeSlider(1, 20, 1);
        Label gameSpeedLabel = uiElements.makeLabel("ROM Speed: ", LabelType.TOOLBAR);
        Label fadeSpeedLabel = uiElements.makeLabel("Fade Speed: ", LabelType.TOOLBAR);

        HBox hboxLeft = new HBox(4, selectRom, resetRom, pause, nextStep, fadeButton);
        HBox hboxRight = new HBox(4, fadeSpeedLabel, fadeSlider, gameSpeedLabel, slider);
        HBox hbox = new HBox(115, hboxLeft, hboxRight);
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(hbox);
        toolBar.getStylesheets().add("toolbar.css");

        Label currentInstruction = uiElements.makeLabel("Current Instruction: 0x0", LabelType.LABEL);
        Label indexRegister = uiElements.makeLabel("Index Register: 0x0", LabelType.LABEL);
        Label programCounter = uiElements.makeLabel("Program Counter: 0x0", LabelType.LABEL);
        Label delayTimer = uiElements.makeLabel("Delay Timer: 0x0", LabelType.TOOLBAR);
        Label soundTimer = uiElements.makeLabel("Sound Timer: 0x0", LabelType.TOOLBAR);

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

        TextArea currentDetailed = uiElements.makeTextArea(290, 105);

        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        VBox vbox = new VBox(currentInstruction, new Separator(Orientation.HORIZONTAL), indexRegister, new Separator(Orientation.HORIZONTAL), programCounter, new Separator(Orientation.HORIZONTAL), delayTimer, soundTimer, new Separator(Orientation.HORIZONTAL), registers, currentDetailed);


        BorderPane spriteViewerPane = new BorderPane();
        SpriteDisplay spriteDisplay = new SpriteDisplay(pixels);
        spriteViewerPane.setTop(uiElements.makeLabel("Sprite\nviewer:", LabelType.TOOLBAR));
        spriteViewerPane.setCenter(spriteDisplay);

        BorderPane bottomPane = new BorderPane();
        bottomPane.setCenter(spriteViewerPane);

        TextArea hexDumpArea = uiElements.makeTextArea(520, 145);
        bottomPane.setRight(hexDumpArea);
        bottomPane.setLeft(disassembler);

        RomDisplay romDisplay = new RomDisplay(pixels, width, height);

        Keyboard keyboard = new Keyboard(keys);

        BorderPane root = new BorderPane();
        root.setBackground(bg);
        root.setTop(toolBar);
        root.setCenter(romDisplay);
        root.setLeft(vbox);
        root.setBottom(bottomPane);
        root.setRight(keyboard);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();

        // keyboard for emulator
        scene.addEventFilter(KeyEvent.ANY, keys::setKey);

        stage.setOnCloseRequest(windowEvent -> System.exit(0));

        selectRom.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null || selectedFile.length() > 4096 || selectedFile.length() < 2) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
            hexDumpArea.setText(executer.getLoader().hexDump());
            stage.setTitle("Chip8 Emulator | Loaded ROM: " + selectedFile.getName());
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

        URL path = getClass().getClassLoader().getResource("beep.mp3");
        Media beep = new Media(path.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(beep);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                    if (!pause.isSelected() && fileChosen) {
                        for (int i = 0; i < gameSpeed; i++) {
                            executer.execute();
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> {
                    gameSpeed = slider.getValue();
                    pixels.setFadeSpeed(fadeSlider.getValue());

                    romDisplay.setFadeSelected(!fadeButton.isSelected());
                    romDisplay.draw();
                    spriteDisplay.draw();

                    pixels.fade(); // fades all pixels that have been erased

                    if (!fileChosen) return;

                    updateLabels(currentInstruction, indexRegister, programCounter, delayTimer, soundTimer, registerLabels, currentDetailed);

                    disassembler.update(executer.getMemory().getPc(), executer.getFetcher());

                    if (executer.getMemory().getSoundTimer() != (byte) 0x0 && !soundPlaying) {
                        // implement sound here
                        mediaPlayer.play();
                        soundPlaying = true;
                    } else {
                        mediaPlayer.stop();
                        soundPlaying = false;
                    }
                });
            }
        }).start();

        stage.show();
    }

    private void updateLabels(Label currentInstruction, Label indexRegister, Label programCounter, Label delayTimer, Label soundTimer, ArrayList<Label> registerLabels, TextArea currentDetailed) {
        currentInstruction.setText("Current instruction: 0x" + Integer.toHexString((executer.getFetcher().getOpcode() & 0xFFFF)).toUpperCase());
        indexRegister.setText("Index register: 0x" + Integer.toHexString((executer.getMemory().getI() & 0xFFFF)).toUpperCase());
        programCounter.setText("Program counter: 0x" + Integer.toHexString((executer.getMemory().getPc() & 0xFFFF)).toUpperCase());
        delayTimer.setText("Delay Timer: 0x" + Integer.toHexString((executer.getMemory().getDelayTimer() & 0xFF)).toUpperCase());
        soundTimer.setText("Sound Timer: 0x" + Integer.toHexString((executer.getMemory().getSoundTimer() & 0xFF)).toUpperCase());
        for (int i = 0; i < 16; i++) {
            registerLabels.get(i).setText(" V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x" + Integer.toHexString((executer.getMemory().getV()[i] & 0xFF)).toUpperCase());
        }
        currentDetailed.setText(executer.getDecoder().getDetailed());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

