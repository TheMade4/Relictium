package io.themade4.relictium.core.client.util;

import net.minecraft.util.math.BlockPos;

public class MathUtil {
    /**
     * @return True if the specified number is greater than zero and is a power of two, otherwise false
     */
    public static boolean isPowerOfTwo(int n) {
        return ((n & (n - 1)) == 0);
    }

    /**
     * @return Hash of the position, replaces a similar method from modern. It's almost certainly not exactly
     * equivalent, but I'll be very concerned if it matters
     */
    public static long hashPos(BlockPos pos) {
        return cantor(pos.getX(), cantor(pos.getY(), pos.getZ()));
    }

    /**
     * Maps every positive a and b to a unique int, barring overflow
     * <a href="https://stackoverflow.com/a/73089718">Source on Stack Overflow</a>
     */
    private static long cantor(long a, long b) {
        return (a + b + 1) * (a + b) / 2 + b;
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
}
