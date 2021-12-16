package com.chip8.configs;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * enables saving selected colors to a file
 */
public class ColorSaver {

    /**
     * saves colors to a file, uses keybinds in same file
     *
     * @param bg color of background
     * @param sprite color of sprite
     * @throws IOException exception if found file in wrong format
     */
    public void save(Color bg, Color sprite) throws IOException {
        File configFile = new File("chip8-configs.txt");

        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        StringBuilder file = new StringBuilder();
        Scanner sc = new Scanner(configFile);

        for (int i = 0; i < 16; i++) {
            file.append(sc.nextLine()).append("\n");
        }

        String colors = file + "bgColor:\n" + bg.toString() + "\nspriteColor:\n" + sprite.toString();
        FileWriter fw = new FileWriter("chip8-configs.txt");
        fw.write(colors);
        fw.close();
    }

    /**
     * @param color bg or sprite color selection
     * @return returns the loaded color in Color.web(hex)
     * @throws FileNotFoundException error if file does not exist
     */
    public String loadColor(String color) throws FileNotFoundException {
        File configFile = new File("chip8-configs.txt");
        Scanner sc = new Scanner(configFile);

        while (sc.hasNextLine()) {
            if (sc.nextLine().equals(color)) {
                color = sc.nextLine();
            }
        }
        sc.close();
        if (!color.matches("0x[0-9A-Fa-f]{8}")) {
            throw new FileNotFoundException();
        }
        return color;
    }
}
