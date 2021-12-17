package com.chip8.emulator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PixelManagerTest {

    private PixelManager pixels;

    @Before
    public void setUp() {
        this.pixels = new PixelManager(5, 5);
    }

    @Test
    public void fading() {
        // draw pixel to 0, 0
        pixels.draw(0, 0);
        // then erase it
        pixels.draw(0, 0);
        // now it should have fade value
        assertEquals(0.95, pixels.getFadeMap().get(0).get(0), 0.01);
        // set fade speed to 0.05
        pixels.setFadeSpeed(0.05);
        pixels.fade();
        assertEquals(0.90, pixels.getFadeMap().get(0).get(0), 0.01);
        for (int i = 0; i < 10; i++) {
            pixels.fade();
        }
        assertEquals(0.40, pixels.getFadeMap().get(0).get(0), 0.01);
    }
}
