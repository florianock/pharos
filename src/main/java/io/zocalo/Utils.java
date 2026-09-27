package io.zocalo;

import io.zocalo.scene.Color;

public class Utils {
    public static final float EPSILON       = 1e-4f;
    public static final float SHADOW_OFFSET = EPSILON * 16; // see https://forum.raytracerchallenge.com/thread/278/ch-8-get-acne
    public static final float HALF_PI       = 1.570796326794897f;
    public static final float PI            = 3.141592653589793f;

    private Utils() {
        /* This utility class should not be instantiated */
    }

    public static boolean equalEnough(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static int clamp255(float value) {
        if (value < 0) return 0;
        if (value > 1) return 255;

        return (int) (value * 255 + 0.5);
    }

    public static int pack255(Color color) {
        return (255 << 24) | (clamp255(color.red()) << 16) | (clamp255(color.green()) << 8) | clamp255(color.blue());
    }

    public static void unpack255(int i, int[] out, int offset) {
        out[offset]      = (i >> 24) & 0xFF;
        out[offset + 1]  = (i >> 16) & 0xFF;
        out[offset + 2]  = (i >> 8)  & 0xFF;
        out[offset + 3]  =  i        & 0xFF;
    }

    /**
     * Useful alternative to Math.pow() for float base and int exponent.
     */
    public static float pow(float base, int exponent) {
        if (exponent == 0) return 1.0f;
        if (exponent < 0) {
            base = 1.0f / base;
            exponent = -exponent;
        }

        float result = 1.0f;
        while (exponent > 0) {
            if ((exponent & 1) == 1) { // Exponent is odd
                result *= base;
            }
            base *= base;
            exponent >>= 1; // Divide exponent by 2
        }
        return result;
    }

}
