package com.chip8.emulator;

import com.chip8.ui.ConsoleDisplay;
import com.chip8.ui.PixelManager;
import lombok.Data;

import java.util.Random;

@Data
public class Decoder {

    private ConsoleDisplay display;
    private Memory m;
    private Fetcher fetcher;
    private PixelManager pixels;
    private short opcode;
    private Keys keys;
    private boolean seeking;
    private String seekString;

    public Decoder(Memory m, Fetcher fetcher, PixelManager pixels, Keys keys) {
        this.display = new ConsoleDisplay();
        this.m = m;
        this.fetcher = fetcher;
        this.pixels = pixels;
        this.keys = keys;
    }

    public void decode(short opcode, boolean seeking) {
        this.seeking = seeking;
        this.opcode = opcode;
        switch (opcode) {
            case 0x0000:
                if (seeking) {
                    this.seekString = "0x0000: Empty memory";
                }
                return;
            case 0x00E0: // 00E0
                this.clearDisplay();
                return;
            case 0x00EE: // 00EE
                this.returnFromSubroutine();
                return;
        }

        switch (opcode & 0xF0FF) {
            case 0xE09E: // EX9E
                this.skipIfKeyEqual();
                return;
            case 0xE0A1: // EXA1
                this.skipIfKeyNotEqual();
                return;
            case 0xF007: // FX07
                this.setVxToDelay();
                return;
            case 0xF00A: // FX0A
                this.getKey();
                return;
            case 0xF015: // FX15
                this.setDelayToVx();
                return;
            case 0xF018: // FX18
                this.setSoundToVx();
                return;
            case 0xF01E: // FX1E
                this.addToIndex();
                return;
            case 0xF029: //FX29
                this.font();
                return;
            case 0xF033: // FX33
                this.bcd();
                return;
            case 0xF055: // FX55
                this.registerDump();
                return;
            case 0xF065: // FX65
                this.registerFill();
                return;
        }
        switch (opcode & 0xF000) {
            case 0x1000: // 1NNN
                this.jumpAddress();
                return;
            case 0x2000: // 2NNN
                this.callSubroutine();
                return;
            case 0x3000: // 3XNN
                this.skipIfEqual();
                return;
            case 0x4000: // 4XNN
                this.skipIfNotEqual();
                return;
            case 0x5000: // 5XY0
                this.skipIfEqualRegisters();
                return;
            case 0x6000: // 6XNN
                this.setVarReg();
                return;
            case 0x7000: // 7XNN
                this.addVarReg();
                return;
            case 0x9000: // 9XY0
                this.skipIfNotEqualRegisters();
                return;
            case 0xA000: // ANNN
                this.setIndex();
                return;
            case 0xB000: // BNNN
                this.jumpWithOffset();
                return;
            case 0xC000: // CXNN
                this.random();
                return;
            case 0xD000: // DXYN
                this.drawDisplay();
                return;
        }

        switch (opcode & 0xF00F) {
            case 0x8000: // 8XY0
                this.setVxToVy();
                return;
            case 0x8001: // 8XY1
                this.binaryOr();
                return;
            case 0x8002: // 8XY2
                this.binaryAnd();
                return;
            case 0x8003: // 8XY3
                this.logicalXor();
                return;
            case 0x8004: // 8XY4
                this.addVxVy();
                return;
            case 0x8005: // 8XY5
            case 0x8007: // 8XY7
                this.subtract();
                return;
            case 0x8006: // 8XY6
                this.shiftRight();
                return;
            case 0x800E: // 8XYE
                this.shiftLeft();
                return;
        }
        if (seeking) {
            this.seekString = "Unknown opcode";
        } else {
            System.out.println("unknown opcode: " + Byte.toUnsignedInt((byte) opcode));
        }
    }


    private void clearDisplay() {
        if (seeking) {
            this.seekString = "00E0: Clears the display";
            return;
        }
        display.clearDisplay();
        pixels.clearDisplay();
    }

    private void returnFromSubroutine() {
        if (seeking) {
            this.seekString = "00EE: Returns from a subroutine";
            return;
        }
        // returns to program popping the pc from stack
        m.setPc(m.getStack().pop());
    }

    private void jumpAddress() {
        if (seeking) {
            this.seekString = "1NNN: Jump to location NNN";
            return;
        }
        // jump, sets the PC to NNN | 1NNN
        m.setPc((short) (opcode & 0x0FFF));
    }

    private void callSubroutine() {
        if (seeking) {
            this.seekString = "2NNN: Calls a subroutine at NNN";
            return;
        }
        // puts current pc to stack then jumps to NNN
        m.getStack().push(m.getPc());
        this.jumpAddress();
    }

    private void skipIfEqual() {
        if (seeking) {
            this.seekString = "3XNN: Skip next instruction if V[X] == NN";
            return;
        }
        // skip next instruction if V[x] == NN | 3XNN
        if (m.getV()[(opcode & 0x0F00) >> 8] == (opcode & 0x0FF)) {
            fetcher.incrementPC();
        }
    }

    private void skipIfNotEqual() {
        if (seeking) {
            this.seekString = "4XNN: Skip next instruction if V[X] != NN";
            return;
        }
        // skip next instruction if V[x] != NN | 4XNN
        if (m.getV()[(opcode & 0x0F00) >> 8] != (opcode & 0x0FF)) {
            fetcher.incrementPC();
        }
    }

    private void skipIfEqualRegisters() {
        if (seeking) {
            this.seekString = "5XY0: Skip next instruction if V[X] == V[Y]";
            return;
        }
        // skip next instruction if V[x] == V[y] | 5XY0
        if (m.getV()[(opcode & 0x0F00) >> 8] == m.getV()[(opcode & 0x0F0) >> 4]) {
            fetcher.incrementPC();
        }
    }

    private void setVarReg() {
        if (seeking) {
            this.seekString = "6XNN: Set V[X] to NN";
            return;
        }
        // Set, sets V(x) = (NN) | 6xNN
        m.varReg((opcode & 0x0F00) >> 8, opcode & 0x00FF);
    }

    private void addVarReg() {
        if (seeking) {
            this.seekString = "7XNN: Add NN to V[X], if overflow VF = 1";
            return;
        }
        // Add, adds V(x) = V(x) + (NN) | 7xNN
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte nn = (byte) (opcode & 0x00FF);
        if ((x + nn) < (byte) 0xFF) {
            m.varReg(0xF, 1);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, (x + nn) & 0xFF);
    }


    private void setVxToVy() {
        if (seeking) {
            this.seekString = "8XY0: Set V[X] to V[Y]";
            return;
        }
        // sets v[x] to v[y]
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x00F0) >> 4]);
    }

    private void binaryOr() {
        if (seeking) {
            this.seekString = "8XY1: Bitwise OR V[X] and V[Y], set to V[X]";
            return;
        }
        // bitwise or on v[x] and v[y] and stores that in v[x]
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x00F0) >> 4] | m.getV()[(opcode & 0x0F00) >> 8]);
    }

    private void binaryAnd() {
        if (seeking) {
            this.seekString = "8XY1: Bitwise AND V[X] and V[Y], set to V[X]";
            return;
        }
        // bitwise and on v[x] and v[y] and stores that in v[x]
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x00F0) >> 4] & m.getV()[(opcode & 0x0F00) >> 8]);
    }

    private void logicalXor() {
        if (seeking) {
            this.seekString = "8XY1: Bitwise XOR V[X] and V[Y], set to V[X]";
            return;
        }
        // bitwise xor on v[x] and v[y] and stores that in v[x]
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x00F0) >> 4] ^ m.getV()[(opcode & 0x0F00) >> 8]);
    }

    private void addVxVy() {
        if (seeking) {
            this.seekString = "8XY4: Add V[Y] to V[X], if overflow VF = 1";
            return;
        }
        // sets v[x] = v[x] + v[y], if overflow then v[0xF] is set to 1 else to 0
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if ((x + y) > (byte) 0xFF) {
            m.varReg(0xF, 1);
            m.varReg((opcode & 0x0F00) >> 8, (x + y) & 0xFF);
        } else {
            m.varReg(0xF, 0);
            m.varReg((opcode & 0x0F00) >> 8, x + y);
        }
    }

    private void subtract() {
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if ((opcode & 0x00F) == 0x5) {
            if (seeking) {
                this.seekString = "8XY5: Subtract V[X] = V[X] - V[Y]";
                return;
            }
            // sets v[x] to v[x] - v[y], if v[x] > v[y] then v[0xF] set to 1, else 0
            if (x > y) {
                m.varReg(0xF, 1);
            } else {
                m.varReg(0xF, 0);
            }
            m.varReg((opcode & 0x0F00) >> 8, x - y);
        } else if ((opcode & 0x00F) == 0x7) {
            if (seeking) {
                this.seekString = "8XY7: Subtract V[X] = V[Y] - V[X]";
                return;
            }
            // sets v[x] to v[y] - v[x], if v[y] > v[x] then v[0xF] set to 1, else 0
            if (y > x) {
                m.varReg(0xF, 1);
            } else {
                m.varReg(0xF, 0);
            }
            m.varReg((opcode & 0x0F00) >> 8, y - x);
        }
    }

    private void shiftRight() {
        if (seeking) {
            this.seekString = "8XY6: Shift Right";
            return;
        }
        // sets v[x] to v[y] then shifts v[x] 1 bit to right, if the shifted bit was 1
        // then sets v[0xF] to 1, else to 0
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if ((y & 0x1) == 1) {
            m.varReg(0xF, 1);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, y >> 1);
    }

    private void shiftLeft() {
        if (seeking) {
            this.seekString = "8XYE: Shift Left";
            return;
        }
        // sets v[x] to v[y] then shifts v[x] 1 bit to left, if the shifted bit was 1
        // then sets v[0xF] to 1, else to 0
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if ((y & 0b10000000) >> 7 == 1) {
            m.varReg(0xF, 1);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, y << 1);
    }

    private void skipIfNotEqualRegisters() {
        if (seeking) {
            this.seekString = "9XY0: Skip next instruction if V[X] != V[Y]";
            return;
        }
        // skip next instruction if V[x] != V[y] | 5XY0
        if (m.getV()[(opcode & 0x0F00) >> 8] != m.getV()[(opcode & 0x0F0) >> 4]) {
            fetcher.incrementPC();
        }
    }

    private void setIndex() {
        if (seeking) {
            this.seekString = "ANNN: Set index to NNN";
            return;
        }
        // Sets index to NNN | ANNN
        m.setI((short) (opcode & 0x0FFF));
    }

    private void jumpWithOffset() {
        if (seeking) {
            this.seekString = "BNNN: Jump with offset, NNN + V[0]";
            return;
        }
        // jumps to NNN + v[0] | BNNN
        m.setPc((short) ((opcode & 0x0FFF) + m.getV()[0]));
        // super-chip style BXNN
        //m.setPc((short) ((opcode & 0x0FFF) + m.getV()[(0x0F00 & opcode) >> 8]));
    }

    private void random() {
        if (seeking) {
            this.seekString = "CXNN: Random, sets V[X] to random byte & NN";
            return;
        }
        // generates random number and binary AND's it with NN
        // then puts the result in V[x] | CXNN
        Random rand = new Random();
        m.varReg((opcode & 0x0F00) >> 8, rand.nextInt(256) & (opcode & 0x00FF));
    }

    private void drawDisplay() {
        if (seeking) {
            this.seekString = "DXYN: Draw display";
            return;
        }
        // draws display, Dxyn
        // gets x and y coordinates for sprite
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
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
                    int xx = Math.abs((x + j) % display.getWidth());
                    int yy = Math.abs((y + i) % display.getHeight());
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
        //display.printDisplay();
    }

    private void skipIfKeyEqual() {
        if (seeking) {
            this.seekString = "EX9E: Skip if key contained in V[X] is pressed";
            return;
        }
        // skips next instruction if pressed key equals key in v[x]
        if (keys.getKeys()[m.getV()[(opcode & 0x0F00) >> 8]]) {
            fetcher.incrementPC();
        }
    }

    private void skipIfKeyNotEqual() {
        if (seeking) {
            this.seekString = "EXA1: Skip if key contained in V[X] not pressed";
            return;
        }
        // skips next instruction if key pressed not equal to key in v[x]
        if (!keys.getKeys()[m.getV()[(opcode & 0x0F00) >> 8]]) {
            fetcher.incrementPC();
        }
    }

    private void setVxToDelay() {
        if (seeking) {
            this.seekString = "FX07: Set V[X] to Delay timer value";
            return;
        }
        // sets v[x] to delay timer
        m.varReg((opcode & 0x0F00) >> 8, m.getDelayTimer());
    }

    private void getKey() {
        if (seeking) {
            this.seekString = "FX0A: Wait for key press and store key in V[X]";
            return;
        }
        // waits for key press by decrementing pc, staying in same instruction.
        // when key press -> set pressed key to v[x] and increment pc
        for (byte b = 0x0; b <= 0xF; b++) {
            if (keys.getKeys()[b]) {
                m.varReg((opcode & 0x0F00) >> 8, b);
                return;
            }
        }
        fetcher.decrementPC();
    }

    private void setDelayToVx() {
        if (seeking) {
            this.seekString = "FX15: Set delay timer to V[X]";
            return;
        }
        // sets delay to v[x]
        m.setDelayTimer(m.getV()[(opcode & 0x0F00) >> 8]);
    }

    private void setSoundToVx() {
        if (seeking) {
            this.seekString = "FX18: Set sound timer to V[X]";
            return;
        }
        // sets sound to v[x]
        m.setSoundTimer(m.getV()[(opcode & 0x0F00) >> 8]);
    }

    private void addToIndex() {
        if (seeking) {
            this.seekString = "FX1E: Add V[X] to index register";
            return;
        }
        // add v[x] to index
        m.setI((short) (m.getI() + Byte.toUnsignedInt(m.getV()[(opcode & 0x0F00) >> 8])));
    }

    private void font() {
        if (seeking) {
            this.seekString = "FX29: Set index to location of a key in V[X]";
            return;
        }
        // Font Fx29, x points to the character in V[x]
        // then I is set ram address that contains data for that character
        int x = ((opcode & 0x0F00) >> 8); // 0 - F
        m.setI((short) (0x50 + (5 * m.getV()[x])));
    }

    private void bcd() {
        if (seeking) {
            this.seekString = "FX33: BCD, store BCD in V[X] to index locations";
            return;
        }
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
        if (seeking) {
            this.seekString = "FX55: Dump registers V[0] - V[X] to index locations";
            return;
        }
        // dump registers from V0 to Vx to ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++, tempI++) {
            ram[tempI] = m.getV()[i];
        }
        m.setRam(ram);
    }

    private void registerFill() {
        if (seeking) {
            this.seekString = "FX65: Fill registers V[0] - V[X] from index locations";
            return;
        }
        // fill registers V0 to Vx from ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++, tempI++) {
            m.varReg(i, ram[tempI]);
        }
    }
}
