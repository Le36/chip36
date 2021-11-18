package com.chip8.emulator;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DecoderTest {

    private Memory m;
    private Fetcher fetcher;
    private Decoder decoder;

    @Before
    public void setUp() {
        this.m = new Memory();
        this.fetcher = new Fetcher(m);
        this.decoder = new Decoder(m, fetcher);
    }

    @Test
    public void jump1NNN() {
        decoder.decode((byte) 0x1200);
        assertEquals(0x200, m.getPC());

        decoder.decode((short) 0x1954);
        assertEquals(0x954, m.getPC());
    }

    @Test
    public void set6XNN() {
        decoder.decode((short) 0x6070);
        assertEquals(0x70, m.getV()[0x0]);

        decoder.decode((short) 0x6A35);
        assertEquals(0x35, m.getV()[0xA]);
    }

    @Test
    public void add7XNN() {
        decoder.decode((short) 0x7035);
        assertEquals(0x35, m.getV()[0x0]);

        decoder.decode((short) 0x7015);
        assertEquals(0x4A, m.getV()[0x0]);
    }

    @Test
    public void setIndexANNN() {
        decoder.decode((short) 0xA530);
        assertEquals(0x530, m.getI());

        decoder.decode((short) 0xAFF3);
        assertEquals(0xFF3, m.getI());
    }
}
