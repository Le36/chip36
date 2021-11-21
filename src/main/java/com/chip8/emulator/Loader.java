package com.chip8.emulator;

import org.apache.commons.io.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

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

    public void hexDump() {
        OutputStream os = new ByteArrayOutputStream();
        try {
            HexDump.dump(bytes, 0, os, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(os);
    }

}
