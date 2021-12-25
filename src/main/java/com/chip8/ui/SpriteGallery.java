package com.chip8.ui;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * collects sprites found in rom and sorts them in listview
 */
public class SpriteGallery extends ListView {

    private int galleryIndex;
    private SpriteDisplay sd;

    public SpriteGallery(SpriteDisplay sd) {
        super.getStylesheets().add("disassembler.css");
        this.sd = sd;
    }

    /**
     * adds found sprites to gallery
     */
    public void update() {
        if (sd.getGallery().size() != galleryIndex) {
            Pair<boolean[][], Integer> p = sd.getGallery().get(galleryIndex);
            this.getItems().add(this.drawSprite(p.getKey(), p.getValue()));
            galleryIndex++;
        }
    }

    private Canvas drawSprite(boolean[][] spriteArray, int height) {
        Canvas canvas = new Canvas(40, height * 5);
        GraphicsContext painter = canvas.getGraphicsContext2D();
        if (galleryIndex % 2 == 1) {
            painter.setFill(Color.BLACK);
        } else {
            painter.setFill(Color.rgb(21, 21, 21));
        }
        painter.fillRect(0, 0, 40, height * 5);
        painter.setFill(Color.rgb(35, 255, 0));
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < 8; y++) {
                if (spriteArray[y][x]) {
                    painter.fillRect(y * 5, x * 5, 5, 5);
                }
            }
        }
        return canvas;
    }
}
