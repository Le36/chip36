package com.chip8.emulator;

import lombok.Data;

@Data
public class Memory {

    private byte[] V; // 16x 8-bit variable registers
    private short I; // 16-bit index register
    private short PC; // program counter
    private byte[] RAM; // 4 kB memory, 0x0 - 0x1FF reserved for other purposes

    public Memory() {
        this.RAM = new byte[4096];
        this.PC = 0x200; // starts at 0x200 since it's where the roms first byte is loaded in RAM
        this.V = new byte[16];
    }

    public void initializeMemory(short address, byte b) {
        this.RAM[address] = b;
    }

    public void varReg(int index, int value) {
        this.V[index] = (byte) value;
        //System.out.println(index + " < index || value >" + value);
    }

}
