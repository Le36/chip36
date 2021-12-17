package com.chip8.emulator;

import static org.junit.Assert.*;

import com.chip8.configs.Configs;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExecuterTest {

    private Executer executer;

    @Before
    public void setUp() throws IOException {
        // create empty file that executer will use as a rom
        File executerTestFile = new File("executerTest.txt");
        if (!executerTestFile.exists()) {
            executerTestFile.createNewFile();
        }
        executerTestFile.deleteOnExit();
        String testFileContents = "this is some random data for the test file to be ignored";
        FileWriter fw = new FileWriter("executerTest.txt");
        fw.write(testFileContents);
        fw.close();

        this.executer = new Executer("executerTest.txt", new PixelManager(1, 1), new Keys(), new Configs());
        // since empty mock rom, lets initialize some ram for tests
        byte[] ram = this.executer.getMemory().getRam();
        ram[0x200] = (byte) 0x00;
        ram[0x201] = (byte) 0xEE;
        this.executer.getMemory().setRam(ram);
    }

    @Test
    public void cycle() {
        // do one fetch-decode-execute cycle
        // we only have one instruction in ram
        // that is 00EE, so lets try it
        executer.execute();
        // pc should be incremented
        assertEquals(this.executer.getMemory().getPc(), 0x202);
        // since we executed 00EE with empty stack, we should have
        // error string in details
        assertEquals("Error:\n00EE instruction, but stack is empty!", this.executer.getDecoder().getDetailed());
    }

    @Test
    public void forceOpcode() {
        // try forcing opcodes
        // force set pc to 0xDDD
        executer.forceOpcode(0x1DDD);
        assertEquals(0xDDD, executer.getMemory().getPc());
    }
}
