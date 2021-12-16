package com.chip8.configs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * enables saving configs to a file
 */
public class ConfigsSaver {

    /**
     * saves configs, appends them to config file after keybindings and colors
     *
     * @param print if printing to console is on
     * @param symbol what symbol to use when printing
     * @param uiUpdates enable or disable ui updates
     * @throws IOException error in file handling
     */
    public void save(boolean print, String symbol, boolean uiUpdates) throws IOException {
        File configFile = new File("chip8-configs.txt");

        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        StringBuilder file = new StringBuilder();
        Scanner sc = new Scanner(configFile);

        for (int i = 0; i < 20; i++) {
            file.append(sc.nextLine()).append("\n");
        }

        String configs = file + "printToConsole:\n" + print + "\nsymbol:\n" + symbol + "\ndisableUiUpdates:\n" + uiUpdates;
        FileWriter fw = new FileWriter("chip8-configs.txt");
        fw.write(configs);
        fw.close();
    }


    /**
     * @return loaded symbol
     * @throws FileNotFoundException if file is missing
     */
    public String loadSymbol() throws FileNotFoundException {
        File configFile = new File("chip8-configs.txt");
        Scanner sc = new Scanner(configFile);
        String symbol = "";

        while (sc.hasNextLine()) {
            if (sc.nextLine().equals("symbol:")) {
                symbol = sc.nextLine();
            }
        }
        sc.close();
        return symbol;
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
