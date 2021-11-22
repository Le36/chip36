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
        this.decoder = new Decoder(m, fetcher, new PixelManager(1, 1), new Keys());
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
        // insert 3 different values into registers
        // they are v[0] = 0d30, v[2] = 0d60, v[5] = 0d11
        m.varReg(0, 30);
        m.varReg(2, 60);
        m.varReg(5, 11);
        m.setI((short) 0x202);
        decoder.decode((short) 0xF755);
        // instruction 0xF755 -> dump registers v[0] - v[6] to ram
        assertEquals(30, m.getRam()[m.getI()]);
        assertEquals(0, m.getRam()[m.getI() + 1]);
        assertEquals(60, m.getRam()[m.getI() + 2]);
        assertEquals(11, m.getRam()[m.getI() + 5]);
    }

    @Test
    public void fillRegistersFx65() {
        // insert 3 different values into ram
        // that are 0x50, 0x30, 0x1F
        byte[] RAM = m.getRam();
        RAM[0xBB8] = 0x50;
        RAM[0xBB9] = 0x30;
        RAM[0xBBA] = 0x1F;
        m.setRam(RAM);
        m.setI((short) 0xBB8);
        decoder.decode((short) 0xF365);
        // now check if registers are filled properly
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
        assertEquals((byte) 0xF0, m.getRam()[m.getI()]);
        assertEquals((byte) 0x90, m.getRam()[m.getI() + 1]);
        assertEquals((byte) 0xF0, m.getRam()[m.getI() + 2]);
        assertEquals((byte) 0x90, m.getRam()[m.getI() + 3]);
        assertEquals((byte) 0x90, m.getRam()[m.getI() + 4]);

        m.varReg(0xC, 0x5); // insert 5 character into V[0xC]
        decoder.decode((short) 0xFC29);
        assertEquals(0x69, m.getI()); // 5's location in RAM

        // 0xF0, 0x80, 0xF0, 0x10, 0xF0 font data for 5
        assertEquals((byte) 0xF0, m.getRam()[m.getI()]);
        assertEquals((byte) 0x80, m.getRam()[m.getI() + 1]);
        assertEquals((byte) 0xF0, m.getRam()[m.getI() + 2]);
        assertEquals((byte) 0x10, m.getRam()[m.getI() + 3]);
        assertEquals((byte) 0xF0, m.getRam()[m.getI() + 4]);
    }

    @Test
    public void skipIfEqual3XNN() {
        // set pc to 0x400
        m.setPc((short) 0x400);
        // load instruction 0x3020 to ram at 0x400 - 0x401
        byte[] ram = m.getRam();
        ram[0x400] = 0x30;
        ram[0x401] = 0x20;
        m.setRam(ram);
        // set v[0] to 0x20
        m.varReg(0x0, 0x20);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we have 0x20 in v[0] and the instruction is trying to compare
        // 0x20 to that value, the result should be that pc is incremented twice
        // instead of once
        assertEquals(0x404, m.getPc());
        assertNotEquals(0x402, m.getPc());

        // reset pc
        m.setPc((short) 0x400);
        // change instruction to compare value in v[0] to 0x21
        ram[0x401] = 0x21;
        m.setRam(ram);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since v[0] contains 0x20, and we're trying to compare it to 0x21
        // result should be that pc is incremented only once
        assertEquals(0x402, m.getPc());
        assertNotEquals(0x404, m.getPc());
    }

    @Test
    public void skipIfNotEqual4XNN() {
        // set pc to 0x334
        m.setPc((short) 0x334);
        // load instruction 0x3020 to ram at 0x334 - 0x335
        byte[] ram = m.getRam();
        ram[0x334] = 0x4D;
        ram[0x335] = 0x15;
        m.setRam(ram);
        // set v[D] to 0x15
        m.varReg(0xD, 0x15);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we have 0x15 in v[D] and the instruction is trying to compare
        // 0x15 to that value, the result should be that pc is incremented once
        // because the values are equal
        assertEquals(0x336, m.getPc());
        assertNotEquals(0x338, m.getPc());

        // now if we're trying to compare unequal values it should increment twice
        ram[0x335] = 0x16;
        m.setRam(ram);
        m.setPc((short) 0x334);
        // v[D] is still holding 0x15, and we try to compare it to 0x16 now
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // now it should have incremented pc twice
        assertEquals(0x338, m.getPc());
        assertNotEquals(0x336, m.getPc());
    }

    @Test
    public void skipIfEqualRegisters5XY0() {
        // set pc to 0x300
        m.setPc((short) 0x300);
        // load instruction 0x5010 to ram at 0x300 - 0x301
        byte[] ram = m.getRam();
        ram[0x300] = 0x50;
        ram[0x301] = 0x10;
        m.setRam(ram);
        // set v[0] to 0x15
        m.varReg(0x0, 0x15);
        // set v[1] to 0x15
        m.varReg(0x1, 0x15);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we are trying to compare values in v[0] and v[1]
        // and they both contain the same value 0x15
        // result should be that pc is incremented twice
        assertEquals(0x304, m.getPc());
        assertNotEquals(0x302, m.getPc());

        // reset pc
        m.setPc((short) 0x300);
        // change value in v[1] to different from what is in v[0]
        m.varReg(0x1, 0x00);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // now the values in v[0] and v[1] are not equal
        // pc should only be incremented once
        assertEquals(0x302, m.getPc());
        assertNotEquals(0x304, m.getPc());
    }

    @Test
    public void jump1NNN() {
        // set pc to 0x200
        decoder.decode((byte) 0x1200);
        assertEquals(0x200, m.getPc());

        // set pc to 0x954
        decoder.decode((short) 0x1954);
        assertEquals(0x954, m.getPc());
    }

    @Test
    public void set6XNN() {
        // set v[0] to 0x70
        decoder.decode((short) 0x6070);
        assertEquals(0x70, m.getV()[0x0]);

        // set v[A] to 0x35
        decoder.decode((short) 0x6A35);
        assertEquals(0x35, m.getV()[0xA]);
    }

    @Test
    public void add7XNN() {
        // add 0x35 to v[0]
        decoder.decode((short) 0x7035);
        // since v[0] was 0, it should be now 0x35
        assertEquals(0x35, m.getV()[0x0]);
        // add 0x15 to v[0]
        decoder.decode((short) 0x7015);
        // since v[0] was 0x35, it should now be 0x4A
        assertEquals(0x4A, m.getV()[0x0]);

        decoder.decode((short) 0x7C35);
        assertEquals(0x35, m.getV()[0xC]);
        decoder.decode((short) 0x7C55);
        assertEquals((byte) 0x8A, (m.getV()[0xC]));
    }

    @Test
    public void setIndexANNN() {
        // set index to 0x530
        decoder.decode((short) 0xA530);
        assertEquals(0x530, m.getI());

        // set index to 0xFF3
        decoder.decode((short) 0xAFF3);
        assertEquals(0xFF3, m.getI());
    }

    @Test
    public void jumpWithOffsetBNNN() {
        // set 0x30 to v[0]
        m.varReg(0x0, 0x30);
        // execute instruction, pc should be at 0x30 + 0x30
        decoder.decode((short) 0xB030);
        assertEquals(0x60, m.getPc());

        // execute instruction, pc should be at 0x30 + 0x1F5
        decoder.decode((short) 0xB1F5);
        assertEquals(0x225, m.getPc());
    }

    @Test
    public void setVxToDelayFX07() {
        // set delay timer to 60, instruction sets it to v[0x4]
        m.setDelayTimer((byte) 60);
        decoder.decode((short) 0xF407);
        assertEquals(60, m.getV()[0x4]);

        decoder.decode((short) 0xFD07);
        assertEquals(60, m.getV()[0xD]);
    }

    @Test
    public void setDelayToVxFX15() {
        // set delay timer to 0x22 then v[0xF] to 0x1F
        // after instruction check if delay timer is now 0x1F
        m.setDelayTimer((byte) 0x22);
        m.varReg(0xF, 0x1F);
        decoder.decode((short) 0xFF15);
        assertEquals(0x1F, m.getDelayTimer());

        // set delay timer to 0x52 then v[0x9] to 0x6A
        // after instruction check if delay timer is now 0x6A
        m.setDelayTimer((byte) 0x52);
        m.varReg(0x9, 0x6A);
        decoder.decode((short) 0xF915);
        assertEquals(0x6A, m.getDelayTimer());
    }

    @Test
    public void setSoundToVxFX18() {
        // set sound timer to 0x22 then v[0xF] to 0x1F
        // after instruction check if delay timer is now 0x1F
        m.setSoundTimer((byte) 0x22);
        m.varReg(0xF, 0x1F);
        decoder.decode((short) 0xFF18);
        assertEquals(0x1F, m.getSoundTimer());

        // set sound timer to 0x52 then v[0x9] to 0x6A
        // after instruction check if delay timer is now 0x6A
        m.setSoundTimer((byte) 0x52);
        m.varReg(0x9, 0x6A);
        decoder.decode((short) 0xF918);
        assertEquals(0x6A, m.getSoundTimer());
    }

    @Test
    public void addToIndexFX1E() {
        m.setI((short) 0x400);
        m.varReg(0x5, 0x40);
        decoder.decode((short) 0xF51E);
        assertEquals(0x440, m.getI());

        m.setI((short) 0x150);
        m.varReg(0xB, 0xFF);
        decoder.decode((short) 0xFB1E);
        assertEquals(0x24F, m.getI());
    }

}
