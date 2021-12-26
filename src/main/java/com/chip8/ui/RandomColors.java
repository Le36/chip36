package com.chip8.ui;

import java.util.Random;

/**
 * generates random colors
 */
public class RandomColors {

    private static Random random = new Random();

    /**
     * returns a random color
     *
     * @return random color string in 0x000000 format
     */
    public String getColor() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }

        return "0x" + sb;
    }
}
