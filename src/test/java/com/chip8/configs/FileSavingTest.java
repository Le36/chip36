package com.chip8.configs;

import com.chip8.emulator.Keys;
import javafx.scene.paint.Color;
import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileSavingTest {

    private KeybindSaver keybindSaver;
    private ColorSaver colorSaver;
    private ConfigsSaver configsSaver;

    @Before
    public void setUp() throws IOException {
        this.keybindSaver = new KeybindSaver();
        this.colorSaver = new ColorSaver();
        this.configsSaver = new ConfigsSaver();

        // create config file with some settings
        Keys keys = new Keys();
        this.keybindSaver.save(keys.getBinds());
        this.colorSaver.save(Color.BLACK, Color.WHITE);
        this.configsSaver.save(false, "*", false);
    }

    @AfterClass
    public static void clearFile() {
        System.gc();
        File file = new File("chip8-configs.txt");
        file.delete();
        file.deleteOnExit();
    }

    @Test
    public void KeybindLoad() throws FileNotFoundException {
        Keys keys = new Keys();
        assertArrayEquals(keys.getBinds(), keybindSaver.load());
    }

    @Test
    public void colorLoad() throws FileNotFoundException {
        assertEquals("0x000000ff", colorSaver.loadColor("bgColor:"));
        assertEquals("0xffffffff", colorSaver.loadColor("spriteColor:"));
    }

    @Test
    public void configLoad() throws FileNotFoundException {
        assertFalse(configsSaver.loadState("printToConsole:"));
        assertFalse(configsSaver.loadState("disableUiUpdates:"));
        assertEquals("*", configsSaver.loadSymbol());
    }
}
