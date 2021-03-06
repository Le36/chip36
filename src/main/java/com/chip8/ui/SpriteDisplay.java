package com.chip8.ui;

import com.chip8.configs.Configs;
import com.chip8.emulator.PixelManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * creates the display where current sprite is displayed
 */
@Data
public class SpriteDisplay extends Canvas {

    private PixelManager pixels;
    private GraphicsContext painter;
    private ArrayList<Pair<boolean[][], Integer>> gallery;
    private HashSet<Integer> galleryHashes;
    private Configs configs;

    public SpriteDisplay(PixelManager pixels, Configs configs) {
        super(80, 160);
        this.pixels = pixels;
        this.painter = this.getGraphicsContext2D();
        this.configs = configs;
        this.gallery = new ArrayList<>();
        this.galleryHashes = new HashSet<>();
    }

    /**
     * calling this will render the sprite display
     * also adds sprite to gallery if extracting
     */
    public void draw() {
        painter.setFill(Color.BLACK);
        painter.fillRect(0, 0, 80, 160);
        boolean[][] spriteViewer = pixels.getSpriteViewer();
        int spriteHeight = pixels.getSpriteHeight();
        painter.setFill(Color.rgb(35, 255, 0));

        // check if 16x16 sprite or normal
        int ylim = spriteHeight == -1 ? 16 : 8;
        int sc = spriteHeight == -1 ? 5 : 10;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < ylim; y++) {
                if (spriteViewer[y][x]) {
                    painter.fillRect(y * sc, x * sc, sc, sc);
                }
            }
        }

        if (configs.isSpriteExtracting()) {
            extractSprite(spriteViewer, spriteHeight);
        }
    }

    private void extractSprite(boolean[][] spriteViewer, int spriteHeight) {
        if (galleryHashes.add(Arrays.deepHashCode(spriteViewer))) {
            // deep copying, check if 16x16 or normal sprite
            boolean[][] temp = spriteHeight == -1 ? new boolean[16][16] : new boolean[8][spriteHeight];
            for (int i = 0; i < (spriteHeight == -1 ? 16 : 8); i++) {
                for (int j = 0; j < (spriteHeight == -1 ? 16 : spriteHeight); j++) {
                    temp[i][j] = spriteViewer[i][j];
                }
            }
            gallery.add(new Pair(temp, spriteHeight));
        }
    }
}
