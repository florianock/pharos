package io.zocalo;

import org.junit.jupiter.api.Assertions;

public class TestUtils {

    private TestUtils(){
        /* This utility class should not be instantiated */
    }

    public static void assertEqualEnough(double a, double b) {
        Assertions.assertTrue(Utils.equalEnough(a, b));
    }
}
