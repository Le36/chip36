package com.chip8.emulator;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Data;

/**
 * handles key presses for emulator, gets pressed keys from ui
 */
@Data
public class Keys {

    private boolean[] keys = new boolean[16];

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
     * @param key the key that is being pressed or released
     */
    private void setKey(boolean state, KeyCode key) {
        switch (key) {
            case DIGIT1:
                keys[1] = state;
                break;
            case DIGIT2:
                keys[2] = state;
                break;
            case DIGIT3:
                keys[3] = state;
                break;
            case DIGIT4:
                keys[0xC] = state;
                break;
            case Q:
                keys[4] = state;
                break;
            case W:
                keys[5] = state;
                break;
            case E:
                keys[6] = state;
                break;
            case R:
                keys[0xD] = state;
                break;
            case A:
                keys[7] = state;
                break;
            case S:
                keys[8] = state;
                break;
            case D:
                keys[9] = state;
                break;
            case F:
                keys[0xE] = state;
                break;
            case Z:
                keys[0xA] = state;
                break;
            case X:
                keys[0] = state;
                break;
            case C:
                keys[0xB] = state;
                break;
            case V:
                keys[0xF] = state;
                break;
        }
    }
}
