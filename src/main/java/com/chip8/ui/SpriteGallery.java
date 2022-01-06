package com.chip8.ui;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * collects sprites found in rom and sorts them in listview
 */
public class SpriteGallery extends ListView {

    private int galleryIndex;
    private SpriteDisplay sd;
    private boolean[][] spriteArray;
    private int height;

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
            this.spriteArray = p.getKey();
            this.height = p.getValue();

            UiElements uiElements = new UiElements();
            TextArea text = uiElements.makeTextArea(340, 35);
            text.setText(this.getHexData());
            text.setWrapText(true);
            HBox hBox = new HBox(10, this.drawSprite(), text);

            this.getItems().add(hBox);
            galleryIndex++;
        }
    }

    private String getHexData() {
        int xlim = height == -1 ? 16 : height;
        int ylim = height == -1 ? 16 : 8;

        StringBuilder spriteHexData = new StringBuilder();
        for (int x = 0; x < xlim; x++) {
            StringBuilder binary = new StringBuilder();
            StringBuilder binary2 = new StringBuilder();
            for (int y = 0; y < ylim; y++) {
                if (y >= 8) {
                    binary2.append(this.spriteArray[y][x] ? "1" : "0");
                } else {
                    binary.append(this.spriteArray[y][x] ? "1" : "0");
                }
            }
            for (int i = 0; i < (height == -1 ? 2 : 1); i++) {
                int decimalForm = i == 0 ? Integer.parseInt(binary.toString(), 2) : Integer.parseInt(binary2.toString(), 2);
                String hexForm = Integer.toString(decimalForm, 16).toUpperCase();
                spriteHexData.append("0x").append(hexForm).append(" ");
            }
        }
        return spriteHexData.toString();
    }

    private Canvas drawSprite() {
        // check if 16x16 sprite
        Canvas canvas = height == -1 ? new Canvas(80, 80) : new Canvas(40, height * 5);

        GraphicsContext painter = canvas.getGraphicsContext2D();
        // odd even split for row colors
        painter.setFill((galleryIndex & 1) == 1 ? Color.BLACK : Color.rgb(21, 21, 21));

        int xlim = height == -1 ? 16 : height;
        int ylim = height == -1 ? 16 : 8;

        painter.fillRect(0, 0, 80, 80);
        painter.setFill(Color.rgb(35, 255, 0));
        for (int x = 0; x < xlim; x++) {
            for (int y = 0; y < ylim; y++) {
                if (spriteArray[y][x]) {
                    painter.fillRect(y * 5, x * 5, 5, 5);
                }
            }
        }
        return canvas;
    }
}
