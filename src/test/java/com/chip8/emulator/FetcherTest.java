package com.chip8.emulator;


import static org.junit.Assert.*;

import com.chip8.configs.Configs;
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
        this.decoder = new Decoder(m, fetcher, new PixelManager(1, 1), new Keys(), new Configs());
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

        // if pc is over 0xFFF then results 0x0000
        m.setPc((short) 0x1234);
        fetcher.fetch();
        assertEquals(0x0000, fetcher.getOpcode());
    }

    @Test
    public void seek() {
        // create opcode 0x6D0A == set Vx to NN (VxNN)
        m.initializeMemory((short) 0x200, (byte) 0x6D);
        m.initializeMemory((short) 0x201, (byte) 0x0A);
        assertEquals(0x6D0A, fetcher.seek((short) 0x200));

        // if pc is over 0xFFF then results 0x0000
        assertEquals(0x0000, fetcher.seek((short) 0x1234));
    }

    @Test
    public void timers() {
        // timers to 2
        m.setDelayTimer((byte) 0x2);
        m.setSoundTimer((byte) 0x2);
        // decrement by 1, should be 1 now
        fetcher.timerDecrement();
        assertEquals(0x1, m.getDelayTimer());
        assertEquals(0x1, m.getSoundTimer());
        // decrement right away again, should still be 1 because 60 hz timer
        fetcher.timerDecrement();
        assertEquals(0x1, m.getDelayTimer());
        assertEquals(0x1, m.getSoundTimer());
        // wait for a while
        try {
            Thread.sleep(19);
        } catch (InterruptedException ignored) {
        }
        // now should be 1
        fetcher.timerDecrement();
        assertEquals(0x0, m.getDelayTimer());
        assertEquals(0x0, m.getSoundTimer());
    }

}
