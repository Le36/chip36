package com.chip8.emulator;

import com.chip8.configs.DefaultValues;
import com.chip8.configs.KeybindSaver;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Data;

/**
 * handles key presses for emulator, gets pressed keys from ui
 */
@Data
public class Keys {

    private boolean[] keys = new boolean[16];
    private String[] binds = new String[16];

    /**
     * initialize default keys in 4*4 keypad
     */
    public Keys() {
        try {
            KeybindSaver kb = new KeybindSaver();
            binds = kb.load();
        } catch (Exception ignored) {
            for (int i = 0; i < 16; i++) {
                String layout = new DefaultValues().getRebindLayout();
                binds[i] = layout.substring(i, i + 1);
            }
        }
    }

    /**
     * checks if the key is pressed or released and sets that key to correct state
     *
     * @param event keyEvent from javafx stage
     */
    public void setKey(KeyEvent event) {
        if (event.getEventType().getName().equals("KEY_PRESSED")) {
            this.setKey(true, event.getCode());
        }
        if (event.getEventType().getName().equals("KEY_RELEASED")) {
            this.setKey(false, event.getCode());
        }
    }

    /**
     * @param state is the key pressed or not
     * @param key   the key that is being pressed or released
     */
    private void setKey(boolean state, KeyCode key) {
        if (binds[0].equals(key.getName())) {
            keys[1] = state;
        } else if (binds[1].equals(key.getName())) {
            keys[2] = state;
        } else if (binds[2].equals(key.getName())) {
            keys[3] = state;
        } else if (binds[3].equals(key.getName())) {
            keys[0xC] = state;
        } else if (binds[4].equals(key.getName())) {
            keys[4] = state;
        } else if (binds[5].equals(key.getName())) {
            keys[5] = state;
        } else if (binds[6].equals(key.getName())) {
            keys[6] = state;
        } else if (binds[7].equals(key.getName())) {
            keys[0xD] = state;
        } else if (binds[8].equals(key.getName())) {
            keys[7] = state;
        } else if (binds[9].equals(key.getName())) {
            keys[8] = state;
        } else if (binds[10].equals(key.getName())) {
            keys[9] = state;
        } else if (binds[11].equals(key.getName())) {
            keys[0xE] = state;
        } else if (binds[12].equals(key.getName())) {
            keys[0xA] = state;
        } else if (binds[13].equals(key.getName())) {
            keys[0] = state;
        } else if (binds[14].equals(key.getName())) {
            keys[0xB] = state;
        } else if (binds[15].equals(key.getName())) {
            keys[0xF] = state;
        }
    }
}