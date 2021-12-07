package com.chip8.ui;

import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import com.chip8.emulator.PixelManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class EmulatorUi extends Stage {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;
    final int width = 64;
    final int height = 32;

    EmulatorUi(boolean mode, int scale) {
        UiElements uiElements = new UiElements();

        this.setTitle("Chip8 Emulator");
        PixelManager pixels = new PixelManager(width, height);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();
        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));


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
        HBox hbox = new HBox(165, hboxLeft, hboxRight);
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(hbox);
        toolBar.getStylesheets().add("toolbar.css");
        toolBar.setBorder(border);

        Label currentInstruction = uiElements.makeLabel("Current Instruction: 0x0", LabelType.LABEL);
        Label indexRegister = uiElements.makeLabel("Index Register: 0x0", LabelType.LABEL);
        Label programCounter = uiElements.makeLabel("Program Counter: 0x0", LabelType.LABEL);
        Label delayTimer = uiElements.makeLabel("Delay Timer: 0x0", LabelType.TOOLBAR);
        Label soundTimer = uiElements.makeLabel("Sound Timer: 0x0", LabelType.TOOLBAR);
        Label stackSize = uiElements.makeLabel("Stack size: 0", LabelType.TOOLBAR);
        Label stackPeek = uiElements.makeLabel("Stack peek: 0x0", LabelType.TOOLBAR);

        GridPane stackAndTimers = stackTimers(uiElements, delayTimer, soundTimer, stackSize, stackPeek);

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
        registers.setBorder(border);
        registers.setHgap(5);
        registers.setVgap(5);
        registers.setMinSize(10.0, 10.0);

        TextArea currentDetailed = uiElements.makeTextArea(290, 105);
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        VBox vboxLeft = new VBox(currentInstruction, uiElements.separator(), indexRegister, uiElements.separator(), programCounter, uiElements.separator(), stackAndTimers, registers, currentDetailed);
        vboxLeft.setBorder(border);

        BorderPane spriteViewerPane = new BorderPane();
        SpriteDisplay spriteDisplay = new SpriteDisplay(pixels);
        spriteViewerPane.setTop(uiElements.makeLabel("Sprite viewer:", LabelType.TOOLBAR));
        spriteViewerPane.setCenter(spriteDisplay);
        spriteViewerPane.setBorder(border);

        TextArea hexDumpArea = uiElements.makeTextArea(520, 183);

        BorderPane bottomPane = new BorderPane();
        bottomPane.setBorder(border);
        bottomPane.setCenter(spriteViewerPane);
        bottomPane.setRight(hexDumpArea);
        bottomPane.setLeft(disassembler);

        RomDisplay romDisplay = new RomDisplay(pixels, width * scale, height * scale);

        Keyboard keyboard = new Keyboard(keys);
        VBox vBoxKeyboard = new VBox(5, uiElements.makeLabel("Keyboard:", LabelType.TOOLBAR), keyboard);
        vBoxKeyboard.setBorder(border);

        TextField forceOpcodeText = uiElements.makeTextField();
        forceOpcodeText.setText("0x0000");
        Button forceOpcodeButton = uiElements.makeButton("Force opcode");
        VBox vBoxForceOpcode = new VBox(5, uiElements.makeLabel("Force opcode:", LabelType.TOOLBAR), forceOpcodeText, forceOpcodeButton);
        vBoxForceOpcode.setAlignment(Pos.CENTER_LEFT);
        vBoxForceOpcode.setBorder(border);

        TextField stepText = uiElements.makeTextField();
        stepText.setEditable(false);
        stepText.setText("2");
        Button stepButton = uiElements.makeButton("Step");
        Button skipButton = uiElements.makeButton("Skip one");
        Button stepPlus = uiElements.makeButton("+");
        Button stepMinus = uiElements.makeButton("-");
        CheckBox ignoreDelay = uiElements.makeCheckBox("Ignore delay");

        VBox vBoxStepControl = new VBox(5, uiElements.makeLabel("Step control:", LabelType.TOOLBAR), new HBox(5, stepText, stepPlus, stepMinus), new HBox(5, stepButton, skipButton), ignoreDelay);
        vBoxStepControl.setAlignment(Pos.CENTER_LEFT);
        vBoxStepControl.setBorder(border);

        BorderPane rightSide = rightSide(border, vBoxKeyboard, vBoxForceOpcode, vBoxStepControl);

        BorderPane root = new BorderPane();
        root.setBorder(border);
        root.setBackground(bg);
        root.setTop(toolBar);
        root.setCenter(romDisplay);
        if (mode) {
            root.setCenter(romDisplay);
            root.setLeft(vboxLeft);
            root.setBottom(bottomPane);
            root.setRight(rightSide);
        }
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.sizeToScene();

        // keyboard for emulator
        scene.addEventFilter(KeyEvent.ANY, keys::setKey);

        this.setOnCloseRequest(windowEvent -> System.exit(0));

        selectRom.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(this);
            if (selectedFile == null || selectedFile.length() > 4096 || selectedFile.length() < 2) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys);
            fileChosen = true;
            pixels.clearDisplay();
            hexDumpArea.setText(executer.getLoader().hexDump());
            this.setTitle("Chip8 Emulator | Loaded ROM: " + selectedFile.getName());
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

        forceOpcodeButton.setOnAction(e -> {
            if (selectedFile == null) return;
            if (forceOpcodeText.getText().matches("0x[0-9A-Fa-f]{4}")) {
                executer.forceOpcode(Integer.decode(forceOpcodeText.getText()));
            } else {
                forceOpcodeText.setText("Bad format");
            }
        });

        stepButton.setOnAction(e -> {
            if (selectedFile == null) return;
            for (int i = 0; i < Integer.parseInt(stepText.getText()); i++) {
                executer.execute();
            }
        });

        skipButton.setOnAction(e -> {
            if (selectedFile == null) return;
            executer.getFetcher().incrementPC();
        });

        stepMinus.setOnAction(e -> {
            if (Integer.parseInt(stepText.getText()) != 0) {
                stepText.setText(String.valueOf(Integer.parseInt(stepText.getText()) - 1));
            }
        });

        stepPlus.setOnAction(e -> {
            if (Integer.parseInt(stepText.getText()) != 100) {
                stepText.setText(String.valueOf(Integer.parseInt(stepText.getText()) + 1));
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
                    if (mode) spriteDisplay.draw();

                    pixels.fade(); // fades all pixels that have been erased

                    if (!fileChosen) return;
                    if (executer.getMemory().getSoundTimer() != (byte) 0x0) {
                        mediaPlayer.stop();
                        mediaPlayer.play();
                    }

                    if (mode) {
                        updateLabels(currentInstruction, indexRegister, programCounter, delayTimer, soundTimer, registerLabels, currentDetailed, stackSize, stackPeek);
                        disassembler.update(executer.getMemory().getPc(), executer.getFetcher());

                        if (ignoreDelay.isSelected()) {
                            executer.getMemory().setDelayTimer((byte) 0);
                        }
                    }
                });
            }
        }).start();

        this.show();
    }

    private BorderPane rightSide(Border border, VBox vBoxKeyboard, VBox vBoxForceOpcode, VBox vBoxStepControl) {
        BorderPane rightSide = new BorderPane();
        rightSide.setTop(vBoxKeyboard);
        rightSide.setLeft(vBoxForceOpcode);
        rightSide.setBottom(vBoxStepControl);
        rightSide.setBorder(border);
        if (!System.getProperty("os.name").startsWith("Windows")) {
            vBoxKeyboard.setPrefSize(150, 150);
            vBoxForceOpcode.setPrefSize(150, 50);
            vBoxStepControl.setPrefSize(150, 100);
        } else {
            vBoxKeyboard.setPrefSize(130, 140);
            vBoxForceOpcode.setPrefSize(130, 50);
            vBoxStepControl.setPrefSize(130, 100);
        }
        vBoxForceOpcode.setPadding(new Insets(1, 5, 3, 3));
        vBoxStepControl.setPadding(new Insets(1, 1, 1, 3));
        return rightSide;
    }

    private GridPane stackTimers(UiElements uiElements, Label delayTimer, Label soundTimer, Label stackSize, Label stackPeek) {
        GridPane stackAndTimers = new GridPane();
        stackAndTimers.add(uiElements.separator(), 0, 0);
        stackAndTimers.add(uiElements.separator(), 0, 1);
        stackAndTimers.add(delayTimer, 1, 0);
        stackAndTimers.add(soundTimer, 1, 1);
        stackAndTimers.add(uiElements.separator(), 2, 0);
        stackAndTimers.add(uiElements.separator(), 2, 1);
        stackAndTimers.add(stackSize, 3, 0);
        stackAndTimers.add(stackPeek, 3, 1);
        delayTimer.setMinSize(150, 0);
        soundTimer.setMinSize(150, 0);
        return stackAndTimers;
    }

    private void updateLabels(Label currentInstruction, Label indexRegister, Label programCounter, Label delayTimer, Label soundTimer, ArrayList<Label> registerLabels, TextArea currentDetailed, Label stackSize, Label stackPeek) {
        currentInstruction.setText("Current instruction: 0x" + Integer.toHexString((executer.getFetcher().getOpcode() & 0xFFFF)).toUpperCase());
        indexRegister.setText("Index register: 0x" + Integer.toHexString((executer.getMemory().getI() & 0xFFFF)).toUpperCase());
        programCounter.setText("Program counter: 0x" + Integer.toHexString((executer.getMemory().getPc() & 0xFFFF)).toUpperCase());
        delayTimer.setText("Delay Timer: 0x" + Integer.toHexString((executer.getMemory().getDelayTimer() & 0xFF)).toUpperCase());
        soundTimer.setText("Sound Timer: 0x" + Integer.toHexString((executer.getMemory().getSoundTimer() & 0xFF)).toUpperCase());
        stackSize.setText("Stack size: " + executer.getMemory().getStack().size());
        try {
            stackPeek.setText("Stack peek: 0x" + Integer.toHexString(executer.getMemory().getStack().peek() & 0xFFFF).toUpperCase());
        } catch (NullPointerException ex) {
            stackPeek.setText("Stack peek: empty");
        }
        for (int i = 0; i < 16; i++) {
            registerLabels.get(i).setText(" V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x" + Integer.toHexString((executer.getMemory().getV()[i] & 0xFF)).toUpperCase());
        }
        currentDetailed.setText(executer.getDecoder().getDetailed());
    }
}
