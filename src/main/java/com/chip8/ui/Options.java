package com.chip8.ui;

import com.chip8.configs.*;
import com.chip8.emulator.Keys;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAction;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * generates the options window for ui that can be found in the toolbar
 */
public class Options extends Stage {

    /**
     * @param keys       keyboard presses used for rebinding
     * @param romDisplay display for rom used for selecting colors
     * @param configs    configurations for emu and ui
     */
    Options(Keys keys, RomDisplay romDisplay, Configs configs) {
        this.setTitle("Options");
        UiElements uiElements = new UiElements();

        EffectController effectController = new EffectController(romDisplay);

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        Button setDefault = uiElements.makeButton("Set default");
        Button saveChanges = uiElements.makeButton("Save changes");
        Button applyChanges = uiElements.makeButton("Apply changes");
        HBox hBoxBottom = new HBox(10, setDefault, saveChanges, applyChanges);

        Rebinds rebinds = new Rebinds(keys);

        CheckBox quirkShift = uiElements.makeCheckBox("Shift left / right quirk");
        quirkShift.setTooltip(uiElements.tooltip("Changes how shift left and right instructions work. When enabled v[x] is first copied from v[y]."));
        quirkShift.setSelected(configs.isQuirkShift());
        CheckBox quirkJump = uiElements.makeCheckBox("Jump with offset quirk");
        quirkJump.setTooltip(uiElements.tooltip("Changes jump with offset instruction bnnn to bxnn, where it will jump to xnn + v[x]"));
        quirkJump.setSelected(configs.isQuirkJump());
        CheckBox quirkIncrementIndex = uiElements.makeCheckBox("Increment index on dump / fill");
        quirkIncrementIndex.setTooltip(uiElements.tooltip("Changes how FX55 and FX65 instructions work. Enabling this will increment index register when using these instructions."));
        quirkIncrementIndex.setSelected(configs.isQuirkIncrementIndex());

        VBox vBoxBinds = new VBox(10, uiElements.makeLabel("Rebind your keys: ", LabelType.TOOLBAR), rebinds, uiElements.makeLabel("Quirks: ", LabelType.TOOLBAR), quirkShift, quirkJump, quirkIncrementIndex);

        ColorPicker spriteColor = uiElements.colorPicker();
        ColorPicker bgColor = uiElements.colorPicker();
        ColorPicker planeColor = uiElements.colorPicker();
        ColorPicker bothColor = uiElements.colorPicker();

        spriteColor.setValue(Color.web(romDisplay.getSpriteColor()));
        bgColor.setValue(Color.web(romDisplay.getBgColor()));
        planeColor.setValue(Color.web(romDisplay.getPlaneColor()));
        bothColor.setValue(Color.web(romDisplay.getBothColor()));
        // colors so we can restore them if not applied / saved
        configs.setSpriteColor(romDisplay.getSpriteColor());
        configs.setBgColor(romDisplay.getBgColor());
        configs.setPlaneColor(romDisplay.getPlaneColor());
        configs.setBothColor(romDisplay.getBothColor());


        CheckBox printConsole = uiElements.makeCheckBox("Print to console");
        CheckBox disableUiUpdates = uiElements.makeCheckBox("Disable ui updates");
        printConsole.setSelected(configs.isPrintToConsole());
        disableUiUpdates.setSelected(configs.isDisableUiUpdates());
        TextField printableSymbol = uiElements.makeTextField();
        printableSymbol.setText(configs.getPrintSymbol());

        VBox vBoxRight = new VBox(10, uiElements.makeLabel("Sprite color:", LabelType.TOOLBAR), spriteColor,
                uiElements.makeLabel("Bg color:", LabelType.TOOLBAR), bgColor, uiElements.makeLabel("Plane color:", LabelType.TOOLBAR),
                planeColor, uiElements.makeLabel("Both color:", LabelType.TOOLBAR), bothColor);

        CheckBox roundPixels = uiElements.makeCheckBox("Round pixels");
        roundPixels.setSelected(configs.isRoundPixels());

        Label blurLabel = uiElements.makeLabel("Blur strength:", LabelType.TOOLBAR);
        Slider blurSlider = uiElements.makeSlider(0, 50, 0);
        blurSlider.setValue(configs.getBlurValue());
        CheckBox blurEnabled = uiElements.makeCheckBox("Enable blur");
        blurEnabled.setSelected(configs.isBlur());

        Label glowLabel = uiElements.makeLabel("Glow strength:", LabelType.TOOLBAR);
        Slider glowSlider = uiElements.makeSlider(0, 1, 0);
        glowSlider.setValue(configs.getGlowValue());
        CheckBox glowEnabled = uiElements.makeCheckBox("Enable glow");
        glowEnabled.setSelected(configs.isGlow());

        VBox effects = new VBox(10, blurLabel, blurSlider, blurEnabled, glowLabel, glowSlider, glowEnabled, roundPixels, printConsole,
                new HBox(5, uiElements.makeLabel("Print symbol:", LabelType.SMALL), printableSymbol), disableUiUpdates);
        effects.setPadding(new Insets(0, 10, 10, 10));

        root.setCenter(effects);
        root.setLeft(vBoxBinds);
        root.setBackground(bg);
        root.setBorder(border);
        root.setBottom(hBoxBottom);
        root.setRight(vBoxRight);
        root.setPadding(new Insets(20, 20, 20, 20));

        this.addEventFilter(KeyEvent.ANY, rebinds::keyBind);

        setDefault.setOnAction(e -> {
            DefaultValues d = new DefaultValues();
            rebinds.setDefault();
            bgColor.setValue(Color.web(d.getBgColor()));
            spriteColor.setValue(Color.web(d.getSpriteColor()));
            planeColor.setValue(Color.web(d.getPlaneColor()));
            bothColor.setValue(Color.web(d.getBothColor()));
            printConsole.setSelected(d.isPrintToConsole());
            disableUiUpdates.setSelected(d.isDisableUiUpdates());
            printableSymbol.setText(d.getPrintSymbol());
            roundPixels.setSelected(d.isRoundPixels());
            blurEnabled.setSelected(d.isBlur());
            glowEnabled.setSelected(d.isGlow());
            blurSlider.setValue(d.getBlurValue());
            glowSlider.setValue(d.getGlowValue());
            quirkShift.setSelected(d.isQuirkShift());
            quirkJump.setSelected(d.isQuirkJump());
            quirkIncrementIndex.setSelected(d.isQuirkIndex());
        });

        saveChanges.setOnAction(e -> {
            String[] binds = applyKeys(keys, rebinds);
            try {
                KeybindSaver keybindSaver = new KeybindSaver();
                keybindSaver.save(binds);
                ColorSaver colorSaver = new ColorSaver();
                colorSaver.save(bgColor.getValue(), spriteColor.getValue(), planeColor.getValue(), bothColor.getValue());
                ConfigsSaver configsSaver = new ConfigsSaver();
                configsSaver.save(printConsole.isSelected(), printableSymbol.getText(), disableUiUpdates.isSelected(), roundPixels.isSelected(), blurEnabled.isSelected(), glowEnabled.isSelected(), blurSlider.getValue(), glowSlider.getValue());
                QuirkSaver quirkSaver = new QuirkSaver();
                quirkSaver.save(quirkShift.isSelected(), quirkJump.isSelected(), quirkIncrementIndex.isSelected());
            } catch (Exception ignored) {
            }
            // apply it too
            applyChanges.executeAccessibleAction(AccessibleAction.FIRE);
        });

        applyChanges.setOnAction(e -> {
            applyKeys(keys, rebinds);
            applyColor(romDisplay, spriteColor, bgColor, planeColor, bothColor);
            configs.setSpriteColor(romDisplay.getSpriteColor());
            configs.setBgColor(romDisplay.getBgColor());

            configs.setPrintToConsole(printConsole.isSelected());
            configs.setDisableUiUpdates(disableUiUpdates.isSelected());
            configs.setPrintSymbol(printableSymbol.getText());
            configs.setRoundPixels(roundPixels.isSelected());

            effectController.roundPixels(roundPixels.isSelected());

            configs.setBlur(blurEnabled.isSelected());
            configs.setGlow(glowEnabled.isSelected());
            configs.setBlurValue(blurSlider.getValue());
            configs.setGlowValue(glowSlider.getValue());

            configs.setQuirkJump(quirkJump.isSelected());
            configs.setQuirkShift(quirkShift.isSelected());
            configs.setQuirkIncrementIndex(quirkIncrementIndex.isSelected());
        });

        this.setScene(new Scene(root, 700, 340));
        this.show();

        AnimationTimer liveChanges = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (glowEnabled.isSelected() && blurEnabled.isSelected()) {
                    effectController.applyBlurGlow(blurSlider.getValue(), glowSlider.getValue());
                } else if (glowEnabled.isSelected()) {
                    effectController.applyGlow(glowSlider.getValue());
                } else if (blurEnabled.isSelected()) {
                    effectController.applyBlur(blurSlider.getValue());
                } else {
                    effectController.removeEffects();
                }
                effectController.roundPixels(roundPixels.isSelected());
                applyColor(romDisplay, spriteColor, bgColor, planeColor, bothColor);
            }
        };

        this.setOnCloseRequest(windowEvent -> {
            // this is to restore settings if they were not applied / saved
            liveChanges.stop();
            effectController.removeEffects();
            if (configs.isGlow() && configs.isBlur()) {
                effectController.applyBlurGlow(configs.getBlurValue(), configs.getGlowValue());
            } else if (configs.isGlow()) {
                effectController.applyGlow(configs.getGlowValue());
            } else if (configs.isBlur()) {
                effectController.applyBlur(configs.getBlurValue());
            }
            effectController.roundPixels(configs.isRoundPixels());
            if (configs.getBgColor() != romDisplay.getBgColor()) {
                romDisplay.setBgColor(configs.getBgColor());
            }
            if (configs.getSpriteColor() != romDisplay.getSpriteColor()) {
                romDisplay.setSpriteColor(configs.getSpriteColor());
            }
        });

        liveChanges.start();
    }

    /**
     * sets colors to the rom display window in the emulator
     * main window
     *
     * @param romDisplay  display to set colors to
     * @param spriteColor color for sprite
     * @param bgColor     color for background
     * @param planeColor  color for xo-chip plane
     * @param bothColor   color for xo-chip overlap color
     */
    private void applyColor(RomDisplay romDisplay, ColorPicker spriteColor, ColorPicker bgColor, ColorPicker planeColor, ColorPicker bothColor) {
        romDisplay.setSpriteColor(spriteColor.getValue().toString());
        romDisplay.setBgColor(bgColor.getValue().toString());
        romDisplay.setPlaneColor(planeColor.getValue().toString());
        romDisplay.setBothColor(bothColor.getValue().toString());
    }

    /**
     * sets the rebinds to the emulator
     *
     * @param keys    keys used by the emulator
     * @param rebinds class for keybind handling
     * @return
     */
    private String[] applyKeys(Keys keys, Rebinds rebinds) {
        String[] binds = new String[16];
        for (int i = 0; i < 16; i++) {
            binds[i] = rebinds.getTButtons().get(i).getText();
        }
        keys.setBinds(binds);
        return binds;
    }


}