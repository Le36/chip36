package com.chip8.emulator;

import lombok.Data;

@Data
public class Fetcher {

    private short opcode;
    private Memory m;

    public Fetcher(Memory memory) {
        this.m = memory;
    }

    // fetches the opcode with PC, increments PC after
    // shifts first byte left 8 bits and does bitwise OR
    public void fetch() {
        this.opcode = (short) ((m.getRAM()[m.getPC()] << 8) | (m.getRAM()[m.getPC() + 1]));
        this.incrementPC();
    }

    public void incrementPC() {
        m.setPC((short) (m.getPC() + 2));
    }
}
