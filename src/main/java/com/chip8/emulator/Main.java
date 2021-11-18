package com.chip8.emulator;

import lombok.SneakyThrows;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Memory memory = new Memory();
        // IBM test file, draws only an IBM-logo on screen
        Loader loader = new Loader("IBM", memory);

        loader.readFile();
        loader.loadToMemory();

        loader.hexDump();

        Fetcher fetcher = new Fetcher(memory);
        Decoder decoder = new Decoder(memory, fetcher);

        // infinite loop for rom file
        while (true) {
            fetcher.fetch();
            decoder.decode(fetcher.getOpcode());
            Thread.sleep(100);
        }
    }

}
