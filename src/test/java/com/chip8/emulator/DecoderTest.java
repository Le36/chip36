package com.chip8.emulator;


import static org.junit.Assert.*;

import com.chip8.ui.PixelManager;
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
        this.decoder = new Decoder(m, fetcher, new PixelManager(1, 1));
    }


    @Test
    public void jump1NNN() {
        decoder.decode((byte) 0x1200);
        assertEquals(0x200, m.getPc());

        decoder.decode((short) 0x1954);
        assertEquals(0x954, m.getPc());
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

    @Test
    public void BCDFx33() {
        decoder.decode((short) 0x609F); // set 0x9F (0d159) to V[0]
        decoder.decode((short) 0xA411); // Index to 0x411
        decoder.decode((short) 0xF033); // Convert to BCD
        // now we expect to have BCD 159 in RAM as
        assertEquals(9, m.getRam()[m.getI() + 2]);
        assertEquals(5, m.getRam()[m.getI() + 1]);
        assertEquals(1, m.getRam()[m.getI()]);
    }

    @Test
    public void dumpRegistersFx55() {
        m.varReg(0, 30);
        m.varReg(2, 60);
        m.varReg(5, 11);
        m.setI((short) 0x202);
        decoder.decode((short) 0xF755);
        assertEquals(30, m.getRam()[m.getI()]);
        assertEquals(0, m.getRam()[m.getI() + 1]);
        assertEquals(60, m.getRam()[m.getI() + 2]);
        assertEquals(11, m.getRam()[m.getI() + 5]);
    }

    @Test
    public void fillRegistersFx65() {
        byte[] RAM = m.getRam();
        RAM[0xBB8] = 0x50;
        RAM[0xBB9] = 0x30;
        RAM[0xBBA] = 0x1F;
        m.setRam(RAM);
        m.setI((short) 0xBB8);
        decoder.decode((short) 0xF365);
        assertEquals(0x50, m.getV()[0]);
        assertEquals(0x30, m.getV()[1]);
        assertEquals(0x1F, m.getV()[2]);
    }

    @Test
    public void fontFx29() {
        m.varReg(0x0, 0xA); // insert A character into V[0x0]

        decoder.decode((short) 0xF029); // make I point to character A location, V[0] holds A
        // since fonts are loaded into ram from address 0x50 to 0x9F
        // we can expect I to be pointing into 0x82 because thats where A is loaded in RAM
        assertEquals(0x82, m.getI());
        // 0xF0, 0x90, 0xF0, 0x90, 0x90 is font data for A so we can expect to find these from RAM pointed by I
        assertEquals(0xF0, Byte.toUnsignedInt(m.getRam()[m.getI()]));
        assertEquals(0x90, Byte.toUnsignedInt(m.getRam()[m.getI() + 1]));
        assertEquals(0xF0, Byte.toUnsignedInt(m.getRam()[m.getI() + 2]));
        assertEquals(0x90, Byte.toUnsignedInt(m.getRam()[m.getI() + 3]));
        assertEquals(0x90, Byte.toUnsignedInt(m.getRam()[m.getI() + 4]));

        m.varReg(0xC, 0x5); // insert 5 character into V[0xC]
        decoder.decode((short) 0xFC29);
        assertEquals(0x69, m.getI()); // 5's location in RAM

        // 0xF0, 0x80, 0xF0, 0x10, 0xF0 font data for 5
        assertEquals(0xF0, Byte.toUnsignedInt(m.getRam()[m.getI()]));
        assertEquals(0x80, Byte.toUnsignedInt(m.getRam()[m.getI() + 1]));
        assertEquals(0xF0, Byte.toUnsignedInt(m.getRam()[m.getI() + 2]));
        assertEquals(0x10, Byte.toUnsignedInt(m.getRam()[m.getI() + 3]));
        assertEquals(0xF0, Byte.toUnsignedInt(m.getRam()[m.getI() + 4]));
    }
}
