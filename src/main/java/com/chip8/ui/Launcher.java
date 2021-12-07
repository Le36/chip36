package com.chip8.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Launcher extends Stage {

    Launcher() {
        UiElements uiElements = new UiElements();
        this.setTitle("Chip8 Launcher");
        Button yes = uiElements.makeButton("Extended");
        Button no = uiElements.makeButton("Normal");

        Label top = uiElements.makeLabel("Select the mode you want to launch the emulator in", LabelType.TOOLBAR);
        BorderPane launcherRoot = new BorderPane();
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        launcherRoot.setBackground(bg);
        launcherRoot.setTop(top);
        launcherRoot.setCenter(new HBox(5, yes, no));

        Slider resolution = uiElements.makeSlider(1, 60, 15);
        resolution.setSnapToTicks(true);
        resolution.setMajorTickUnit(1.0);
        resolution.setMinorTickCount(1);
        Label gameRes = uiElements.makeLabel("Game resolution:", LabelType.TOOLBAR);
        Label selectedRes = uiElements.makeLabel("0x0", LabelType.TOOLBAR);
        Button launchNormal = uiElements.makeButton("Launch");
        VBox normalMode = new VBox(5, resolution, gameRes, selectedRes, launchNormal);
        normalMode.setAlignment(Pos.CENTER);
        this.setScene(new Scene(launcherRoot, 350, 200));
        this.show();

        yes.setOnAction(e -> {
            this.close();
            new EmulatorUi(true, 10);
        });

        no.setOnAction(e -> {
            top.setText("Select your resolution");
            launcherRoot.setCenter(normalMode);
        });

        launchNormal.setOnAction(e -> {
            this.close();
            new EmulatorUi(false, (int) resolution.getValue());
        });

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                int val = (int) resolution.getValue();
                selectedRes.setText(val * 64 + "x" + val * 32);
            }
        }.start();
    }
}
