package com.chip8.emulator;

import lombok.Data;

/**
 * after decoding the instruction, generates a text
 * to explain what the instruction did, used by ui
 */
@Data
public class DecodeDetails {

    private short opcode;
    private String x;
    private String y;
    private String nnn;
    private String nn;
    private String n;
    private String iBefore;
    private String i;
    private String pc;
    private boolean state;

    /**
     * updates emulator memory states to string and hex format,
     * these are used by the methods here
     *
     * @param opcode current opcode
     * @param pc     current program counter
     * @param i      current index register
     */
    public void update(short opcode, short pc, short i) {
        this.state = false;
        this.x = Integer.toHexString((((opcode & 0x0F00) >> 8) & 0xF)).toUpperCase();
        this.y = Integer.toHexString((((opcode & 0x00F0) >> 4) & 0xF)).toUpperCase();
        this.nnn = Integer.toHexString(((opcode & 0x0FFF) & 0xFFF)).toUpperCase();
        this.nn = Integer.toHexString(((opcode & 0x00FF) & 0xFF)).toUpperCase();
        this.n = Integer.toHexString(((opcode & 0x000F) & 0xF)).toUpperCase();
        this.iBefore = Integer.toHexString((i & 0xFFF)).toUpperCase();
        this.pc = Integer.toHexString((pc & 0xFFFF)).toUpperCase();
        this.i = Integer.toHexString((pc & 0xFFFF)).toUpperCase();
    }

    public String detailReturnFrom(int stackSizeBefore, int stackSize) {
        return "Returns from a subroutine. Does this by" +
                "\npopping the stack. Stack size before pop: " +
                stackSizeBefore + "\nSize of the stack after the pop: " +
                stackSize + "\nPopped value was: 0x" + this.pc +
                "\nThis value was assigned to program counter.";
    }

    public String detailJumpAddress(short pcBefore) {
        return "Jumps to address: 0x" + this.nnn +
                "\nThis value was assigned to program counter." +
                "\nProgram counter was before execution: 0x" +
                Integer.toHexString((pcBefore & 0xFFFF)).toUpperCase();
    }

    public String detailCallSub(int stackSizeBefore, int stackSize) {
        return "Calls a subroutine. Does this by pushing" +
                "\ncurrent program counter to stack." +
                "\nStack size before push: " + stackSizeBefore +
                "\nStack size after the push: " +
                stackSize + "\nPushed program counter was: 0x" + this.pc +
                "\nAfter push, jumps to address: 0x" + this.nnn;
    }

    public String detailSkipIfEqual() {
        return "Skips next instruction if V[" + this.x +
                "]\nis equal to 0x" + this.nn +
                "\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSkipIfNotEqual() {
        return "Skips next instruction if V[" + this.x +
                "]\nis NOT equal to 0x" + this.nn +
                "\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSkipIfEqualReg() {
        return "Skips next instruction if V[" + this.x +
                "]\nis equal to V[" + this.y +
                "]\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSetVarReg() {
        return "Sets V[" + this.x + "] to 0x" + this.nn;
    }

    public String detailAddVarReg(byte x) {
        return "Adds 0x" + this.nn + " to V[" + this.x +
                "].\nRegister value before add: 0x" +
                Integer.toHexString(((x) & 0xFF)).toUpperCase();
    }

    public String detailSetVxToVy() {
        return "Sets V[" + this.x + "] to V[" + this.y + "]";
    }

    public String detailBinary(byte xValue, byte yValue, String s, String s2) {
        return s + this.x + "] and V[" +
                this.y + "]\nand stores this value to V[" + this.x +
                "]\n0x" + Integer.toHexString((xValue & 0xFF)).toUpperCase() +
                s2 + Integer.toHexString((yValue & 0xFF)).toUpperCase() +
                "\nResult stored in V[" + this.x + "]";
    }

    public String detailAddVxVy(byte x, byte y) {
        return "Adds V[" + this.y + "] to V[" + this.x + "]." +
                "\nIf overflow then V[F] is set to 1.\nOverflow: " + state +
                "\nRegister value before add: 0x" + Integer.toHexString((x & 0xFF)).toUpperCase() +
                "\nValue to be added: 0x" + Integer.toHexString((y & 0xFF)).toUpperCase();
    }

    public String detailSubtract5(byte x, byte y, String xValue, String yValue) {
        return "Subtract V[" + this.x + "] = V[" + this.x + "] - V[" + this.y + "]" + "\n0x" +
                yValue + " - 0x" + xValue + " = 0x" + Integer.toHexString(((y - x) & 0xFF)).toUpperCase() +
                "\nIf V[" + this.x + "] (0x" + yValue + ") > V[" + this.y + "] (0x" + xValue + ")" +
                "\nThen set VF to 1, VF set to 1: " + state;
    }

    public String detailSubtract7(byte x, byte y, String xValue, String yValue) {
        return "Subtract V[" + this.y + "] = V[" + this.y + "] - V[" + this.x + "]" + "\n0x" +
                yValue + " - 0x" + xValue + " = 0x" + Integer.toHexString(((y - x) & 0xFF)).toUpperCase() +
                "\nIf V[" + this.y + "] (0x" + yValue + ") > V[" + this.x + "] (0x" + xValue + ")" +
                "\nThen set VF to 1, VF set to 1: " + state;
    }

    public String detailShiftRight(byte x) {
        return "Shifts right the value in V[" + this.x + "] by 1 bit." +
                "\nIf the shifted value was 1, sets VF to 1" + "\nVF 1: " + state + "." +
                "\nAfter this divides value in V[" + this.x + "] by 2." +
                "\nValue in V[" + this.x + "] before divide: 0x" + Integer.toHexString((x & 0xFF)).toUpperCase();
    }

    public String detailShiftLeft(byte x) {
        return "Shifts left the value in V[" + this.x + "] by 1 bit." +
                "\nIf the shifted value was 1, sets VF to 1" + "\nVF 1: " + state + "." +
                "\nAfter this multiplies value in V[" + this.x + "] by 2." +
                "\nValue in V[" + this.x + "] before multiply: 0x" + Integer.toHexString((x & 0xFF)).toUpperCase();
    }

    public String detailSkipIfNotEqReg() {
        return "Skips next instruction if V[" + this.x +
                "]\nis NOT equal to V[" + this.y +
                "]\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSetIndex() {
        return "Sets index register to 0x" + this.nnn +
                "\nIndex register was before operation: 0x" + this.iBefore;
    }

    public String detailJumpWithOff() {
        return "Jumps to 0x" + this.nnn + " + V[" + this.x + "]." +
                "\nIndex register was before operation: 0x" + this.iBefore;
    }

    public String detailRandom() {
        return "Sets V[" + this.x + "] to random byte that is" + "\nbinary AND with 0x" + this.nn;
    }

    public String detailDrawDisplay() {
        return "Draws sprite starting at following\ncoordinates: x: V[" + this.x + "] y: V[" + this.y + "]" +
                "\nSprite size is 8 pixels in width" +
                "\nand up to 16 pixels in height" +
                "\nSprite height: 0x" + this.n +
                "\nSprite viewer shows the sprite";
    }

    public String detailSkipIfKeyEq() {
        return "Skips next instruction if key in V[" + this.x +
                "]\nis pressed.\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSkipIfKeyNotEq() {
        return "Skips next instruction if key in V[" + this.x +
                "]\nis NOT pressed.\nSkipping is done by incrementing pc by 0x2" +
                "\nSkip happened: " + state;
    }

    public String detailSetVxToDetail() {
        return "Sets value in delay timer to V[" + this.x + "]";
    }

    public String detailGetKey() {
        return "Loops in this instruction\nuntil any key is pressed." +
                "\nAfter key press sets pressed key" +
                "\nto register V[" + this.x + "]";
    }

    public String detailSetDelayToVx() {
        return "Sets value in V[" + this.x + "] to delay timer.";
    }

    public String detailSetSoundToVx() {
        return "Sets value in sound timer to V[" + this.x + "]";
    }


    public String detailAddToIndex() {
        return "Adds value in V[" + this.x + "] to index register." +
                "\nIndex register value before operation: 0x" + iBefore;
    }

    public String detailFont() {
        return "Sets index register to font data location" +
                "\npointed by character in V[" + this.x + "]." +
                "Font data\nlocation starts at 0x50, which contains 0." +
                "\nEach font data is 5 bytes, so 1 would be\nat 0x55" +
                "and 2 at 0x60 etc.";
    }

    public String detailBcd(int decimal) {
        return "Converts value in V[" + this.x + "] to BCD." +
                "\nValue in decimal form: " + decimal + "\n this is now" +
                "inserted into ram pointed\nby index register in BCD format.";
    }

    public String detailRegisterDump() {
        return "Dumps registers from V[0] to V[" + this.x +
                "].\nThese are dumped to ram pointed by\nindex register" +
                " at locations starting\nat i, i+1, i+2 etc..";
    }

    public String detailRegisterFill() {
        return "Fills registers from V[0] to V[" + this.x +
                "].\nThese are filled from ram pointed by\nindex register" +
                " at locations starting\nat i, i+1, i+2 etc..";
    }

}
