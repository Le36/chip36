package com.chip8.ui;

import com.chip8.configs.DefaultValues;
import com.chip8.emulator.Keys;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * base used for clickable keyboard in ui and also
 * for the rebinding possibility in options menu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyboardPane extends GridPane {

    private final ArrayList<Button> buttons;
    private final ArrayList<ToggleButton> tButtons;
    private final Keys keys;
    private boolean state;

    /**
     * @param keys  keys used by the emulator
     * @param state true = clickable keyboard, false = rebindable keyboard
     */
    public KeyboardPane(Keys keys, boolean state) {
        this.buttons = new ArrayList<>();
        this.tButtons = new ArrayList<>();
        this.keys = keys;
        this.state = state;
        addButtonsToList();
        addButtonsToPane();
        this.setHgap(5);
        this.setVgap(5);
        this.setAlignment(Pos.CENTER);
    }

    private void addButtonsToPane() {
        for (int i = 0, first = 0, second = 0; i < 16; i++) {
            if (state) {
                this.add(buttons.get(i), first, second);
            } else {
                this.add(tButtons.get(i), first, second);
            }
            first++;
            if (first == 4) {
                second++;
                first = 0;
            }
        }
    }

    private void addButtonsToList() {
        for (int i = 0; i < 16; i++) {
            if (state) {
                String layout = new DefaultValues().getMouseKbLayout();
                buttons.add(new UiElements().makeButton(layout.substring(i, i + 1)));
            } else {
                tButtons.add(new UiElements().makeToggleButton(keys.getBinds()[i]));
            }
        }
    }
}