package com.chip8.configs;

import lombok.Data;

/**
 * default values for configurable items
 */
@Data
public class DefaultValues {

    private String rebindLayout;
    private String mouseKbLayout;
    private String bgColor;
    private String spriteColor;
    private String printSymbol;
    private boolean printToConsole;
    private boolean disableUiUpdates;
    private boolean roundPixels;
    private boolean blur;
    private boolean glow;
    private double blurValue;
    private double glowValue;

    /**
     * edit these if you want to change some default values
     */
    public DefaultValues() {
        this.rebindLayout = "1234QWERASDFZXCV";
        this.mouseKbLayout = "123C456D789EA0BF";
        this.bgColor = "0x000000";
        this.spriteColor = "0xFFFFFF";
        this.printSymbol = "█#";
        this.printToConsole = false;
        this.disableUiUpdates = false;
        this.roundPixels = false;
        this.blur = false;
        this.glow = false;
        this.blurValue = 0.0;
        this.glowValue = 0.0;
    }
}
