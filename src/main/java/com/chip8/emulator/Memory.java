package com.chip8.emulator;

import lombok.Data;

import java.util.ArrayDeque;

@Data
public class Memory {

    private byte[] v; // 16x 8-bit variable registers
    private short i; // 16-bit index register
    private short pc; // program counter
    private byte[] ram; // 4 kB memory, 0x0 - 0x1FF reserved for font data etc.
    private byte delayTimer; // 8-bit delay timer
    private byte soundTimer; // 8-bit sound timer
    private ArrayDeque<Short> stack; // stack for 16-bit addresses used by 00EE and 2NNN

    public Memory() {
        this.ram = new byte[4096];
        this.pc = 0x200; // starts at 0x200 since it's where the roms first byte is loaded in RAM
        this.v = new byte[16];
        this.stack = new ArrayDeque<>();
    }

    public void initializeMemory(short address, byte b) {
        this.ram[address] = b;
    }

    public void varReg(int index, int value) {
        this.v[index] = (byte) value;
    }

}
