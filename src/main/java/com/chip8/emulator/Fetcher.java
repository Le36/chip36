package com.chip8.emulator;

import lombok.Data;


@Data
public class Fetcher {

    private short opcode;
    private Memory m;
    private long currentTime;

    public Fetcher(Memory memory) {
        this.m = memory;
        this.currentTime = 0;
    }

    // fetches the opcode with PC, increments PC after
    // shifts first byte left 8 bits and does bitwise OR
    public void fetch() {
        this.opcode = (short) (((m.getRam()[m.getPc()] << 8) & 0xFF00) | (m.getRam()[m.getPc() + 1] & 0x00FF));
        this.incrementPC();
        this.timerDecrement();
    }

    public short seek(short pc) {
        return (short) (((m.getRam()[pc] << 8) & 0xFF00) | (m.getRam()[pc + 1] & 0x00FF));
    }

    public void incrementPC() {
        m.setPc((short) (m.getPc() + 2));
    }

    public void decrementPC() {
        m.setPc((short) (m.getPc() - 2));
    }


    /**
     * Accurate timers that are 60 hz just like in the original
     */
    public void timerDecrement() {
        if (currentTime + 17 < System.currentTimeMillis()) {
            currentTime = System.currentTimeMillis();
        } else {
            return;
        }
        if (Byte.toUnsignedInt(m.getDelayTimer()) > 0) {
            m.setDelayTimer((byte) (m.getDelayTimer() - 1));
        }
        if (Byte.toUnsignedInt(m.getDelayTimer()) > 0) {
            m.setSoundTimer((byte) (m.getSoundTimer() - 1));
        }
    }

}


