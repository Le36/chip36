package com.chip8.ui;

import com.chip8.emulator.Executer;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * extended disassembler for selected rom
 */
public class ExtendedDisassembler extends Stage {

    ExtendedDisassembler(Executer executer) {
        this.setTitle("ROM Disassembler: " + executer.getLoader().getLoadedRom().getName());
        UiElements uiElements = new UiElements();

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        BorderPane topPane = new BorderPane();
        ToggleButton follow = uiElements.makeToggleButton("Follow program counter");
        Label text = uiElements.makeLabel("Graphic symbol: ", LabelType.SMALL);
        TextField symbol = uiElements.makeTextField();
        symbol.setText("██");
        HBox hBox = new HBox(10, follow, text, symbol);

        Disassembler disassembler = new Disassembler();
        disassembler.getStylesheets().add("disassembler.css");
        disassembler.setPrefSize(700, 550);

        topPane.setPadding(new Insets(5, 5, 5, 5));
        topPane.setBorder(border);
        topPane.setCenter(hBox);
        root.setTop(topPane);
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
                disassembler.updateFull(executer.getMemory().getPc(), executer.getFetcher(), follow.isSelected(), symbol.getText());
            }
        };

        this.setOnCloseRequest(windowEvent -> {
            screenUpdater.stop();
            this.close();
        });

        screenUpdater.start();
    }
}
