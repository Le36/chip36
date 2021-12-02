package com.chip8.ui;

import com.chip8.emulator.Keys;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class Keyboard extends GridPane {

    private final ArrayList<Button> buttons;
    private final Keys keys;

    public Keyboard(Keys keys) {
        this.buttons = new ArrayList<>();
        this.keys = keys;
        addButtonsToList();
        this.setHgap(5);
        this.setVgap(5);
        addButtonsToPane();
        keyboardEvents();
        this.setAlignment(Pos.CENTER);
    }

    private void keyboardEvents() {
        for (int i = 0; i < 16; i++) {
            int pressedKey = Integer.parseInt(buttons.get(i).getText(), 16);
            buttons.get(i).setOnMousePressed(e -> {
                this.keys.getKeys()[pressedKey] = true;
            });
            buttons.get(i).setOnMouseReleased(e -> {
                this.keys.getKeys()[pressedKey] = false;
            });
        }
    }

    private void addButtonsToPane() {
        for (int i = 0, first = 0, second = 0; i < 16; i++) {
            this.add(buttons.get(i), first, second);
            first++;
            if (first == 4) {
                second++;
                first = 0;
            }
        }
    }

    private void addButtonsToList() {
        buttons.add(new UiElements().makeButton("1"));
        buttons.add(new UiElements().makeButton("2"));
        buttons.add(new UiElements().makeButton("3"));
        buttons.add(new UiElements().makeButton("C"));
        buttons.add(new UiElements().makeButton("4"));
        buttons.add(new UiElements().makeButton("5"));
        buttons.add(new UiElements().makeButton("6"));
        buttons.add(new UiElements().makeButton("D"));
        buttons.add(new UiElements().makeButton("7"));
        buttons.add(new UiElements().makeButton("8"));
        buttons.add(new UiElements().makeButton("9"));
        buttons.add(new UiElements().makeButton("E"));
        buttons.add(new UiElements().makeButton("A"));
        buttons.add(new UiElements().makeButton("0"));
        buttons.add(new UiElements().makeButton("B"));
        buttons.add(new UiElements().makeButton("F"));
    }
}
