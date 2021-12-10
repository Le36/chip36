package com.chip8.ui;

import com.chip8.emulator.Keys;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Options extends Stage {

    Options(Keys keys) {
        this.setTitle("Options");

        UiElements uiElements = new UiElements();

        Border border = new Border(new BorderStroke(Color.rgb(35, 255, 0),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        Background bg = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();

        Rebinds rebinds = new Rebinds(keys);
        Button setDefault = uiElements.makeButton("Set default");
        Button setKeys = uiElements.makeButton("Save rebinds");
        HBox hBoxBottom = new HBox(10, setDefault, setKeys);

        root.setTop(uiElements.makeLabel("Rebind your keys: ", LabelType.LABEL));
        root.setCenter(rebinds);
        root.setBackground(bg);
        root.setBorder(border);
        root.setBottom(hBoxBottom);
        root.setPadding(new Insets(20, 20, 20, 20));

        this.addEventFilter(KeyEvent.ANY, keyEvent -> {
            if (rebinds.isToggled().getKey()) {
                int i = rebinds.isToggled().getValue();
                rebinds.getTButtons().get(i).setText(keyEvent.getCode().getName());
                rebinds.getTButtons().get(i).setSelected(false);
            }
        });

        setDefault.setOnAction(e -> {
            rebinds.setDefault();
        });

        setKeys.setOnAction(e -> {
            String[] binds = new String[16];
            for (int i = 0; i < 16; i++) {
                binds[i] = rebinds.getTButtons().get(i).getText();
            }
            keys.setBinds(binds);
        });

        this.setScene(new Scene(root, 450, 250));
        this.show();
    }
}