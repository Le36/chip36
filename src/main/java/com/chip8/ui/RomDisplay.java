package com.chip8.ui;

import com.chip8.configs.ColorSaver;
import com.chip8.emulator.PixelManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class RomDisplay extends Canvas {

    private PixelManager pixels;
    private GraphicsContext painter;
    private int width;
    private int height;
    private boolean fadeSelected;
    private int scale;
    private String bgColor;
    private String spriteColor;

    public RomDisplay(PixelManager pixels, int width, int height) {
        super(width, height);
        this.pixels = pixels;
        this.painter = this.getGraphicsContext2D();
        this.width = width;
        this.height = height;
        this.scale = width / 64;
        try {
            ColorSaver cs = new ColorSaver();
            this.spriteColor = cs.loadColor("SPRITE COLOR");
            this.bgColor = cs.loadColor("BG COLOR");
        } catch (Exception ignored) {
            this.spriteColor = "0xFFFFFF";
            this.bgColor = "0x000000";
        }
    }

    public void draw() {
        painter.setFill(Color.web(bgColor));
        painter.fillRect(0, 0, width, height);
        if (fadeSelected) {
            this.drawFading();
        }
        boolean[][] display = pixels.getDisplay();
        painter.setFill(Color.web(spriteColor));
        for (int x = 0; x < height / scale; x++) {
            for (int y = 0; y < width / scale; y++) {
                if (display[y][x]) {
                    painter.fillRect(y * scale, x * scale, scale, scale);
                }
            }
        }
    }

    private void drawFading() {
        HashMap<Integer, HashMap<Integer, Double>> fadeMap = pixels.getFadeMap();
        for (int x = 0; x < fadeMap.size(); x++) {
            for (int y = 0; y < fadeMap.get(x).size(); y++) {
                if (fadeMap.get(x).get(y) > 0.0) {
                    double fading = Math.min(0.95, fadeMap.get(x).get(y));

                    Color color = Color.web(spriteColor, fading);

                    painter.setFill(color);
                    painter.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
    }

    public void setFadeSelected(boolean fadeSelected) {
        this.fadeSelected = fadeSelected;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setSpriteColor(String spriteColor) {
        this.spriteColor = spriteColor;
    }
}