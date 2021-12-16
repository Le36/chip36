package com.chip8.ui;

import com.chip8.emulator.Keys;

/**
 * creates clickable keyboard for emulator ui
 */
public class Keyboard extends KeyboardPane {

    /**
     * creates the keyboard
     *
     * @param keys keys used by emulator
     */
    public Keyboard(Keys keys) {
        super(keys, true);
        this.keyboardEvents();
    }

    private void keyboardEvents() {
        for (int i = 0; i < 16; i++) {
            int pressedKey = Integer.parseInt(super.getButtons().get(i).getText(), 16);
            super.getButtons().get(i).setOnMousePressed(e -> {
                super.getKeys().getKeys()[pressedKey] = true;
            });
            super.getButtons().get(i).setOnMouseReleased(e -> {
                super.getKeys().getKeys()[pressedKey] = false;
            });
        }
    }
}