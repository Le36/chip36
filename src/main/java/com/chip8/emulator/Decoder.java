package com.chip8.emulator;

import com.chip8.ui.ConsoleDisplay;
import com.chip8.ui.PixelManager;

public class Decoder {

    private ConsoleDisplay display;
    private Memory m;
    private Fetcher fetcher;
    private PixelManager pixels;
    private short opcode;

    public Decoder(Memory m, Fetcher fetcher, PixelManager pixels) {
        this.display = new ConsoleDisplay();
        this.m = m;
        this.fetcher = fetcher;
        this.pixels = pixels;
    }

    public void decode(short opcode) {
        this.opcode = opcode;
        if (opcode == 0x00E0) {
            this.clearDisplay();
            return;
        }

        switch (opcode & 0xF0FF) {
            case 0xF029:
                this.font();
                return;
            case 0xF033:
                this.bcd();
                return;
            case 0xF055:
                this.registerDump();
                return;
            case 0xF065:
                this.registerFill();
                return;
        }
        switch (opcode & 0xF000) {
            case 0x1000:
                this.jumpAddress();
                return;
            case 0x6000:
                this.setVarReg();
                return;
            case 0x7000:
                this.addVarReg();
                return;
            case 0xA000:
                this.setIndex();
                return;
            case 0xD000:
                this.drawDisplay();
                return;
        }
    }

    private void clearDisplay() {
        display.clearDisplay();
        pixels.clearDisplay();
    }

    private void jumpAddress() {
        // jump, sets the PC to NNN | 1NNN
        m.setPc((short) (opcode & 0x0FFF));
    }

    private void setVarReg() {
        // Set, sets V(x) = (NN) | 6xNN
        m.varReg((opcode & 0x0F00) >> 8, opcode & 0x00FF);
    }

    private void addVarReg() {
        // Add, adds V(x) = V(x) + (NN) | 7xNN
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x0F00) >> 8] + opcode & 0x00FF);
    }

    private void setIndex() {
        // Sets index to NNN | INNN
        m.setI((short) (opcode & 0x0FFF));
    }

    private void drawDisplay() {
        // draws display, Dxyn
        // gets x and y coordinates for sprite
        int x = m.getV()[(opcode & 0x0F00) >> 8];
        int y = m.getV()[(opcode & 0x00F0) >> 4];
        // variable register VF (V[15]) keeps track if there were any pixels erased, reset here
        m.varReg(0xF, 0);
        // first nibble indicating height of the sprite
        for (int i = 0; i < (opcode & 0x000F); i++) {
            // gets sprite row data from ram
            byte spriteData = m.getRam()[m.getI() + i];
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
                    pixels.draw(xx, yy);
                }
            }
        }
        display.printDisplay();
    }

    private void font() {
        // Font Fx29, x points to the character in V[x]
        // then I is set ram address that contains data for that character
        int x = ((opcode & 0x0F00) >> 8); // 0 - F
        m.setI((short) (0x50 + (5 * m.getV()[x])));
    }

    private void bcd() {
        // converts value in Vx to decimal 0 - 255 and then inserts
        // the decimal in BCD format to ram pointed by I
        // first decimal going to I + 2, second to I + 1 and third to I
        int decimal = Byte.toUnsignedInt(m.getV()[((opcode & 0x0F00) >> 8)]);
        byte[] ram = m.getRam();
        ram[m.getI() + 2] = (byte) (decimal % 10);
        decimal = decimal / 10;
        ram[m.getI() + 1] = (byte) (decimal % 10);
        decimal = decimal / 10;
        ram[m.getI()] = (byte) (decimal % 10);
        m.setRam(ram);
    }

    private void registerDump() {
        // dumps registers from V0 to Vx to ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i < ((opcode & 0x0F00) >> 8); i++, tempI++) {
            ram[tempI] = m.getV()[i];
        }
        m.setRam(ram);
    }

    private void registerFill() {
        // fills registers V0 to Vx from ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i < ((opcode & 0x0F00) >> 8); i++, tempI++) {
            m.varReg(i, ram[tempI]);
        }
    }
}
