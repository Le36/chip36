package com.chip8.emulator;

import lombok.Data;

@Data
public class Executer {

    private Memory memory;
    private Fetcher fetcher;
    private Decoder decoder;
    private Loader loader;

    public Executer(String rom, PixelManager pixels, Keys keys) {
        this.memory = new Memory();

        this.loader = new Loader(rom, memory);

        this.loader.readFile();
        this.loader.loadToMemory();
        this.loader.loadFontToRAM();

        this.fetcher = new Fetcher(memory);
        this.decoder = new Decoder(memory, fetcher, pixels, keys);
    }

    public void execute() {
        fetcher.fetch();
        decoder.decode(fetcher.getOpcode());
    }

    public void forceOpcode(short opcode) {
        decoder.decode(opcode);
    }
}
