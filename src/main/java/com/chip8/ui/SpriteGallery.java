package com.chip8.ui;


import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
            TextArea text = uiElements.makeTextArea(350, 35);
            text.setText(this.getHexData());
            HBox hBox = new HBox(10, this.drawSprite(), text);

            this.getItems().add(hBox);
            galleryIndex++;
        }
    }

    private String getHexData() {
        StringBuilder spriteHexData = new StringBuilder();
        for (int x = 0; x < height; x++) {
            StringBuilder binary = new StringBuilder();
            for (int y = 0; y < 8; y++) {
                if (spriteArray[y][x]) {
                    binary.append("1");
                } else {
                    binary.append("0");
                }
            }
            int decimalForm = Integer.parseInt(binary.toString(), 2);
            String hexForm = Integer.toString(decimalForm, 16).toUpperCase();
            spriteHexData.append("0x").append(hexForm).append(" ");
            if (x == 8) {
                spriteHexData.append("\n");
            }
        }
        return spriteHexData.toString();
    }

    private Canvas drawSprite() {
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
