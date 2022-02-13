package com.chip8.ui;

import com.chip8.configs.Configs;
import com.chip8.emulator.Executer;
import com.chip8.emulator.Keys;
import com.chip8.emulator.PixelManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * emulators main ui scene
 */
public class EmulatorUi extends Stage {

    private Executer executer;
    private boolean fileChosen;
    private File selectedFile;
    private double gameSpeed;
    final int width = 128;
    final int height = 64;
    private volatile boolean audioPlaying;
    private volatile int prevAudioTone;
    private volatile int prevSoundDelay;

    /**
     * generates ui for emulator
     *
     * @param mode  if using the extended or normal mode
     * @param scale scale for drawing the emulator display
     */
    EmulatorUi(boolean mode, int scale) {
        UiElements uiElements = new UiElements();

        this.setTitle("Chip8 Emulator");
        PixelManager pixels = new PixelManager(width, height);
        FileChooser fileChooser = new FileChooser();
        Keys keys = new Keys();
        Configs configs = new Configs();
        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

        Disassembler disassembler = uiElements.makeDisassembler();

        Button selectRom = uiElements.makeButton("Select ROM");
        Button resetRom = uiElements.makeButton("Reset ROM");
        ToggleButton pause = uiElements.makeToggleButton("Pause ROM");
        Button nextStep = uiElements.makeButton("Next Instruction");
        ToggleButton fadeButton = uiElements.makeToggleButton("Fade On");
        Button options = uiElements.makeButton("Options");

        Slider fadeSlider = uiElements.makeSlider(0.0001, 0.05, 0.05);
        Slider slider = uiElements.makeSlider(-20, 20, 1);
        Label gameSpeedLabel = uiElements.makeLabel("ROM Speed: ", LabelType.TOOLBAR);
        Label fadeSpeedLabel = uiElements.makeLabel("Fade Speed: ", LabelType.TOOLBAR);

        HBox hboxLeft = new HBox(4, selectRom, resetRom, pause, nextStep, fadeButton, options);
        HBox hboxRight = new HBox(4, fadeSpeedLabel, fadeSlider, gameSpeedLabel, slider);
        HBox toolbarHBoxTop = new HBox(45, hboxLeft, hboxRight);

        Button extDisassembler = uiElements.makeButton("Extended Disassembler");
        Button extStack = uiElements.makeButton("Extended Stack");
        ToggleButton randomColors = uiElements.makeToggleButton("Random");
        Label multiplierLabel = uiElements.makeLabel("ROM Speed Multiplier: ", LabelType.TOOLBAR);
        Slider multiplier = uiElements.makeSlider(1, 500, 1);
        HBox hboxBotLeft = new HBox(4, extDisassembler, extStack, randomColors);
        HBox hboxBotRight = new HBox(4, multiplierLabel, multiplier);
        HBox toolbarHBoxBottom = new HBox(372, hboxBotLeft, hboxBotRight);
        VBox toolbarVBox = new VBox(5, toolbarHBoxTop, toolbarHBoxBottom);
        ToolBar toolBar = new ToolBar();

        FpsCounter fps = new FpsCounter();
        Label currentFpsLabel = uiElements.makeLabel("0", LabelType.FPS);
        DecimalFormat df = new DecimalFormat("+#,###0.00;-#");
        Label currentSpeedLabel = uiElements.makeLabel(df.format(gameSpeed), LabelType.FPS);
        VBox fpsCounter = new VBox(uiElements.makeLabel("FPS:", LabelType.FPS), currentFpsLabel, uiElements.makeLabel("Speed:", LabelType.FPS), currentSpeedLabel);
        if (mode) {
            toolBar.getItems().add(new HBox(5, toolbarVBox, fpsCounter));
        } else {
            toolBar.getItems().add(toolbarHBoxTop);
        }
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
        SpriteDisplay spriteDisplay = new SpriteDisplay(pixels, configs);
        Button spriteExtract = uiElements.makeButton("Extract sprites");
        BorderPane paneSpriteButton = new BorderPane();
        paneSpriteButton.setPadding(new Insets(0, 5, 5, 5));
        paneSpriteButton.setCenter(spriteExtract);
        spriteViewerPane.setTop(uiElements.makeLabel("Sprite viewer:", LabelType.TOOLBAR));
        spriteViewerPane.setBottom(paneSpriteButton);
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

        if (!mode) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            if (scale * 128 > width || (scale + 5) * 64 > height) {
                this.setMaximized(true);
            }
        }

        // keyboard for emulator
        scene.addEventFilter(KeyEvent.ANY, keys::setKey);

        this.setOnCloseRequest(windowEvent -> System.exit(0));

        selectRom.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(this);
            // 4096 total memory, - 512 reserved = 3584 max in regular chip8 / s-chip
            // in XO there is 65536 total memory, 512 still reserved so = 65024 max in xo-chip
            if (selectedFile == null || selectedFile.length() > 65024 || selectedFile.length() < 2) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys, configs);
            pixels.setResolutionMode(false);
            fileChosen = true;
            clearDisplay(pixels);
            hexDumpArea.setText(executer.getLoader().hexDump());
            this.setTitle("Chip8 Emulator | Loaded ROM: " + selectedFile.getName());

            // check here if its 64x64 rom for hires mode
            specialHires(pixels);
        });

        resetRom.setOnAction(e -> {
            if (selectedFile == null) return;
            this.executer = new Executer(selectedFile.getAbsolutePath(), pixels, keys, configs);
            fileChosen = true;
            clearDisplay(pixels);
            specialHires(pixels);
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

        options.setOnAction(e -> new Options(keys, romDisplay, configs));

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

        spriteExtract.setOnAction(e -> new SpriteExtractor(configs, spriteDisplay));

        extDisassembler.setOnAction(e -> {
            if (selectedFile == null) return;
            new ExtendedDisassembler(executer);
        });

        extStack.setOnAction(e -> {
            if (selectedFile == null) return;
            new ExtendedStack(executer);
        });

        Audio audio = new Audio();

        // thread for audio handling
        new Thread(() -> {
            while (true) {
                if (audioPlaying) {
                    if (executer.getMemory().getSoundTimer() == 0) prevSoundDelay = 0;
                    if (prevSoundDelay > executer.getMemory().getSoundTimer()) {
                        if (prevAudioTone == executer.getMemory().getPitch()) {
                            continue;
                        }
                    }
                    try {
                        int pitch = executer.getMemory().getPitch();
                        int timer = Math.max(1, Byte.toUnsignedInt(executer.getMemory().getSoundTimer()));
                        audio.tone(pitch, executer.getMemory().getAudio());
                        this.prevAudioTone = pitch;
                        this.prevSoundDelay = timer;
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // thread for everything else
        new Thread(() -> {
            while (true) {
                try {
                    if (gameSpeed <= 0) {
                        Thread.sleep((long) Math.max(1, Math.abs(gameSpeed)));
                        if (!pause.isSelected() && fileChosen) executer.execute();
                    } else {
                        Thread.sleep(1);
                        if (!pause.isSelected() && fileChosen) {
                            for (int i = 0; i < gameSpeed; i++) {
                                executer.execute();
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> {
                    gameSpeed = slider.getValue();
                    pixels.setFadeSpeed(fadeSlider.getValue());
                    romDisplay.setFadeSelected(!fadeButton.isSelected());
                    gameSpeed *= multiplier.getValue();
                    currentFpsLabel.setText(String.format("%.0f", fps.update(System.nanoTime())));

                    if (!configs.isDisableUiUpdates()) {
                        romDisplay.draw();
                        if (mode) {
                            spriteDisplay.draw();
                            currentSpeedLabel.setText(df.format(gameSpeed));
                        }

                        pixels.fade(); // fades all pixels that have been erased

                        if (!fileChosen) return;
                        audioPlaying = executer.getMemory().getSoundTimer() != (byte) 0x0;

                        if (mode) {
                            updateLabels(currentInstruction, indexRegister, programCounter, delayTimer, soundTimer, registerLabels, currentDetailed, stackSize, stackPeek);
                            disassembler.update(executer.getMemory().getPc(), executer.getFetcher());
                            if (ignoreDelay.isSelected()) {
                                executer.getMemory().setDelayTimer((byte) 0);
                            }
                            if (randomColors.isSelected()) {
                                romDisplay.setBgColor(new RandomColors().getColor());
                                romDisplay.setSpriteColor(new RandomColors().getColor());
                                romDisplay.setPlaneColor(new RandomColors().getColor());
                                romDisplay.setBothColor(new RandomColors().getColor());
                            }
                        }
                    } else {
                        romDisplay.uiUpdatesDisabled();
                    }
                });
            }
        }).start();

        this.show();
    }

    private void clearDisplay(PixelManager pixels) {
        pixels.setCurrentPlane(3);
        pixels.clearDisplay();
        pixels.setCurrentPlane(1);
    }

    /**
     * used to check if loaded rom is a special 64x64 resolution rom
     *
     * @param pixels pixelmanager used by rom
     */
    private void specialHires(PixelManager pixels) {
        if (executer.getMemory().getRam()[0x200] == 0x12 && executer.getMemory().getRam()[0x201] == 0x60) {
            pixels.setResolutionMode(true);
            executer.getMemory().setPc((short) 0x2c0);
        }
    }

    private BorderPane rightSide(Border border, VBox vBoxKeyboard, VBox vBoxForceOpcode, VBox vBoxStepControl) {
        BorderPane rightSide = new BorderPane();
        rightSide.setTop(vBoxKeyboard);
        rightSide.setLeft(vBoxForceOpcode);
        rightSide.setBottom(vBoxStepControl);
        rightSide.setBorder(border);
        vBoxKeyboard.setPrefSize(130, 140);
        vBoxForceOpcode.setPrefSize(130, 50);
        vBoxStepControl.setPrefSize(130, 100);
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
        } catch (NullPointerException ignored) {
            stackPeek.setText("Stack peek: empty");
        }
        for (int i = 0; i < 16; i++) {
            registerLabels.get(i).setText(" V" + Integer.toHexString(i & 0xF).toUpperCase() + ": 0x" + Integer.toHexString((executer.getMemory().getV()[i] & 0xFF)).toUpperCase());
        }
        currentDetailed.setText(executer.getDecoder().getDetailed());
    }
}