package com.chip8.configs;

import lombok.Data;

/**
 * some configurable settings for emulator and ui
 */
@Data
public class Configs {

    private boolean printToConsole;
    private boolean disableUiUpdates;
    private String printSymbol;

    public Configs() {
        try {
            ConfigsSaver configsSaver = new ConfigsSaver();
            this.printToConsole = configsSaver.loadState("printToConsole:");
            this.disableUiUpdates = configsSaver.loadState("disableUiUpdates:");
            this.printSymbol = configsSaver.loadSymbol();
        } catch (Exception ignored) {
            this.printToConsole = false;
            this.disableUiUpdates = false;
            this.printSymbol = "█#";
        }
    }

}
