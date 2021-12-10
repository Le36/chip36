package com.chip8.ui;

import com.chip8.emulator.Keys;

public class Keyboard extends KeyboardPane {

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