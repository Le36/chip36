package com.chip8.configs;

import lombok.Data;

/**
 * some configurable settings for emulator and ui
 */
@Data
public class Configs {

    private boolean printToConsole;
    private boolean disableUiUpdates;

    public Configs() {
        printToConsole = false;
        disableUiUpdates = false;
    }

}
