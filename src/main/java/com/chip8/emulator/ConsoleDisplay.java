package com.chip8.emulator;

import lombok.Data;

/**
 * allows printing of the emulator display to console
 */
@Data
public class ConsoleDisplay {

    private boolean[][] display;
    private int width = 64;
    private int height = 32;

    public ConsoleDisplay() {
        this.display = new boolean[width][height];
    }

    /**
     * print rom display to console
     */
    public void printDisplay(String symbol) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (this.display[j][i]) {
                    System.out.print(symbol);
                } else {
                    for (int n = 0; n < symbol.length(); n++) {
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * clears the display
     */
    public void clearDisplay() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.display[i][j] = false;
            }
        }
    }

    public void drawPixel(int i, int j) {
        this.display[i][j] = !this.display[i][j];
    }

    public boolean getPixel(int i, int j) {
        return this.display[i][j];
    }
}
