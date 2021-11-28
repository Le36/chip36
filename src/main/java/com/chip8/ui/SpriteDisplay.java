package com.chip8.ui;

import com.chip8.emulator.PixelManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpriteDisplay extends Canvas {

    private PixelManager pixels;
    private GraphicsContext painter;

    public SpriteDisplay(PixelManager pixels) {
        super(80, 160);
        this.pixels = pixels;
        this.painter = this.getGraphicsContext2D();
    }

    public void draw() {
        painter.setFill(Color.BLACK);
        painter.fillRect(0, 0, 80, 160);
        boolean[][] spriteViewer = pixels.getSpriteViewer();
        painter.setFill(Color.rgb(35, 255, 0));
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 8; y++) {
                if (spriteViewer[y][x]) {
                    painter.fillRect(y * 10, x * 10, 10, 10);
                }
            }
        }
    }
}
