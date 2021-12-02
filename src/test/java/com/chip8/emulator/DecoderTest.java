package com.chip8.emulator;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DecoderTest {

    private Memory m;
    private Fetcher fetcher;
    private Decoder decoder;
    private Keys keys;

    @Before
    public void setUp() {
        this.m = new Memory();
        this.keys = new Keys();
        new Loader("noFileHere", m).loadFontToRAM();
        this.fetcher = new Fetcher(m);
        this.decoder = new Decoder(m, fetcher, new PixelManager(64, 32), keys);
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
    public void skipIfNotEqualRegs9XY0() {
        // set pc to 0x300
        m.setPc((short) 0x300);
        // load instruction 0x9010 to ram at 0x300 - 0x301
        byte[] ram = m.getRam();
        ram[0x300] = (byte) 0x90;
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
        // result should be that pc is incremented once
        assertEquals(0x302, m.getPc());
        assertNotEquals(0x304, m.getPc());

        // reset pc
        m.setPc((short) 0x300);
        // change value in v[1] to different from what is in v[0]
        m.varReg(0x1, 0x00);
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // now the values in v[0] and v[1] are not equal
        // pc should only be incremented twice
        assertEquals(0x304, m.getPc());
        assertNotEquals(0x302, m.getPc());
    }

    @Test
    public void binaryXor8XY3() {
        // setup V9 and VD
        // with binary xor result would be:
        // 000101 ^ 110001 = 110100
        m.varReg(0x9, 0b000101);
        m.varReg(0xD, 0b110001);
        // do the instuction for V9 and VD
        decoder.decode((short) 0x89D3);
        assertEquals((byte) 0b110100, m.getV()[9]);
    }

    @Test
    public void binaryAnd8XY2() {
        // setup V9 and VD
        // with binary and result would be:
        // 000101 & 110001 = 000001
        m.varReg(0x9, 0b000101);
        m.varReg(0xD, 0b110001);
        // do the instuction for V9 and VD
        decoder.decode((short) 0x89D2);
        assertEquals((byte) 0b000001, m.getV()[9]);
    }

    @Test
    public void binaryOr8XY1() {
        // setup V9 and VD
        // with binary or result would be:
        // 000101 | 110001 = 110101
        m.varReg(0x9, 0b000101);
        m.varReg(0xD, 0b110001);
        // do the instuction for V9 and VD
        decoder.decode((short) 0x89D1);
        assertEquals((byte) 0b110101, m.getV()[9]);
    }

    @Test
    public void setVxToVy8XY0() {
        // set 0xF2 to V5
        m.varReg(5, 0xF2);
        // now generate instruction to set that value to V2
        decoder.decode((short) 0x8250);
        assertEquals((byte) 0xF2, m.getV()[2]);
    }

    @Test
    public void callSubroutine2NNN() {
        // check stack empty
        assertEquals(0, m.getStack().size());
        // set pc to 0x502
        m.setPc((short) 0x502);
        // generate instruction to jump to 0xAAA
        // so we expect to find pc set to 0xAAA
        // and stack holding value 0x502
        decoder.decode((short) 0x2AAA);
        assertEquals(1, m.getStack().size());
        assertEquals(0xAAA, m.getPc());
        assertEquals(0x502, (short) m.getStack().peek());
    }

    @Test
    public void returnFromSub00EE() {
        // sets 0x502 to stack
        this.callSubroutine2NNN();
        assertEquals(1, m.getStack().size());
        assertEquals(0xAAA, m.getPc());
        // generate instruction to return
        // we assume pc to be set to 0x502
        decoder.decode((short) 0x00EE);
        assertEquals(0, m.getStack().size());
        assertEquals(0x502, m.getPc());
    }

    @Test
    public void addVxVy8XY4() {
        m.varReg(0x4, 0x04);
        m.varReg(0xB, 0x0A);
        decoder.decode((short) 0x84B4);
        assertEquals((byte) 0x0E, m.getV()[4]);
        assertEquals(0, m.getV()[0xF]);
        // now try if VF goes 1 if overflow
        m.varReg(0xB, 0xFA);
        decoder.decode((short) 0x84B4);
        assertEquals(1, m.getV()[0xF]);
        // 0x0E + 0xFA = 0x108, but since overflow
        // 0b100001000
        //   ^ removed first bit value is then 0x08
        assertEquals((byte) 0x08, m.getV()[4]);
    }

    @Test
    public void subtract8XY5() {
        m.varReg(0x0, 0x6F);
        m.varReg(0x1, 0x0A);
        decoder.decode((short) 0x8015);
        // since v0 > v1 VF should be set to 1
        assertEquals(1, m.getV()[0xF]);
        assertEquals((byte) 0x65, m.getV()[0]);
        // now swap condition and VF should be 0
        m.varReg(0x1, 0x8A);
        decoder.decode((short) 0x8015);
        // since v0 < v1 VF should be set to 0
        // 0x65 - 0x8a = -0x25
        assertEquals(0, m.getV()[0xF]);
        assertEquals((byte) -0x25, m.getV()[0]);
    }

    @Test
    public void subtract8XY7() {
        m.varReg(0x0, 0x1F);
        m.varReg(0x1, 0x4C);
        decoder.decode((short) 0x8017);
        // 0x4C - 0x1F = 0x2D
        // since v1 > v0 VF should be set to 1
        assertEquals(1, m.getV()[0xF]);
        assertEquals((byte) 0x2D, m.getV()[0]);

        // this instruction can result in negative Bytes
        // lets see what happens with them:
        m.varReg(0x0, -0x65);
        m.varReg(0x1, 0x8A);
        decoder.decode((short) 0x8017);
        // -0x65 (VX)
        // 10011011 HEXADECIMAL 8-BIT
        // unsigned	9B (decimal: 155)
        // signed	-65 (decimal: -101)
        //
        // 0x8a (VY)
        // 10001010 HEXADECIMAL 8-BIT
        // unsigned	8A (decimal: 138)
        // signed	-76 (decimal: -118)
        //
        // VF is 1 if VY > VX, here it stays 0
        // 0x8a - (-0x65) = 0xEF
        assertEquals(0, m.getV()[0xF]);
        assertEquals((byte) 0xEF, m.getV()[0]);
    }

    @Test
    public void shiftRight8XY6() {
        m.varReg(0, 0b0001001); // 0x09
        decoder.decode((short) 0x8006);
        // set VF to 1 if shifted bit was 1, here it should be
        assertEquals(1, m.getV()[0xF]);
        // also divides the value in VX by 2
        assertEquals(0x4, m.getV()[0]);

        m.varReg(0, 0b0011000); // 0x18
        decoder.decode((short) 0x8006);
        // set VF to 1 if shifted bit was 1, here it should'nt be
        assertEquals(0, m.getV()[0xF]);
        assertEquals(0xC, m.getV()[0]);
    }

    @Test
    public void shiftLeft8XYE() {
        m.varReg(0, 0b0001001); // 0x09
        decoder.decode((short) 0x800E);
        // set VF to 1 if shifted bit was 1, here it should'nt be
        assertEquals(0, m.getV()[0xF]);
        // also multiplies the value in VX by 2
        assertEquals(0x12, m.getV()[0]);

        m.varReg(0, 0b11001001); // 0xC9
        decoder.decode((short) 0x800E);
        // set VF to 1 if shifted bit was 1, here it should be
        assertEquals(1, m.getV()[0xF]);
        // after multiplication val should be 0x192, but in 8-bit:
        assertEquals((byte) 0x92, m.getV()[0]);
    }

    @Test
    public void skipIfKeyEqualEX9E() {
        // set pc to 0x300
        m.setPc((short) 0x300);
        // load instruction 0xE49E to ram at 0x300 - 0x301
        byte[] ram = m.getRam();
        ram[0x300] = (byte) 0xE4;
        ram[0x301] = (byte) 0x9E;
        m.setRam(ram);

        keys.getKeys()[0xF] = true; // simulate F key pressed
        m.varReg(4, 0xF); // set F key in V4

        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we had equal keys we skip next instruction
        // by incrementing pc twice
        assertEquals(0x304, m.getPc());
        assertNotEquals(0x302, m.getPc());

        // reset pc to 0x300
        m.setPc((short) 0x300);
        m.varReg(4, 0xA); // set A key in V4

        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we have NOT equal keys we don't skip next instruction
        // A is currently not pressed on keyboard, only F
        assertEquals(0x302, m.getPc());
        assertNotEquals(0x304, m.getPc());
    }

    @Test
    public void skipIfNotKeyEqualEXA1() {
        // set pc to 0x300
        m.setPc((short) 0x300);
        // load instruction 0xE4A1 to ram at 0x300 - 0x301
        byte[] ram = m.getRam();
        ram[0x300] = (byte) 0xE4;
        ram[0x301] = (byte) 0xA1;
        m.setRam(ram);

        keys.getKeys()[0xF] = true; // simulate F key pressed
        m.varReg(4, 0xF); // set F key in V4

        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we have equal keys we DON'T skip
        assertEquals(0x302, m.getPc());
        assertNotEquals(0x304, m.getPc());

        // reset pc to 0x300
        m.setPc((short) 0x300);
        m.varReg(4, 0xA); // set A key in V4

        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since we have NOT equal keys we skip next instruction
        assertEquals(0x304, m.getPc());
        assertNotEquals(0x302, m.getPc());
    }

    @Test
    public void getKeyFX0A() {
        // set pc to 0x300
        m.setPc((short) 0x300);
        // load instruction 0xF40A to ram at 0x300 - 0x301
        byte[] ram = m.getRam();
        ram[0x300] = (byte) 0xF4;
        ram[0x301] = (byte) 0x0A;
        m.setRam(ram);

        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since there's no key press, we stay in same pc
        assertEquals(0x300, m.getPc());
        assertNotEquals(0x302, m.getPc());

        // we can try it in a loop like a rom would just wait for a key
        for (int i = 0; i < 20; i++) {
            fetcher.fetch();
            decoder.decode(fetcher.getOpcode());
        }
        // still in same instruction
        assertEquals(0x300, m.getPc());

        keys.getKeys()[0xC] = true; // simulate C key pressed
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
        // since now, we have a keypress C button
        // we should have C in VX and pc should be incremented
        assertEquals(0x302, m.getPc());
        assertEquals(0xC, m.getV()[4]);
    }

    @Test
    public void drawDXYN() {
        // let's test drawing with simple sprite at 0x0 coords
        // first set 0 x 0 coords to v0 and v1
        m.varReg(0, 0);
        m.varReg(1, 0);
        // check VF is 0
        assertEquals(0, m.getV()[0XF]);
        // generate sprite data to ram
        byte[] ram = m.getRam();
        ram[0x300] = (byte) 0b11111110;
        ram[0x301] = (byte) 0b00011000;
        m.setRam(ram);
        // set index register to sprite data location
        m.setI((short) 0x300);
        // display should be clear at 0x0
        assertFalse(decoder.getDisplay().getDisplay()[0][0]);
        // instruction with sprite height of 2
        decoder.decode((short) 0xD012);
        // we drew on empty screen, so VF should still be 0
        assertEquals(0, m.getV()[0XF]);
        // drawn sprite should be starting from origin 0 x 0
        // *******
        //    **
        // we can test that each of these pixels is on or off:
        assertTrue(decoder.getDisplay().getDisplay()[0][0]);
        assertTrue(decoder.getDisplay().getDisplay()[1][0]);
        assertTrue(decoder.getDisplay().getDisplay()[2][0]);
        assertTrue(decoder.getDisplay().getDisplay()[3][0]);
        assertTrue(decoder.getDisplay().getDisplay()[4][0]);
        assertTrue(decoder.getDisplay().getDisplay()[5][0]);
        assertTrue(decoder.getDisplay().getDisplay()[6][0]);
        assertFalse(decoder.getDisplay().getDisplay()[7][0]);

        assertFalse(decoder.getDisplay().getDisplay()[0][1]);
        assertFalse(decoder.getDisplay().getDisplay()[1][1]);
        assertFalse(decoder.getDisplay().getDisplay()[2][1]);
        assertTrue(decoder.getDisplay().getDisplay()[3][1]);
        assertTrue(decoder.getDisplay().getDisplay()[4][1]);
        assertFalse(decoder.getDisplay().getDisplay()[5][1]);
        assertFalse(decoder.getDisplay().getDisplay()[6][1]);
        assertFalse(decoder.getDisplay().getDisplay()[7][1]);

        // now we want to change the drawn sprite
        // to look like this:
        // ***  ***
        // *  **  *
        // edit the sprite data in ram
        ram[0x300] = (byte) 0b00011001;
        ram[0x301] = (byte) 0b10000001;
        decoder.decode((short) 0xD012);
        // this time we erased some pixels, so VF should be 1
        assertEquals(1, m.getV()[0XF]);
        // then we can again check each pixel one by one
        assertTrue(decoder.getDisplay().getDisplay()[0][0]);
        assertTrue(decoder.getDisplay().getDisplay()[1][0]);
        assertTrue(decoder.getDisplay().getDisplay()[2][0]);
        assertFalse(decoder.getDisplay().getDisplay()[3][0]);
        assertFalse(decoder.getDisplay().getDisplay()[4][0]);
        assertTrue(decoder.getDisplay().getDisplay()[5][0]);
        assertTrue(decoder.getDisplay().getDisplay()[6][0]);
        assertTrue(decoder.getDisplay().getDisplay()[7][0]);

        assertTrue(decoder.getDisplay().getDisplay()[0][1]);
        assertFalse(decoder.getDisplay().getDisplay()[1][1]);
        assertFalse(decoder.getDisplay().getDisplay()[2][1]);
        assertTrue(decoder.getDisplay().getDisplay()[3][1]);
        assertTrue(decoder.getDisplay().getDisplay()[4][1]);
        assertFalse(decoder.getDisplay().getDisplay()[5][1]);
        assertFalse(decoder.getDisplay().getDisplay()[6][1]);
        assertTrue(decoder.getDisplay().getDisplay()[7][1]);

        // finally we can test clear screen instruction 00E0:
        decoder.decode((short) 0x00E0);
        // this should just clear the whole screen, lets test
        // every pixel on the screen that they are off
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 32; j++) {
                assertFalse(decoder.getDisplay().getDisplay()[i][j]);
            }
        }
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
