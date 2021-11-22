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
        this.opcode = (short) (((m.getRam()[m.getPc()] << 8) & 0xFF00) | (m.getRam()[m.getPc() + 1] & 0x00FF));
        this.incrementPC();
        m.timerDecrement();
    }

    public void incrementPC() {
        m.setPc((short) (m.getPc() + 2));
    }

    public void decrementPC() {
        m.setPc((short) (m.getPc() - 2));
    }
}
