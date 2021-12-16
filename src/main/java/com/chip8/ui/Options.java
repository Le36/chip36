package com.chip8.ui;

import com.chip8.configs.ColorSaver;
import com.chip8.configs.Configs;
import com.chip8.configs.KeybindSaver;
import com.chip8.emulator.Keys;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
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
        ColorSaver cs = new ColorSaver();

        try {
            spriteColor.setValue(Color.web(cs.loadColor("SPRITE COLOR")));
            bgColor.setValue(Color.web(cs.loadColor("BG COLOR")));
        } catch (Exception ignored) {
            spriteColor.setValue(Color.web(romDisplay.getSpriteColor()));
            bgColor.setValue(Color.web(romDisplay.getBgColor()));
        }

        CheckBox printConsole = uiElements.makeCheckBox("Print to console");
        CheckBox disableRomDisplay = uiElements.makeCheckBox("Disable ui updates");
        printConsole.setSelected(configs.isPrintToConsole());
        disableRomDisplay.setSelected(configs.isDisableUiUpdates());
        VBox vBoxRight = new VBox(10, uiElements.makeLabel("Sprite color:", LabelType.TOOLBAR), spriteColor, uiElements.makeLabel("Bg color:", LabelType.TOOLBAR), bgColor, printConsole, disableRomDisplay);

        root.setLeft(vBoxBinds);
        root.setBackground(bg);
        root.setBorder(border);
        root.setBottom(hBoxBottom);
        root.setRight(vBoxRight);
        root.setPadding(new Insets(20, 20, 20, 20));

        this.addEventFilter(KeyEvent.ANY, rebinds::keyBind);

        setDefault.setOnAction(e -> {
            rebinds.setDefault();
            bgColor.setValue(Color.web("0x000000"));
            spriteColor.setValue(Color.web("0xFFFFFF"));
        });

        saveChanges.setOnAction(e -> {
            String[] binds = applyKeys(keys, rebinds);
            try {
                KeybindSaver keybindSaver = new KeybindSaver();
                keybindSaver.save(binds);
                ColorSaver colorSaver = new ColorSaver();
                colorSaver.save(bgColor.getValue(), spriteColor.getValue());
            } catch (Exception ignored) {
            }
            applyColor(romDisplay, spriteColor, bgColor);
            configs.setPrintToConsole(printConsole.isSelected());
            configs.setDisableUiUpdates(disableRomDisplay.isSelected());
        });

        applyChanges.setOnAction(e -> {
            applyKeys(keys, rebinds);
            applyColor(romDisplay, spriteColor, bgColor);
            configs.setPrintToConsole(printConsole.isSelected());
            configs.setDisableUiUpdates(disableRomDisplay.isSelected());
        });

        this.setScene(new Scene(root, 550, 250));
        this.show();
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