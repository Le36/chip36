package com.chip8.emulator;

import lombok.Data;

import java.util.ArrayDeque;

/**
 * memory for emulator, contains every register and ram
 */
@Data
public class Memory {

    private byte[] v; // 16x 8-bit variable registers
    private short i; // 16-bit index register
    private short pc; // program counter
    private byte[] ram; // 4 kB memory, 0x0 - 0x1FF reserved for font data etc.
    private byte delayTimer; // 8-bit delay timer
    private byte soundTimer; // 8-bit sound timer
    private ArrayDeque<Short> stack; // stack for 16-bit addresses used by 00EE and 2NNN

    /**
     * initializes 4 kB ram and sets pc to start at 0x200
     */
    public Memory() {
        this.ram = new byte[4096];
        this.pc = 0x200; // starts at 0x200 since it's where the roms first byte is loaded in RAM
        this.v = new byte[16];
        this.stack = new ArrayDeque<>();
    }

    /**
     * @param address ram address to set byte content
     * @param b       byte content for ram
     */
    public void initializeMemory(short address, byte b) {
        this.ram[address] = b;
    }

    /**
     * set value to one of 16 variable registers
     *
     * @param index index for register
     * @param value value for register
     */
    public void varReg(int index, int value) {
        this.v[index] = (byte) value;
    }

}
