package com.chip8.emulator;

import lombok.Data;

import java.util.HashMap;

/**
 * manages pixels for jfx ui
 */
@Data
public class PixelManager {

    private HashMap<Integer, HashMap<Integer, Double>> fadeMap;
    private boolean[][] display;
    private int x = 0;
    private int y = 0;
    private boolean fade;
    private double fadeSpeed;
    private boolean[][] spriteViewer;
    private int spriteHeight;
    private boolean resolutionMode; // true = hires, false = lores

    /**
     * @param width  screen width
     * @param height screen height
     */
    public PixelManager(int width, int height) {
        this.fadeMap = new HashMap<>();
        this.fade = true;
        this.fadeSpeed = 0.1;
        for (int x = 0; x < width; x++) {
            this.fadeMap.putIfAbsent(x, new HashMap<>());
            for (int y = 0; y < height; y++) {
                this.fadeMap.get(x).put(y, 0.0);
            }
        }
        this.display = new boolean[width][height];
        this.spriteViewer = new boolean[16][16];
        this.spriteHeight = 0;
    }

    /**
     * generate fade for pixels that are turned off
     * fade is used for smoother look and to get rid of stutter and flicker
     */
    public void fade() {
        for (int x = 0; x < this.fadeMap.size(); x++) {
            for (int y = 0; y < this.fadeMap.get(x).size(); y++) {
                double d = this.fadeMap.get(x).get(y);
                if (d > 0) {
                    d -= fadeSpeed; // basically how fast the fade is
                }
                this.fadeMap.get(x).put(y, d);
            }
        }
    }

    /**
     * if the display is going to be erased, instead of erasing it right away
     * we are going to add it to a fade map, that lets the pixel fade out slowly
     *
     * @param x coordinate x
     * @param y coordinate y
     */
    public void draw(int x, int y) {
        if (this.display[x][y] && fade) {
            this.x = x;
            this.y = y;
            this.fadeMap.get(this.x).put(this.y, 0.95);
        }
        this.display[x][y] = !this.display[x][y];
    }

    /**
     * clears display
     */
    public void clearDisplay() {
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 64; y++) {
                if (this.display[x][y]) {
                    this.draw(x, y);
                }
            }
        }
    }

    /**
     * draws sprite on sprite display
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void drawSprite(int x, int y) {
        this.spriteViewer[x][y] = true;
    }

    /**
     * clears sprite display
     */
    public void clearSprite() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                this.spriteViewer[x][y] = false;
            }
        }
    }

    /**
     * @param x x coordinate
     * @param y y coordinate
     * @return state of that pixel
     */
    public boolean getPixel(int x, int y) {
        return this.display[x][y];
    }

    /**
     * print rom display to console according to resolution mode
     *
     * @param symbol symbol that is used to print rom to console
     */
    public void printDisplay(String symbol) {
        int xlim = resolutionMode ? 128 : 64;
        int ylim = resolutionMode ? 64 : 32;
        for (int y = 0; y < ylim; y++) {
            for (int x = 0; x < xlim; x++) {
                if (this.display[x][y]) {
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

    private void drawScrolling(int x, int y, int amount, Scroll dir) {
        if (this.display[x][y] && fade) {
            this.fadeMap.get(x).put(y, 0.95);
        }
        switch (dir) {
            case DOWN:
                this.display[x][y] = display[x][y - amount];
                return;
            case LEFT:
                this.display[x][y] = display[x + amount][y];
                return;
            case RIGHT:
                this.display[x][y] = display[x - amount][y];
                return;
            case UP:
                this.display[x][y] = display[x][y + amount];
        }
    }

    private void erase(int x, int y) {
        if (display[x][y]) {
            this.draw(x, y);
        }
    }

    /**
     * scrolls down screen in for a given amount
     *
     * @param amount how many pixels to scroll down to
     */
    public void scrollDown(int amount) {
        amount = scrollBy(amount);
        // draw screen bottom to top
        for (int y = 63; y >= amount; y--) {
            for (int x = 0; x < 128; x++) {
                drawScrolling(x, y, amount, Scroll.DOWN);
            }
        }

        // now clear top side
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < amount; y++) {
                erase(x, y);
            }
        }
    }

    public void scrollRight() {
        int amount = scrollBy(4);

        for (int y = 0; y < 64; y++) {
            for (int x = 127; x >= amount; x--) {
                drawScrolling(x, y, amount, Scroll.RIGHT);
            }
        }

        for (int x = 0; x < amount; x++) {
            for (int y = 0; y < 64; y++) {
                erase(x, y);
            }
        }
    }

    public void scrollLeft() {
        int amount = scrollBy(4);

        for (int y = 63; y >= 0; y--) {
            for (int x = 0; x < 127 - amount + 1; x++) {
                drawScrolling(x, y, amount, Scroll.LEFT);
            }
        }

        for (int x = 128 - amount; x < 128; x++) {
            for (int y = 0; y < 64; y++) {
                erase(x, y);
            }
        }
    }

    private int scrollBy(int amount) {
        return resolutionMode ? amount : amount / 2;
    }
}
