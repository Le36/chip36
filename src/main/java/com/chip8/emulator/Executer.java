package com.chip8.emulator;


import com.chip8.ui.PixelManager;
import lombok.Data;

@Data
public class Executer {

    private Memory memory;
    private Fetcher fetcher;
    private Decoder decoder;
    private Loader loader;

    public Executer(String rom, PixelManager pixels, Keys keys) {
        this.memory = new Memory();

        loader = new Loader(rom, memory);

        loader.readFile();
        loader.loadToMemory();

        this.fetcher = new Fetcher(memory);
        this.decoder = new Decoder(memory, fetcher, pixels, keys);
    }

    public void execute() {
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode(), false);
    }

    // used to testing
    public void forceOpcode(short opcode) {
        decoder.decode(opcode, false);
    }
}
