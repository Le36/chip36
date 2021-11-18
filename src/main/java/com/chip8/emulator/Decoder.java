package com.chip8.emulator;

import com.chip8.display.ConsoleDisplay;

public class Decoder {

    private ConsoleDisplay display;
    private Memory m;
    private Fetcher fetcher;

    public Decoder(Memory m, Fetcher fetcher) {
        this.display = new ConsoleDisplay();
        this.m = m;
        this.fetcher = fetcher;
    }

    public void decode(short opcode) {
        switch (opcode) {
            case 0x00E0: // clears display
                display.clearDisplay();
                //System.out.println("clear");
                return;
        }

        switch (opcode & 0xF000) {
            case 0x1000: // jump, sets the PC to NNN | 1NNN
                m.setPC((short) (opcode & 0x0FFF));
                //System.out.println("jump");
                return;
            case 0x6000: // Set, sets V(x) = (NN) | 6xNN
                //System.out.println("(opcode & 0x0F00) >> 8)  " + ((opcode & 0x0F00) >> 8) + "\nopcode & 0x00FF  " + (opcode & 0x00FF));
                m.varReg((opcode & 0x0F00) >> 8, opcode & 0x00FF);
                //System.out.println("set");
                return;
            case 0x7000: // Add, adds V(x) = V(x) + (NN) | 7xNN
                m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x0F00) >> 8] + opcode & 0x00FF);
                //System.out.println("add");
                return;
            case 0xA000: // Sets index to NNN | INNN
                m.setI((short) (opcode & 0x0FFF));
                //System.out.println("set I" + (opcode & 0x0FFF));
                return;
            case 0xD000: // draws display, Dxyn
                // gets x and y coordinates for sprite
                int x = m.getV()[(opcode & 0x0F00) >> 8];
                int y = m.getV()[(opcode & 0x00F0) >> 4];
                // first nibble indicating height of the sprite
                int n = opcode & 0x000F;
                // variable register VF (V[15]) keeps track if there were any pixels erased, reset here
                m.varReg(0xF, 0);
                for (int i = 0; i < n; i++) {
                    // gets sprite row data from ram
                    byte spriteData = m.getRAM()[m.getI() + i];
                    for (int j = 0; j < 8; j++) {
                        // using binary mask to check each bit in sprite if that bit should be drawn or not
                        if ((spriteData & (0b10000000 >> j)) != 0) {
                            // modulo to wrap sprites around the screen
                            int xx = (x + j) % display.getWidth();
                            int yy = (y + i) % display.getHeight();
                            // if we erased pixel then set VF register to 1
                            if (display.getPixel(xx, yy)) {
                                m.varReg(0xF, 1);
                            }
                            // draws pixel by flipping it
                            display.drawPixel(xx, yy);
                        }
                    }
                }
                display.printDisplay();
                return;
        }
    }
}
