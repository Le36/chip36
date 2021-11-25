package com.chip8.emulator;

import lombok.Data;
import org.apache.commons.io.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@Data
public class Loader {

    private byte[] bytes;
    private String name;
    private Memory memory;

    public Loader(String name, Memory memory) {
        this.name = name;
        this.memory = memory;
    }

    public void readFile() {
        File rom = new File(this.name);
        try {
            bytes = Files.readAllBytes(rom.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadToMemory() {
        short address = 0x200; // Chip8 RAM starts at 0x200 / 0d512 for programs, 0x0 - 0x1FF reserved for fonts etc.
        for (byte b : bytes) {
            memory.initializeMemory(address, b);
            address++;
        }
    }

    public String hexDump() {
        OutputStream os = new ByteArrayOutputStream();
        try {
            HexDump.dump(bytes, 0, os, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toString();
    }

    public void loadFontToRAM() {
        int[] fontData = this.fontData();
        short address = 0x50;
        int pointer = 0;
        // 16 different characters, each character 5 bytes
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 5; j++, address++, pointer++) {
                memory.initializeMemory(address, (byte) fontData[pointer]);
            }
        }
    }

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

}
