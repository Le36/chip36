package com.chip8.ui;

import com.chip8.configs.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * sprite extractor that shows all the sprites in the rom
 */
public class SpriteExtractor extends Stage {

    SpriteExtractor(Configs configs, SpriteDisplay sprites) {
        this.setTitle("Sprite extractor");

        UiElements uiElements = new UiElements();

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        BorderPane topButton = new BorderPane();
        ToggleButton startExtractor = uiElements.makeToggleButton("Start extractor");
        topButton.setPadding(new Insets(5, 5, 5, 5));
        topButton.setBorder(border);
        topButton.setCenter(startExtractor);
        root.setTop(topButton);
        root.setBackground(bg);
        root.setBorder(border);

        SpriteGallery spriteGallery = new SpriteGallery(sprites);

        root.setCenter(spriteGallery);

        startExtractor.setOnAction(e -> {
            if (startExtractor.isSelected()) {
                startExtractor.setText("Stop extractor");
                configs.setSpriteExtracting(true);
            } else {
                startExtractor.setText("Start extractor");
                configs.setSpriteExtracting(false);
            }
        });

        this.setScene(new Scene(root, 480, 400));
        this.show();

        AnimationTimer screenUpdater = new AnimationTimer() {
            @Override
            public void handle(long l) {
                spriteGallery.update();
            }
        };

        this.setOnCloseRequest(windowEvent -> {
            configs.setSpriteExtracting(false);
            screenUpdater.stop();
            this.close();
        });

        screenUpdater.start();
    }

}
