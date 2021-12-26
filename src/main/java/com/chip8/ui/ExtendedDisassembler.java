package com.chip8.ui;

import com.chip8.emulator.Executer;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * extended disassembler for selected rom
 */
public class ExtendedDisassembler extends Stage {

    ExtendedDisassembler(Executer executer) {
        this.setTitle("ROM Disassembler: " + executer.getLoader().getName());
        UiElements uiElements = new UiElements();

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        BorderPane topButton = new BorderPane();
        ToggleButton follow = uiElements.makeToggleButton("Follow program counter");

        Disassembler disassembler = new Disassembler();
        disassembler.getStylesheets().add("disassembler.css");
        disassembler.setPrefSize(700, 550);

        topButton.setPadding(new Insets(5, 5, 5, 5));
        topButton.setBorder(border);
        topButton.setCenter(follow);
        root.setTop(topButton);
        root.setBackground(bg);
        root.setBorder(border);
        root.setCenter(disassembler);

        follow.setOnAction(e -> {
            if (follow.isSelected()) {
                follow.setText("Stop following");
            } else {
                follow.setText("Follow program counter");
            }
        });

        this.setScene(new Scene(root, 640, 550));
        this.show();

        AnimationTimer screenUpdater = new AnimationTimer() {
            @Override
            public void handle(long l) {
                disassembler.updateFull(executer.getMemory().getPc(), executer.getFetcher(), follow.isSelected());
            }
        };

        this.setOnCloseRequest(windowEvent -> {
            screenUpdater.stop();
            this.close();
        });

        screenUpdater.start();
    }
}
