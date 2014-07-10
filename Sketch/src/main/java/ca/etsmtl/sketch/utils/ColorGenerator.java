package ca.etsmtl.sketch.utils;

import android.graphics.Color;

import java.util.Random;

/*
 * TODO Refactor as a real random color picker
 */
public class ColorGenerator {
    public final static int[] COLORS_STANDARD = new int[] {
            Color.BLUE, Color.GRAY, Color.GREEN, Color.RED
    };

    private int currentIndex = 0;

    public static int generateDefaultStandardColor() {
        Random random = new Random(System.currentTimeMillis());
        return COLORS_STANDARD[random.nextInt(COLORS_STANDARD.length -1)];
    }

    public int next() {
        if (currentIndex >= COLORS_STANDARD.length) {
            currentIndex = 0;
        }

        return COLORS_STANDARD[currentIndex++];
    }
}
