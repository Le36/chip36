package com.chip8.display;

import lombok.Data;

@Data
public class ConsoleDisplay {

    private boolean[][] display;
    private int width = 64;
    private int height = 32;

    public ConsoleDisplay() {
        this.display = new boolean[width][height];
    }

    public void printDisplay() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (this.display[j][i]) {
                    System.out.print("X");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

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
