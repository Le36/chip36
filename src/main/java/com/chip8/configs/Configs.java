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
            DefaultValues d = new DefaultValues();
            this.printToConsole = d.isPrintToConsole();
            this.disableUiUpdates = d.isDisableUiUpdates();
            this.printSymbol = d.getPrintSymbol();
        }
    }

}
