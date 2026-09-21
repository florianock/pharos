package io.zocalo.scene;

import io.zocalo.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColorTest {

    @Test
    void colorTest() {
        var c = Color.create(-0.5f, 0.4f, 1.7f);

        assertEqualEnough(-0.5f, c.red());
        assertEqualEnough(0.4f, c.green());
        assertEqualEnough(1.7f, c.blue());
    }
    @Test
    void add() {
        var c1 = Color.create(0.9f, 0.6f, 0.75f);
        var c2 = Color.create(0.7f, 0.1f, 0.25f);

        var result = c1.add(c2);

        assertEqualEnough(1.6f, result.red());
        assertEqualEnough(0.7f, result.green());
        assertEqualEnough(1.0f, result.blue());
    }

    @Test
    void subtract() {
        var c1 = Color.create(0.9f, 0.6f, 0.75f);
        var c2 = Color.create(0.7f, 0.1f, 0.25f);

        var result = c1.subtract(c2);

        assertEqualEnough(0.2f, result.red());
        assertEqualEnough(0.5f, result.green());
        assertEqualEnough(0.5f, result.blue());
    }

    @Test
    void multiplyScalar() {
        var c = Color.create(0.2f, 0.3f, 0.4f);

        var result = c.multiply(2);

        assertEqualEnough(0.4f, result.red());
        assertEqualEnough(0.6f, result.green());
        assertEqualEnough(0.8f, result.blue());
    }

    @Test
    void hadamardProduct() {
        var c1 = Color.create(1, 0.2f, 0.4f);
        var c2 = Color.create(0.9f, 1, 0.1f);

        var result = c1.multiply(c2);

        assertEqualEnough(0.9f, result.red());
        assertEqualEnough(0.2f, result.green());
        assertEqualEnough(0.04f, result.blue());
    }

    private static void assertEqualEnough(float a, float b) {
        Assertions.assertTrue(Utils.equalEnough(a, b));
    }
}