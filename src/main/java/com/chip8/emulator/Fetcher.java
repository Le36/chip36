package com.chip8.emulator;

import lombok.Data;


/**
 * handles fetching, gets the required opcode from ram with current program counter and manages timers
 */
@Data
public class Fetcher {

    private short opcode;
    private Memory m;
    private long currentTime;

    /**
     * @param memory memory that emulator is using
     */
    public Fetcher(Memory memory) {
        this.m = memory;
        this.currentTime = 0;
    }

    /**
     * fetches the opcode with PC. shifts first byte left 8 bits and does bitwise OR
     * to get correct result and also bitwise ANDs it with either 0xFF00 or 0x00FF to get leading zeros
     * increments pc and decrements timer
     */
    public void fetch() {
        if (m.getPc() >= 0xFFF) {
            this.setOpcode((short) 0x0000);
            return;
        }
        this.opcode = (short) (((m.getRam()[m.getPc()] << 8) & 0xFF00) | (m.getRam()[m.getPc() + 1] & 0x00FF));
        this.incrementPC();
        this.timerDecrement();
    }

    /**
     * can be used to seek the next instruction without incrementing pc or decrementing timers
     *
     * @param pc given program counter
     * @return returns the instruction from ram
     */
    public short seek(short pc) {
        if (pc >= 0xFFF) {
            return 0x0000;
        }
        return (short) (((m.getRam()[pc] << 8) & 0xFF00) | (m.getRam()[pc + 1] & 0x00FF));
    }

    /**
     * increments pc by 2
     */
    public void incrementPC() {
        m.setPc((short) (m.getPc() + 2));
    }

    /**
     * decrements pc by 2
     */
    public void decrementPC() {
        m.setPc((short) (m.getPc() - 2));
    }

    /**
     * accurate timers that are 60 hz just like in the original
     * decrements them every 17 milliseconds
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
        if (Byte.toUnsignedInt(m.getSoundTimer()) > 0) {
            m.setSoundTimer((byte) (m.getSoundTimer() - 1));
        }
    }
}


