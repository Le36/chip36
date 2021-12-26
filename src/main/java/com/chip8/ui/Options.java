package com.chip8.ui;

import com.chip8.configs.*;
import com.chip8.emulator.Keys;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAction;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
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
        VBox vBoxBinds = new VBox(10, uiElements.makeLabel("Rebind your keys: ", LabelType.LABEL), rebinds);

        ColorPicker spriteColor = uiElements.colorPicker();
        ColorPicker bgColor = uiElements.colorPicker();

        spriteColor.setValue(Color.web(romDisplay.getSpriteColor()));
        bgColor.setValue(Color.web(romDisplay.getBgColor()));
        // colors so we can restore them if not applied / saved
        configs.setSpriteColor(romDisplay.getSpriteColor());
        configs.setBgColor(romDisplay.getBgColor());

        CheckBox printConsole = uiElements.makeCheckBox("Print to console");
        CheckBox disableUiUpdates = uiElements.makeCheckBox("Disable ui updates");
        printConsole.setSelected(configs.isPrintToConsole());
        disableUiUpdates.setSelected(configs.isDisableUiUpdates());
        TextField printableSymbol = uiElements.makeTextField();
        printableSymbol.setText(configs.getPrintSymbol());

        VBox vBoxRight = new VBox(10, uiElements.makeLabel("Sprite color:", LabelType.TOOLBAR), spriteColor,
                uiElements.makeLabel("Bg color:", LabelType.TOOLBAR), bgColor, printConsole,
                new HBox(5, uiElements.makeLabel("Print symbol:", LabelType.SMALL), printableSymbol), disableUiUpdates);

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

        VBox effects = new VBox(10, blurLabel, blurSlider, blurEnabled, glowLabel, glowSlider, glowEnabled, roundPixels);
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
            printConsole.setSelected(d.isPrintToConsole());
            disableUiUpdates.setSelected(d.isDisableUiUpdates());
            printableSymbol.setText(d.getPrintSymbol());
            roundPixels.setSelected(d.isRoundPixels());
            blurEnabled.setSelected(d.isBlur());
            glowEnabled.setSelected(d.isGlow());
            blurSlider.setValue(d.getBlurValue());
            glowSlider.setValue(d.getGlowValue());
        });

        saveChanges.setOnAction(e -> {
            String[] binds = applyKeys(keys, rebinds);
            try {
                KeybindSaver keybindSaver = new KeybindSaver();
                keybindSaver.save(binds);
                ColorSaver colorSaver = new ColorSaver();
                colorSaver.save(bgColor.getValue(), spriteColor.getValue());
                ConfigsSaver configsSaver = new ConfigsSaver();
                configsSaver.save(printConsole.isSelected(), printableSymbol.getText(), disableUiUpdates.isSelected(), roundPixels.isSelected(), blurEnabled.isSelected(), glowEnabled.isSelected(), blurSlider.getValue(), glowSlider.getValue());
            } catch (Exception ignored) {
            }
            // apply it too
            applyChanges.executeAccessibleAction(AccessibleAction.FIRE);
        });

        applyChanges.setOnAction(e -> {
            applyKeys(keys, rebinds);
            applyColor(romDisplay, spriteColor, bgColor);
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
        });

        this.setScene(new Scene(root, 700, 310));
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
                applyColor(romDisplay, spriteColor, bgColor);
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
     */
    private void applyColor(RomDisplay romDisplay, ColorPicker spriteColor, ColorPicker bgColor) {
        romDisplay.setSpriteColor(spriteColor.getValue().toString());
        romDisplay.setBgColor(bgColor.getValue().toString());
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