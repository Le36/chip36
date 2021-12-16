package com.chip8.configs;

import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * enables saving set keybinds to a file
 */
@Data
public class KeybindSaver {

    /**
     * @param binds array of binds for keyboard
     * @throws IOException if error occurs in format
     */
    public void save(String[] binds) throws IOException {
        File configFile = new File("chip8-configs.txt");

        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        StringBuilder keybinds = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            keybinds.append(binds[i]).append("\n");
        }
        FileWriter fw = new FileWriter("chip8-configs.txt");
        fw.write(keybinds.toString());
        fw.close();
    }

    /**
     * loads keybinds from the file
     *
     * @return loaded binds in string array
     * @throws FileNotFoundException if file does not exist
     */
    public String[] load() throws FileNotFoundException {
        File configFile = new File("chip8-configs.txt");

        Scanner sc = new Scanner(configFile);
        String[] binds = new String[16];

        for (int i = 0; i < 16; i++) {
            binds[i] = sc.nextLine();
        }
        sc.close();
        return binds;
    }
}
