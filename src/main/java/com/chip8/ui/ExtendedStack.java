package com.chip8.ui;

import com.chip8.emulator.Executer;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * possibility to view whole stack content
 */
public class ExtendedStack extends Stage {

    ExtendedStack(Executer executer) {
        this.setTitle("Stack");

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        ListView listView = new ListView();
        listView.getStylesheets().add("disassembler.css");
        listView.setPrefSize(250, 300);

        root.setBackground(bg);
        root.setBorder(border);
        root.setCenter(listView);


        this.setScene(new Scene(root, 250, 300));
        this.show();

        AnimationTimer screenUpdater = new AnimationTimer() {
            @Override
            public void handle(long l) {
                listView.getItems().clear();
                try {
                    if (executer.getMemory().getStack().isEmpty()) {
                        listView.getItems().add("Stack is empty!");
                    } else {
                        int k = 0;
                        for (int i : executer.getMemory().getStack()) {
                            listView.getItems().add("Index: " + k + " Contains: 0x" + Integer.toHexString(i).toUpperCase());
                            k++;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        };

        this.setOnCloseRequest(windowEvent -> {
            screenUpdater.stop();
            this.close();
        });

        screenUpdater.start();
    }
}
