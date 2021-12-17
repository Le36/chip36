package com.chip8.emulator;

import com.chip8.configs.DefaultValues;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KeysTest {

    private Keys keys;

    @Before
    public void setUp() {
        this.keys = new Keys();
    }

    @Test
    public void keyPresses() {
        // press all default keys and then release them
        // check that states are correct for each key
        DefaultValues d = new DefaultValues();
        String keyIndex = d.getMouseKbLayout();
        int i = 0;
        for (char c : d.getRebindLayout().toCharArray()) {
            String s = String.valueOf(c);
            if (s.matches("[0-9]")) {
                s = "DIGIT" + s;
            }
            // press key
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, s, s, KeyCode.valueOf(s), false, false, false, false);
            keys.setKey(event);
            Integer indexPointer = Integer.valueOf(keyIndex.substring(i, i + 1), 16);
            assertTrue(keys.getKeys()[indexPointer]);

            // release key
            event = new KeyEvent(KeyEvent.KEY_RELEASED, s, s, KeyCode.valueOf(s), false, false, false, false);
            keys.setKey(event);
            assertFalse(keys.getKeys()[indexPointer]);
            i++;
        }
    }

    @Test
    public void unbindedKey() {
        // check that nothing is pressed when key that is not bound
        // is pressed, here simulating pressing key "enter"
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "Enter", "Enter", KeyCode.ENTER, false, false, false, false);
        keys.setKey(event);
        for (boolean b : keys.getKeys()) {
            assertFalse(b);
        }
    }
}
