package com.slipgaji.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SoundUtil {

    public static void beepSuccess() {
        new Thread(() -> {
            playTone(523, 150);
            pause(60);
            playTone(659, 220);
        }).start();
    }

    public static void beepError() {
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                playTone(400, 130);
                pause(100);
            }
        }).start();
    }

    private static void playTone(int hz, int ms) {
        try {
            float sampleRate = 44100;
            int sampleCount = (int) (sampleRate * ms / 1000.0);
            byte[] buf = new byte[sampleCount];
            for (int i = 0; i < sampleCount; i++) {
                double angle = 2.0 * Math.PI * i / (sampleRate / hz);
                buf[i] = (byte) (Math.sin(angle) * 70);
            }
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.close();
        } catch (Exception e) {
            // fallback silent if audio not available
        }
    }

    private static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
