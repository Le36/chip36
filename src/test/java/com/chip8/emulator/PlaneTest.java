package com.chip8.emulator;

import com.chip8.configs.Configs;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaneTest {

    private Memory m;
    private Fetcher fetcher;
    private Decoder decoder;
    private Keys keys;

    @Before
    public void setUp() {
        this.m = new Memory();
        this.keys = new Keys();
        this.fetcher = new Fetcher(m);
        this.decoder = new Decoder(m, fetcher, new PixelManager(128, 64), keys, new Configs());
        // prepare draw instruction, 1 pixel at index 0x400, for coords 10,10
        m.varReg(0, 0xA);
        m.varReg(1, 0xA);
        m.setI((short) 0x400);
        byte[] ram = m.getRam();
        ram[0x400] = (byte) 0b10000000;
        // for both planes
        ram[0x401] = (byte) 0b10000000;
        // when in hires mode
        ram[0x500] = (byte) 0b10000000;
        ram[0x520] = (byte) 0b10000000;
        m.setRam(ram);
    }

    @Test
    public void scrollRight() {
        // draw in lores to default plane
        decoder.decode((short) 0xD011);
        // scroll, default scroll in lores mode is 2 pixels
        decoder.decode((short) 0x00FB);
        assertTrue(decoder.getPixels().getDisplay()[12][10][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[12][10][1]);
    }

    @Test
    public void scrollRightXoPlane() {
        // draw in hires to xo plane
        decoder.decode((short) 0xF201);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD011);
        // scroll, default scroll in hires mode is 4 pixels
        decoder.decode((short) 0x00FB);
        assertTrue(decoder.getPixels().getDisplay()[14][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[14][10][0]);
    }

    @Test
    public void scrollRightBothPlane() {
        // draw in lores to both plane
        decoder.decode((short) 0xF301);
        decoder.decode((short) 0xD011);
        // scroll, default scroll in lores mode is 2 pixels
        decoder.decode((short) 0x00FB);
        assertTrue(decoder.getPixels().getDisplay()[12][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        assertTrue(decoder.getPixels().getDisplay()[12][10][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[12][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[12][10][1]);
    }

    @Test
    public void scrollLeft() {
        // draw in lores to default plane
        decoder.decode((short) 0xD011);
        // scroll, default scroll in lores mode is 2 pixels
        decoder.decode((short) 0x00FC);
        assertTrue(decoder.getPixels().getDisplay()[8][10][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[8][10][1]);
    }

    @Test
    public void scrollLeftXoPlane() {
        // draw in hires to xo plane
        decoder.decode((short) 0xF201);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD010);
        // scroll, default scroll in hires mode is 4 pixels
        decoder.decode((short) 0x00FC);
        assertTrue(decoder.getPixels().getDisplay()[6][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[6][10][0]);
    }

    @Test
    public void scrollLeftBothPlane() {
        // draw in hires to both plane
        m.setI((short) 0x500);
        decoder.decode((short) 0xF301);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD010);
        // scroll, default scroll in hires mode is 4 pixels
        decoder.decode((short) 0x00FC);
        assertTrue(decoder.getPixels().getDisplay()[6][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        assertTrue(decoder.getPixels().getDisplay()[6][10][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[6][10][0]);
        assertFalse(decoder.getPixels().getDisplay()[6][10][1]);
    }

    @Test
    public void scrollUp() {
        // draw in hires to default plane
        m.setI((short) 0x500);
        decoder.decode((short) 0xF101);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD010);
        // scroll up by 8
        decoder.decode((short) 0x00D8);
        assertTrue(decoder.getPixels().getDisplay()[10][2][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][2][1]);
    }

    @Test
    public void scrollUpXoPlane() {
        // draw in hires to xo plane
        decoder.decode((short) 0xF201);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD011);
        // scroll 8 pixels up
        decoder.decode((short) 0x00D8);
        assertTrue(decoder.getPixels().getDisplay()[10][2][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][2][0]);
    }

    @Test
    public void scrollUpBothPlane() {
        // draw in lores to both plane
        decoder.decode((short) 0xF301);
        decoder.decode((short) 0xD011);
        // scroll up by 8, but in lores 4
        decoder.decode((short) 0x00D8);
        assertTrue(decoder.getPixels().getDisplay()[10][6][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        assertTrue(decoder.getPixels().getDisplay()[10][6][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][6][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][6][1]);
    }

    @Test
    public void scrollDown() {
        // draw in hires to default plane
        m.setI((short) 0x500);
        decoder.decode((short) 0xF101);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD010);
        // scroll up by 8
        decoder.decode((short) 0x00C8);
        assertTrue(decoder.getPixels().getDisplay()[10][18][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][18][1]);
    }

    @Test
    public void scrollDownXoPlane() {
        // draw in hires to xo plane
        decoder.decode((short) 0xF201);
        decoder.decode((short) 0x00FF);
        decoder.decode((short) 0xD011);
        // scroll 8 pixels up
        decoder.decode((short) 0x00C8);
        assertTrue(decoder.getPixels().getDisplay()[10][18][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][18][0]);
    }

    @Test
    public void scrollDownBothPlane() {
        // draw in lores to both plane
        decoder.decode((short) 0xF301);
        decoder.decode((short) 0xD011);
        // scroll down by 8, but in lores 4
        decoder.decode((short) 0x00C8);
        assertTrue(decoder.getPixels().getDisplay()[10][14][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][0]);
        assertTrue(decoder.getPixels().getDisplay()[10][14][1]);
        assertFalse(decoder.getPixels().getDisplay()[10][10][1]);
        // clear then
        decoder.decode((short) 0x00E0);
        assertFalse(decoder.getPixels().getDisplay()[10][14][0]);
        assertFalse(decoder.getPixels().getDisplay()[10][14][1]);
    }
}
