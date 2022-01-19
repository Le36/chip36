package com.chip8.ui;

import javax.sound.sampled.*;

public class Audio {

    public void tone(int pitch, byte[] buffer) throws LineUnavailableException {
        tone(freq(pitch), buffer);
    }

    private void tone(float hz, byte[] buffer) throws LineUnavailableException {
        AudioFormat af = new AudioFormat(hz, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i = 0; i < 16; i++) {
            if (buffer[i] == (byte) 255) {
                buffer[i] = (byte) 0x38;
            }
            sdl.write(buffer, 0, 16);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

    private float freq(int pitch) {
        return (float) (4000 * Math.pow(2, (pitch - 64f) / 48f));
    }
}
