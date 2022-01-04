package com.chip8.emulator;

import lombok.Data;
import org.apache.commons.io.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * handles file loading for emulator and inserts font data into ram
 */
@Data
public class Loader {

    private byte[] bytes;
    private String name;
    private Memory memory;
    private File loadedRom;

    /**
     * @param name   file path
     * @param memory memory for emulator
     */
    public Loader(String name, Memory memory) {
        this.name = name;
        this.memory = memory;
    }

    /**
     * reads file and gets byte array of the file
     */
    public void readFile() {
        this.loadedRom = new File(this.name);
        try {
            bytes = Files.readAllBytes(loadedRom.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads file to ram using byte array that readFile() created
     */
    public void loadToMemory() {
        short address = 0x200; // Chip8 RAM starts at 0x200 / 0d512 for programs, 0x0 - 0x1FF reserved for fonts etc.
        for (byte b : bytes) {
            memory.initializeMemory(address, b);
            address++;
        }
    }

    /**
     * hex dump for ui
     *
     * @return outputStream of the loaded file as hex dump
     */
    public String hexDump() {
        OutputStream os = new ByteArrayOutputStream();
        try {
            HexDump.dump(bytes, 0, os, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toString();
    }

    /**
     * loads font data to rom, this is always same for every rom
     */
    public void loadFontToRAM() {
        int[] fontData = this.fontData();
        int pointer = 0;
        // 16 different characters, each character 5 bytes
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 5; j++, pointer++) {
                memory.initializeMemory((short) pointer, (byte) fontData[pointer]);
            }
        }
        fontData = this.largeFontData();
        short address = 0x60;
        pointer = 0;
        // 16 different characters, each character 10 bytes
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 10; j++, address++, pointer++) {
                memory.initializeMemory(address, (byte) fontData[pointer]);
            }
        }
    }

    /**
     * @return font data
     */
    private int[] fontData() {
        return new int[]{
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        };
    }

    private int[] largeFontData() {
        return new int[]{
            0b01111100, 0b11000110, 0b11001110, 0b11011110, 0b11010110, 0b11110110, 0b11100110, 0b11000110, 0b01111100, 0b00000000, // 0
            0b00010000, 0b00110000, 0b11110000, 0b00110000, 0b00110000, 0b00110000, 0b00110000, 0b00110000, 0b11111100, 0b00000000, // 1
            0b01111000, 0b11001100, 0b11001100, 0b00001100, 0b00011000, 0b00110000, 0b01100000, 0b11001100, 0b11111100, 0b00000000, // 2
            0b01111000, 0b11001100, 0b00001100, 0b00001100, 0b00111000, 0b00001100, 0b00001100, 0b11001100, 0b01111000, 0b00000000, // 3
            0b00001100, 0b00011100, 0b00111100, 0b01101100, 0b11001100, 0b11111110, 0b00001100, 0b00001100, 0b00011110, 0b00000000, // 4
            0b11111100, 0b11000000, 0b11000000, 0b11000000, 0b11111000, 0b00001100, 0b00001100, 0b11001100, 0b01111000, 0b00000000, // 5
            0b00111000, 0b01100000, 0b11000000, 0b11000000, 0b11111000, 0b11001100, 0b11001100, 0b11001100, 0b01111000, 0b00000000, // 6
            0b11111110, 0b11000110, 0b11000110, 0b00000110, 0b00001100, 0b00011000, 0b00110000, 0b00110000, 0b00110000, 0b00000000, // 7
            0b01111000, 0b11001100, 0b11001100, 0b11101100, 0b01111000, 0b11011100, 0b11001100, 0b11001100, 0b01111000, 0b00000000, // 8
            0b01111100, 0b11000110, 0b11000110, 0b11000110, 0b01111100, 0b00011000, 0b00011000, 0b00110000, 0b01110000, 0b00000000, // 9
            0b00110000, 0b01111000, 0b11001100, 0b11001100, 0b11001100, 0b11111100, 0b11001100, 0b11001100, 0b11001100, 0b00000000, // A
            0b11111100, 0b01100110, 0b01100110, 0b01100110, 0b01111100, 0b01100110, 0b01100110, 0b01100110, 0b11111100, 0b00000000, // B
            0b00111100, 0b01100110, 0b11000110, 0b11000000, 0b11000000, 0b11000000, 0b11000110, 0b01100110, 0b00111100, 0b00000000, // C
            0b11111000, 0b01101100, 0b01100110, 0b01100110, 0b01100110, 0b01100110, 0b01100110, 0b01101100, 0b11111000, 0b00000000, // D
            0b11111110, 0b01100010, 0b01100000, 0b01100100, 0b01111100, 0b01100100, 0b01100000, 0b01100010, 0b11111110, 0b00000000, // E
            0b11111110, 0b01100110, 0b01100010, 0b01100100, 0b01111100, 0b01100100, 0b01100000, 0b01100000, 0b11110000, 0b00000000  // F
        };
    }
}
