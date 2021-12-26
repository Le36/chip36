package com.chip8.ui;

import com.chip8.configs.ConfigsSaver;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;

/**
 * handles effects for the emulator canvas
 */
public class EffectController {

    private RomDisplay romDisplay;

    public EffectController(RomDisplay romDisplay) {
        this.romDisplay = romDisplay;
    }

    /**
     * applies gaussian blur for display
     *
     * @param strength radius for blur
     */
    public void applyBlur(double strength) {
        GaussianBlur blur = new GaussianBlur(strength);
        romDisplay.setEffect(blur);
    }

    /**
     * applies glow for display
     *
     * @param strength glow strength
     */
    public void applyGlow(double strength) {
        Glow glow = new Glow(strength);
        romDisplay.setEffect(glow);
    }

    /**
     * applies gaussian blur and glow to display
     *
     * @param b blur radius
     * @param g glow strength
     */
    public void applyBlurGlow(double b, double g) {
        GaussianBlur blur = new GaussianBlur(b);
        Glow glow = new Glow(g);
        glow.setInput(blur);
        romDisplay.setEffect(glow);
    }

    /**
     * removes all effects
     */
    public void removeEffects() {
        romDisplay.setEffect(null);
    }

    /**
     * set round pixels for display or square pixels
     *
     * @param state round pixels or not
     */
    public void roundPixels(boolean state) {
        romDisplay.setRoundPixels(state);
    }

    /**
     * load saved states from file on emulator launch
     */
    public void onLoad() {
        ConfigsSaver configsSaver = new ConfigsSaver();
        try {
            romDisplay.setRoundPixels(configsSaver.loadState("roundPixels:"));
            boolean blur = configsSaver.loadState("blur:");
            boolean glow = configsSaver.loadState("glow:");

            double blurValue = configsSaver.loadValue("blurValue:");
            double glowValue = configsSaver.loadValue("glowValue:");

            if (glow && blur) {
                this.applyBlurGlow(blurValue, glowValue);
            } else if (glow) {
                this.applyGlow(glowValue);
            } else if (blur) {
                this.applyBlur(blurValue);
            }
        } catch (Exception ignored) {
            // do nothing, don't load anything
        }
    }
}
