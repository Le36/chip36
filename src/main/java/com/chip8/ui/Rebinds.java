package com.chip8.ui;

import com.chip8.emulator.Keys;
import javafx.util.Pair;

import java.util.ArrayList;


public class Rebinds extends KeyboardPane {

    public Rebinds(Keys keys) {
        super(keys, false);
        this.keyboardEvents();
    }

    private void keyboardEvents() {
        for (int i = 0; i < 16; i++) {
            super.getTButtons().get(i).setOnMousePressed(e -> {
                for (int j = 0; j < 16; j++) {
                    if (super.getTButtons().get(j).isSelected()) {
                        super.getTButtons().get(j).setSelected(false);
                    }
                }
            });
        }
    }

    public Pair<Boolean, Integer> isToggled() {
        for (int i = 0; i < 16; i++) {
            if (super.getTButtons().get(i).isSelected()) {
                return new Pair<>(true, i);
            }
        }
        return new Pair<>(false, 0);
    }

    public void setDefault() {
        for (int i = 0; i < 16; i++) {
            String layout = "1234QWERASDFZXCV";
            super.getTButtons().get(i).setText(layout.substring(i, i + 1));
        }
    }

    public ArrayList<String> getRebinds() {
        ArrayList<String> newBinds = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            newBinds.add(super.getTButtons().get(i).getText());
        }
        return newBinds;
    }
}