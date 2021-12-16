package com.chip8.emulator;

import com.chip8.configs.Configs;
import lombok.Data;

import java.util.Random;

/**
 * decodes given opcode and acts accordingly
 */
@Data
public class Decoder {

    private ConsoleDisplay display;
    private Memory m;
    private Fetcher fetcher;
    private PixelManager pixels;
    private short opcode;
    private Keys keys;
    private String detailed;
    private DecodeDetails d;
    private Configs c;


    public Decoder(Memory m, Fetcher fetcher, PixelManager pixels, Keys keys, Configs c) {
        this.display = new ConsoleDisplay();
        this.m = m;
        this.fetcher = fetcher;
        this.pixels = pixels;
        this.keys = keys;
        this.d = new DecodeDetails();
        this.c = c;
    }

    /**
     * decodes the instruction
     *
     * @param opcode opcode given by the fetcher
     */
    public void decode(short opcode) {
        d.update(opcode, m.getPc(), m.getI());
        this.opcode = opcode;
        switch (opcode) {
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
    }

    private void clearDisplay() {
        display.clearDisplay();
        pixels.clearDisplay();
        this.detailed = "Clears the display";
    }

    private void returnFromSubroutine() {
        // returns to program popping the pc from stack
        int stackSizeBefore = m.getStack().size();
        try {
            m.setPc(m.getStack().pop());
            this.detailed = d.detailReturnFrom(stackSizeBefore, m.getStack().size());
        } catch (Exception ignored) {
            this.detailed = "Error:\n00EE instruction, but stack is empty!";
        }
    }

    private void jumpAddress() {
        // jump, sets the PC to NNN | 1NNN
        short pcBefore = m.getPc();
        m.setPc((short) (opcode & 0x0FFF));
        this.detailed = d.detailJumpAddress(pcBefore);
    }

    private void callSubroutine() {
        // puts current pc to stack then jumps to NNN
        int stackSize = m.getStack().size();
        m.getStack().push(m.getPc());
        this.jumpAddress();
        this.detailed = d.detailCallSub(stackSize, m.getStack().size());
    }

    private void skipIfEqual() {
        // skip next instruction if V[x] == NN | 3XNN
        if (m.getV()[(opcode & 0x0F00) >> 8] == (byte) (opcode & 0x0FF)) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfEqual();
    }

    private void skipIfNotEqual() {
        // skip next instruction if V[x] != NN | 4XNN
        if (m.getV()[(opcode & 0x0F00) >> 8] != (byte) (opcode & 0x0FF)) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfNotEqual();
    }

    private void skipIfEqualRegisters() {
        // skip next instruction if V[x] == V[y] | 5XY0
        if (m.getV()[(opcode & 0x0F00) >> 8] == m.getV()[(opcode & 0x0F0) >> 4]) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfEqualReg();
    }

    private void setVarReg() {
        // Set, sets V(x) = (NN) | 6xNN
        m.varReg((opcode & 0x0F00) >> 8, opcode & 0x00FF);
        this.detailed = d.detailSetVarReg();
    }

    private void addVarReg() {
        // Add, adds V(x) = V(x) + (NN) | 7xNN
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte nn = (byte) (opcode & 0x00FF);
        m.varReg((opcode & 0x0F00) >> 8, (x + nn) & 0xFF);
        this.detailed = d.detailAddVarReg(x);
    }

    private void setVxToVy() {
        // sets v[x] to v[y]
        m.varReg((opcode & 0x0F00) >> 8, m.getV()[(opcode & 0x00F0) >> 4]);
        this.detailed = d.detailSetVxToVy();
    }

    private void binaryOr() {
        // bitwise or on v[x] and v[y] and stores that in v[x]
        byte x = (byte) ((opcode & 0x0F00) >> 8);
        byte y = (byte) ((opcode & 0x00F0) >> 4);
        byte xValue = m.getV()[x];
        byte yValue = m.getV()[y];
        m.varReg(x, m.getV()[x] | m.getV()[y]);
        this.detailed = d.detailBinary(xValue, yValue, "Does bitwise OR on V[", " | 0x");
    }

    private void binaryAnd() {
        // bitwise and on v[x] and v[y] and stores that in v[x]
        byte x = (byte) ((opcode & 0x0F00) >> 8);
        byte y = (byte) ((opcode & 0x00F0) >> 4);
        byte xValue = m.getV()[x];
        byte yValue = m.getV()[y];
        m.varReg(x, m.getV()[x] & m.getV()[y]);
        this.detailed = d.detailBinary(xValue, yValue, "Does bitwise AND on V[", " & 0x");
    }

    private void logicalXor() {
        // bitwise xor on v[x] and v[y] and stores that in v[x]
        byte x = (byte) ((opcode & 0x0F00) >> 8);
        byte y = (byte) ((opcode & 0x00F0) >> 4);
        byte xValue = m.getV()[x];
        byte yValue = m.getV()[y];
        m.varReg(x, m.getV()[x] ^ m.getV()[y]);
        this.detailed = d.detailBinary(xValue, yValue, "Does bitwise XOR on V[", " ^ 0x");
    }

    private void addVxVy() {
        // sets v[x] = v[x] + v[y], if overflow then v[0xF] is set to 1 else to 0
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if (Byte.toUnsignedInt(x) + Byte.toUnsignedInt(y) > Byte.toUnsignedInt((byte) 0xFF)) {
            m.varReg(0xF, 1);
            m.varReg((opcode & 0x0F00) >> 8, (x + y) & 0xFF);
            d.setState(true);
        } else {
            m.varReg(0xF, 0);
            m.varReg((opcode & 0x0F00) >> 8, x + y);
        }
        this.detailed = d.detailAddVxVy(x, y);
    }

    private void subtract() {
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        if ((opcode & 0x00F) == 0x5) {
            subtract5(x, y);
        } else if ((opcode & 0x00F) == 0x7) {
            subtract7(x, y);
        }
    }

    private void subtract5(byte x, byte y) {
        // sets v[x] to v[x] - v[y], if v[x] > v[y] then v[0xF] set to 1, else 0
        if (Byte.toUnsignedInt(x) > Byte.toUnsignedInt(y)) {
            m.varReg(0xF, 1);
            d.setState(true);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, x - y);
        String xValue = Integer.toHexString((x & 0xFF)).toUpperCase();
        String yValue = Integer.toHexString((y & 0xFF)).toUpperCase();
        this.detailed = d.detailSubtract5(y, x, yValue, xValue);
    }

    private void subtract7(byte x, byte y) {
        // sets v[x] to v[y] - v[x], if v[y] > v[x] then v[0xF] set to 1, else 0
        if (Byte.toUnsignedInt(y) > Byte.toUnsignedInt(x)) {
            m.varReg(0xF, 1);
            d.setState(true);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, y - x);
        String xValue = Integer.toHexString((x & 0xFF)).toUpperCase();
        String yValue = Integer.toHexString((y & 0xFF)).toUpperCase();
        this.detailed = d.detailSubtract7(x, y, xValue, yValue);
    }

    private void shiftRight() {
        // shifts v[x] 1 bit to right, if the shifted bit was 1 then sets v[0xF] to 1
        // else to 0, after this v[x] is divided by 2
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        if ((x & 0x1) == 1) {
            m.varReg(0xF, 1);
            d.setState(true);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, Byte.toUnsignedInt(x) / 2);
        this.detailed = d.detailShiftRight(x);
    }


    private void shiftLeft() {
        // shifts v[x] 1 bit to left, if the shifted bit was 1 then sets v[0xF] to 1
        // else to 0, after this v[x] is multiplied with 2
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        if ((x & 0b10000000) >> 7 == 1) {
            d.setState(true);
            m.varReg(0xF, 1);
        } else {
            m.varReg(0xF, 0);
        }
        m.varReg((opcode & 0x0F00) >> 8, Byte.toUnsignedInt(x) * 2);
        this.detailed = d.detailShiftLeft(x);
    }

    private void skipIfNotEqualRegisters() {
        // skip next instruction if V[x] != V[y] | 5XY0
        if (m.getV()[(opcode & 0x0F00) >> 8] != m.getV()[(opcode & 0x0F0) >> 4]) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfNotEqReg();
    }

    private void setIndex() {
        // Sets index to NNN | ANNN
        m.setI((short) (opcode & 0x0FFF));
        this.detailed = d.detailSetIndex();
    }

    private void jumpWithOffset() {
        // jumps to NNN + v[0] | BNNN
        m.setPc((short) ((opcode & 0x0FFF) + Byte.toUnsignedInt(m.getV()[0])));
        this.detailed = d.detailJumpWithOff();
    }

    private void random() {
        // generates random number and binary AND's it with NN
        // then puts the result in V[x] | CXNN
        Random rand = new Random();
        m.varReg((opcode & 0x0F00) >> 8, rand.nextInt(256) & (opcode & 0x00FF));
        this.detailed = d.detailRandom();
    }


    private void drawDisplay() {
        pixels.clearSprite();
        // draws display, Dxyn
        // gets x and y coordinates for sprite
        byte x = m.getV()[(opcode & 0x0F00) >> 8];
        byte y = m.getV()[(opcode & 0x00F0) >> 4];
        // variable register VF (V[15]) keeps track if there were any pixels erased, reset here
        m.varReg(0xF, 0);
        draw(x, y);
        this.detailed = d.detailDrawDisplay();
        if (this.c.isPrintToConsole()) {
            display.printDisplay(c.getPrintSymbol());
        }
    }

    private void draw(byte x, byte y) {
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
                    pixels.drawSprite(j, i);
                }
            }
        }
    }

    private void skipIfKeyEqual() {
        // skips next instruction if pressed key equals key in v[x]
        if (keys.getKeys()[m.getV()[(opcode & 0x0F00) >> 8]]) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfKeyEq();
    }

    private void skipIfKeyNotEqual() {
        // skips next instruction if key pressed not equal to key in v[x]
        if (!keys.getKeys()[m.getV()[(opcode & 0x0F00) >> 8]]) {
            fetcher.incrementPC();
            d.setState(true);
        }
        this.detailed = d.detailSkipIfKeyNotEq();
    }

    private void setVxToDelay() {
        // sets v[x] to delay timer
        m.varReg((opcode & 0x0F00) >> 8, m.getDelayTimer());
        this.detailed = d.detailSetVxToDetail();
    }

    private void getKey() {
        // waits for key press by decrementing pc, staying in same instruction.
        // when key press -> set pressed key to v[x] and increment pc
        for (byte b = 0x0; b <= 0xF; b++) {
            if (keys.getKeys()[b]) {
                m.varReg((opcode & 0x0F00) >> 8, b);
                return;
            }
        }
        fetcher.decrementPC();
        this.detailed = d.detailGetKey();
    }


    private void setDelayToVx() {
        // sets delay to v[x]
        m.setDelayTimer(m.getV()[(opcode & 0x0F00) >> 8]);
        this.detailed = d.detailSetDelayToVx();
    }


    private void setSoundToVx() {
        // sets sound to v[x]
        m.setSoundTimer(m.getV()[(opcode & 0x0F00) >> 8]);
        this.detailed = d.detailSetSoundToVx();
    }

    private void addToIndex() {
        // add v[x] to index
        m.setI((short) (m.getI() + Byte.toUnsignedInt(m.getV()[(opcode & 0x0F00) >> 8])));
        this.detailed = d.detailAddToIndex();
    }

    private void font() {
        // Font Fx29, x points to the character in V[x]
        // then I is set ram address that contains data for that character
        int x = ((opcode & 0x0F00) >> 8); // 0 - F
        m.setI((short) (0x50 + (5 * m.getV()[x])));
        this.detailed = d.detailFont();
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
        this.detailed = d.detailBcd(decimal);
    }

    private void registerDump() {
        // dump registers from V0 to Vx to ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++, tempI++) {
            ram[tempI] = m.getV()[i];
        }
        m.setRam(ram);
        this.detailed = d.detailRegisterDump();
    }

    private void registerFill() {
        // fill registers V0 to Vx from ram at I
        int tempI = m.getI();
        byte[] ram = m.getRam();
        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++, tempI++) {
            m.varReg(i, ram[tempI]);
        }
        this.detailed = d.detailRegisterFill();
    }
}