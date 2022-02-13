package com.chip8.configs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * allows saving quirks to a file
 */
public class QuirkSaver {

    /**
     * saves quirk states to file
     *
     * @param shift if quirk is used for shifting instructions
     * @param jump  if quirk is used for jump with offset instruction
     * @param index if quirk is used for dump and fill instructions
     * @throws IOException
     */
    public void save(boolean shift, boolean jump, boolean index) throws IOException {
        File configFile = new File("chip8-configs.txt");

        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        StringBuilder file = new StringBuilder();
        Scanner sc = new Scanner(configFile);

        for (int i = 0; i < 40; i++) {
            file.append(sc.nextLine()).append("\n");
        }

        String configs = file + "quirkShift:\n" + shift + "\nquirkJump:\n" + jump + "\nquirkIndex:\n" + index;
        FileWriter fw = new FileWriter("chip8-configs.txt");
        fw.write(configs);
        fw.close();
    }

    /**
     * @param state what to load from file
     * @return returns loaded state
     * @throws FileNotFoundException if file missing
     */
    public boolean loadState(String state) throws FileNotFoundException {
        File configFile = new File("chip8-configs.txt");
        Scanner sc = new Scanner(configFile);
        boolean getter = false;

        while (sc.hasNextLine()) {
            if (sc.nextLine().equals(state)) {
                getter = Boolean.parseBoolean(sc.nextLine());
            }
        }
        sc.close();
        return getter;
    }
}
