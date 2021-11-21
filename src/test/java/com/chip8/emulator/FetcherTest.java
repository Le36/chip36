package com.chip8.emulator;


import static org.junit.Assert.*;

import com.chip8.ui.PixelManager;
import org.junit.Before;
import org.junit.Test;

public class FetcherTest {

    private Memory m;
    private Fetcher fetcher;
    private Decoder decoder;

    @Before
    public void setUp() {
        this.m = new Memory();
        this.fetcher = new Fetcher(m);
        this.decoder = new Decoder(m, fetcher, new PixelManager(1, 1), new Keys());
    }

    @Test
    public void incrementPC() {
        fetcher.incrementPC();
        assertEquals(0x202, m.getPc());
        fetcher.incrementPC();
        fetcher.incrementPC();
        fetcher.incrementPC();
        assertEquals(0x208, m.getPc());
    }

    @Test
    public void fetch() {
        // create opcode 0x6D0A == set Vx to NN (VxNN)
        m.initializeMemory((short) 0x200, (byte) 0x6D);
        m.initializeMemory((short) 0x201, (byte) 0x0A);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        assertEquals(0x0A, m.getV()[0xD]);
        assertEquals(0x202, m.getPc());
    }

}
