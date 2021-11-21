package com.chip8.emulator;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Data;

@Data
public class Keys {

    private boolean[] keys = new boolean[16];

    public void setKey(KeyEvent event) {
        if (event.getEventType().getName().equals("KEY_PRESSED")) {
            this.setKey(true, event.getCode());
            System.out.println(event.toString());
        }
        if (event.getEventType().getName().equals("KEY_RELEASED")) {
            this.setKey(false, event.getCode());
            System.out.println(event.toString());
        }
    }

    private void setKey(boolean state, KeyCode key) {
        switch (key) {
            case DIGIT1:
                keys[0] = state;
                break;
            case DIGIT2:
                keys[1] = state;
                break;
            case DIGIT3:
                keys[2] = state;
                break;
            case DIGIT4:
                keys[3] = state;
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
                keys[7] = state;
                break;
            case A:
                keys[8] = state;
                break;
            case S:
                keys[9] = state;
                break;
            case D:
                keys[10] = state;
                break;
            case F:
                keys[11] = state;
                break;
            case Z:
                keys[12] = state;
                break;
            case X:
                keys[13] = state;
                break;
            case C:
                keys[14] = state;
                break;
            case V:
                keys[15] = state;
                break;
        }
    }
}
