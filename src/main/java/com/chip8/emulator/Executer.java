package com.chip8.emulator;


import com.chip8.ui.PixelManager;

public class Executer {

    private Memory memory;
    private Fetcher fetcher;
    private Decoder decoder;

    public Executer(String rom, PixelManager pixels) {
        this.memory = new Memory();

        Loader loader = new Loader(rom, memory);

        loader.readFile();
        loader.loadToMemory();

        loader.hexDump();

        this.fetcher = new Fetcher(memory);
        this.decoder = new Decoder(memory, fetcher, pixels);
    }

    public void execute() {
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
    }

    // used to testing
    public void forceOpcode(short opcode) {
        decoder.decode(opcode);
    }
}
