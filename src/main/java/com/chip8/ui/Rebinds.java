package com.chip8.ui;

import com.chip8.configs.DefaultValues;
import com.chip8.emulator.Keys;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;


/**
 * allows rebinding of keyboard used by the emulator
 */
public class Rebinds extends KeyboardPane {

    public Rebinds(Keys keys) {
        super(keys, false);
        this.keyboardEvents();
    }

    /**
     * initializes action for toggling the buttons on the options menu,
     * when selecting key with mouse that is rebinded, untoggles the previous
     * button if it was toggled
     */
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

    /**
     * used to check if there exists a key that is toggled on options menu,
     * and returns it with toggle state and identifier number
     *
     * @return pressed key and int identifier for it
     */
    public Pair<Boolean, Integer> isToggled() {
        for (int i = 0; i < 16; i++) {
            if (super.getTButtons().get(i).isSelected()) {
                return new Pair<>(true, i);
            }
        }
        return new Pair<>(false, 0);
    }

    /**
     * sets all keys to their default values
     */
    public void setDefault() {
        for (int i = 0; i < 16; i++) {
            String layout = new DefaultValues().getRebindLayout();
            super.getTButtons().get(i).setText(layout.substring(i, i + 1));
        }
    }

    /**
     * sets the bind to the currently selected key
     *
     * @param keyEvent detected keypress from options menu
     */
    public void keyBind(KeyEvent keyEvent) {
        if (this.isToggled().getKey()) {
            int i = this.isToggled().getValue();
            super.getTButtons().get(i).setText(keyEvent.getCode().getName());
            super.getTButtons().get(i).setSelected(false);
        }
    }
}