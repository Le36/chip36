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

    /**
     * @param width screen width
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
        this.spriteViewer = new boolean[8][16];
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
        for (int hei = 0; hei < 32; hei++) {
            for (int wid = 0; wid < 64; wid++) {
                if (this.display[wid][hei]) {
                    this.draw(wid, hei);
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                this.spriteViewer[i][j] = false;
            }
        }
    }
}
