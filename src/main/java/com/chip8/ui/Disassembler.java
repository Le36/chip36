package com.chip8.ui;

import com.chip8.emulator.Fetcher;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.Data;

import static java.lang.Integer.toHexString;

/**
 * creates the ui element disassembler,
 * that shows the upcoming instructions from ram
 * pointer by program counter
 */
@Data
public class Disassembler extends ListView {

    private String seekString;
    private String x;
    private String y;
    private String nnn;
    private String nn;
    private String n;

    /**
     * gets the upcoming instructions from ram
     * and adds them to the listview
     *
     * @param pc current program counter
     * @param f  fetcher that gets the instruction from ram
     */
    public void update(short pc, Fetcher f) {
        this.getItems().clear();
        for (int i = 0; i < 10; i++) {
            short opcode = f.seek(pc);
            this.seek(opcode);
            String instruction = Integer.toHexString((opcode & 0xFFFF)).toUpperCase();
            String base = "0x";
            if (instruction.length() == 1) {
                base = "0x000";
            } else if (instruction.length() == 2) {
                base = "0x00";
            } else if (instruction.length() == 3) {
                base = "0x0";
            }
            this.getItems().add(base + instruction + " | " + this.getSeekString());
            pc += 2;
        }
    }

    /**
     * gets data for extended disassembler
     * and shows the content in loaded ram
     *
     * @param pc     current program counter
     * @param f      fetcher that gets the instruction from ram
     * @param scroll if scroll is enabled for view
     */
    public void updateFull(short pc, Fetcher f, boolean scroll, String symbol) {
        UiElements uiElements = new UiElements();
        this.getItems().clear();
        for (int i = 0; i < 0xFFF; i++) {
            BorderPane pane = new BorderPane();
            short opcode = f.seek((short) i);
            this.seek(opcode);
            String instruction = Integer.toHexString((opcode & 0xFFFF)).toUpperCase();
            if (instruction.length() == 1) {
                instruction = "000" + instruction;
            } else if (instruction.length() == 2) {
                instruction = "00" + instruction;
            } else if (instruction.length() == 3) {
                instruction = "0" + instruction;
            }
            StringBuilder temp = new StringBuilder();
            if (instruction.length() > 2) {
                temp = new StringBuilder(instruction.substring(0, 2));
                int h = Integer.parseInt(temp.toString(), 16);
                byte b = (byte) (h ^ 0x100);
                temp = new StringBuilder();
                for (int j = 0; j < 8; j++) {
                    if (((b & (0b10000000 >>> j)) != 0)) {
                        temp.append(symbol);
                    } else {
                        for (int m = 0; m < symbol.length(); m++) {
                            temp.append(" ");
                        }
                    }
                }
            }
            pane.setLeft(uiElements.makeLabel("RAM: 0x" + Integer.toString(i, 16).toUpperCase() + " | 0x" + instruction + " | " + this.getSeekString(), LabelType.SMALL));
            pane.setRight(uiElements.makeLabel(temp.toString(), LabelType.SMALL));

            this.getItems().add(pane);
        }
        if (scroll) this.scrollTo(pc);
    }

    private void seek(short opcode) {
        this.x = toHexString((((opcode & 0x0F00) >> 8) & 0xF)).toUpperCase();
        this.y = toHexString((((opcode & 0x00F0) >> 4) & 0xF)).toUpperCase();
        this.nnn = toHexString(((opcode & 0x0FFF) & 0xFFF)).toUpperCase();
        this.nn = toHexString(((opcode & 0x00FF) & 0xFF)).toUpperCase();
        this.n = toHexString(((opcode & 0x000F) & 0xF)).toUpperCase();

        switch (opcode) {
            case 0x0000:
                this.seekString = "0x0000: Empty memory";
                return;
            case 0x00E0: // 00E0
                this.seekString = "00E0: Clears the display";
                return;
            case 0x00EE: // 00EE
                this.seekString = "00EE: Returns from a subroutine";
                return;
            case 0x00FB: // 00FB -- Super chip
                this.seekString = "00FB: Scroll right display by 4 pixels";
                return;
            case 0x00FC: // 00FC -- Super chip
                this.seekString = "00FC: Scroll left display by 4 pixels";
                return;
            case 0x00FD: // 00FD -- Super chip
                this.seekString = "00FD: Exit emulator";
                return;
            case 0x00FE: // 00FE -- Super chip
                this.seekString = "00FE: Set LoRes mode";
                return;
            case 0x00FF: // 00FF -- Super chip
                this.seekString = "00FF: Set HiRes mode";
                return;
            case 0x0230: // 0230 -- hires mode clear screen
                this.seekString = "0230: Clear display HiRes mode";
                return;
            case (short) 0xF000: // F000 -- XO-Chip
                this.seekString = "F000: Set next opcode to index register";
                return;
            case (short) 0xF002: // F002 -- XO-Chip
                this.seekString = "F002: Fill audio buffer";
                return;
        }
        switch (opcode & 0xFFF0) {
            case 0x00C0: // 00CN -- Super chip
                this.seekString = "00CN: Scroll down display by " + n + " pixels";
                return;
            case 0x00D0: // 00DN -- XO chip
                this.seekString = "00DN: Scroll up display by " + n + " pixels";
                return;
        }
        switch (opcode & 0xF0FF) {
            case 0xE09E: // EX9E
                this.seekString = "EX9E: Skip if key contained in V[" + this.x + "] is pressed";
                return;
            case 0xE0A1: // EXA1
                this.seekString = "EXA1: Skip if key contained in V[" + this.x + "] not pressed";
                return;
            case 0xF001: // FN01
                this.seekString = "FN01: Set drawing plane to " + this.x;
                return;
            case 0xF007: // FX07
                this.seekString = "FX07: Set V[" + this.x + "] to Delay timer value";
                return;
            case 0xF00A: // FX0A
                this.seekString = "FX0A: Wait for key press and store key in V[" + this.x + "]";
                return;
            case 0xF015: // FX15
                this.seekString = "FX15: Set delay timer to V[" + this.x + "]";
                return;
            case 0xF018: // FX18
                this.seekString = "FX18: Set sound timer to V[" + this.x + "]";
                return;
            case 0xF01E: // FX1E
                this.seekString = "FX1E: Add V[" + this.x + "] to index register";
                return;
            case 0xF029: //FX29
                this.seekString = "FX29: Set index to location of a key in V[" + this.x + "]";
                return;
            case 0xF033: // FX33
                this.seekString = "FX33: BCD, store BCD in V[" + this.x + "] to index locations";
                return;
            case 0xF03A: // FX3A
                this.seekString = "FX3A: Set pitch register to V[" + this.x + "]";
                return;
            case 0xF055: // FX55
                this.seekString = "FX55: Dump registers V[0] - V[" + this.x + "] to index locations";
                return;
            case 0xF065: // FX65
                this.seekString = "FX65: Fill registers V[0] - V[" + this.x + "] from index locations";
                return;
            case 0xF075: // FX75 -- Super chip
                this.seekString = "FX75: Dump V[0] - V[" + this.x + "] to RPL[0] - RPL[" + this.x + "]";
                return;
            case 0xF085: // FX85 -- Super chip
                this.seekString = "FX85: Fill V[0] - V[" + this.x + "] from RPL[0] - RPL[" + this.x + "]";
                return;
        }
        switch (opcode & 0xF000) {
            case 0x1000: // 1NNN
                this.seekString = "1NNN: Jump to location 0x" + this.nnn;
                return;
            case 0x2000: // 2NNN
                this.seekString = "2NNN: Calls a subroutine at 0x" + this.nnn;
                return;
            case 0x3000: // 3XNN
                this.seekString = "3XNN: Skip next instruction if V[" + this.x + "] == 0x" + this.nn;
                return;
            case 0x4000: // 4XNN
                this.seekString = "4XNN: Skip next instruction if V[" + this.x + "] != 0x" + this.nn;
                return;
            case 0x6000: // 6XNN
                this.seekString = "6XNN: Set V[" + this.x + "] to 0x" + this.nn;
                return;
            case 0x7000: // 7XNN
                this.seekString = "7XNN: Add 0x" + this.nn + " to V[" + this.x + "]";
                return;
            case 0x9000: // 9XY0
                this.seekString = "9XY0: Skip next instruction if V[" + this.x + "] != V[" + this.y + "]";
                return;
            case 0xA000: // ANNN
                this.seekString = "ANNN: Set index to 0x" + this.nnn;
                return;
            case 0xB000: // BNNN
                this.seekString = "BNNN: Jump with offset, 0x" + this.nnn + " + V[0]";
                return;
            case 0xC000: // CXNN
                this.seekString = "CXNN: Random, sets V[" + this.x + "] to random byte & 0x" + this.nn;
                return;
            case 0xD000: // DXYN
                this.seekString = "DXYN: Draw display";
                return;
        }
        switch (opcode & 0xF00F) {
            case 0x5000: // 5XY0
                this.seekString = "5XY0: Skip next instruction if V[" + this.x + "] == V[" + this.y + "]";
                return;
            case 0x5002: // 5XY2 -- XO-Chip
                this.seekString = "5XY2: Dump registers V[" + this.x + "] - V[" + this.y + "] to index locations";
                return;
            case 0x5003: // 5XY3 -- XO-Chip
                this.seekString = "5XY3: Fill registers V[" + this.x + "] - V[" + this.y + "] from index locations";
                return;
            case 0x8000: // 8XY0
                this.seekString = "8XY0: Set V[" + this.x + "] to V[" + this.y + "]";
                return;
            case 0x8001: // 8XY1
                this.seekString = "8XY1: Bitwise OR V[" + this.x + "] and V[" + this.y + "], set to V[" + this.x + "]";
                return;
            case 0x8002: // 8XY2
                this.seekString = "8XY1: Bitwise AND V[" + this.x + "] and V[" + this.y + "], set to V[" + this.x + "]";
                return;
            case 0x8003: // 8XY3
                this.seekString = "8XY1: Bitwise XOR V[" + this.x + "] and V[" + this.y + "], set to V[" + this.x + "]";
                return;
            case 0x8004: // 8XY4
                this.seekString = "8XY4: Add V[" + this.y + "] to V[" + this.x + "], if overflow VF = 1";
                return;
            case 0x8005: // 8XY5
                this.seekString = "8XY5: Subtract V[" + this.x + "] = V[" + this.x + "] - V[" + this.y + "]";
                return;
            case 0x8007: // 8XY7
                this.seekString = "8XY7: Subtract V[" + this.x + "] = V[" + this.y + "] - V[" + this.x + "]";
                return;
            case 0x8006: // 8XY6
                this.seekString = "8XY6: Shift Right and divide V[" + this.x + "] by 2";
                return;
            case 0x800E: // 8XYE
                this.seekString = "8XYE: Shift Left and multiply V[" + this.x + "] by 2";
                return;
        }
        this.seekString = "Unknown opcode!";
    }
}
