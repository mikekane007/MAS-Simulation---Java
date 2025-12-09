package mas.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random RANDOM = new Random();

    /**
     * Generates a random integer between min and max (inclusive).
     */
    public static int getInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Max must be greater than or equal to min");
        }
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns true with the given probability (0.0 to 1.0).
     */
    public static boolean chance(double probability) {
        return RANDOM.nextDouble() < probability;
    }

    /**
     * Returns a random element from an array.
     */
    public static <T> T getItem(T[] array) {
        if (array == null || array.length == 0) return null;
        return array[RANDOM.nextInt(array.length)];
    }
}
