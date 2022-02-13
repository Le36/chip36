package com.chip8.ui;

/**
 * FPS counter for javafx
 */
public class FpsCounter {

    private final long[] frameTimes = new long[5];
    private int frameTimeIndex = 0;

    /**
     * @param now current NanoTime
     * @return returns the current FPS
     */
    public double update(long now) {
        long oldFrameTime = frameTimes[frameTimeIndex];
        frameTimes[frameTimeIndex] = now;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;

        long elapsedNanos = now - oldFrameTime;
        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
        return (1_000_000_000.0 / elapsedNanosPerFrame);
    }
}
