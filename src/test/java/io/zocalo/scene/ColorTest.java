package io.zocalo.scene;

import org.junit.jupiter.api.Test;

import static io.zocalo.TestUtils.assertEqualEnough;

class ColorTest {

    @Test
    void colorTest() {
        var c = new Color(-0.5, 0.4, 1.7);

        assertEqualEnough(-0.5, c.red());
        assertEqualEnough(0.4, c.green());
        assertEqualEnough(1.7, c.blue());
    }
    @Test
    void add() {
        var c1 = new Color(0.9, 0.6, 0.75);
        var c2 = new Color(0.7, 0.1, 0.25);

        var result = c1.add(c2);

        assertEqualEnough(1.6, result.red());
        assertEqualEnough(0.7, result.green());
        assertEqualEnough(1.0, result.blue());
    }

    @Test
    void subtract() {
        var c1 = new Color(0.9, 0.6, 0.75);
        var c2 = new Color(0.7, 0.1, 0.25);

        var result = c1.subtract(c2);

        assertEqualEnough(0.2, result.red());
        assertEqualEnough(0.5, result.green());
        assertEqualEnough(0.5, result.blue());
    }

    @Test
    void multiplyScalar() {
        var c = new Color(0.2, 0.3, 0.4);

        var result = c.multiply(2);

        assertEqualEnough(0.4, result.red());
        assertEqualEnough(0.6, result.green());
        assertEqualEnough(0.8, result.blue());
    }

    @Test
    void hadamardProduct() {
        var c1 = new Color(1, 0.2, 0.4);
        var c2 = new Color(0.9, 1, 0.1);

        var result = c1.multiply(c2);

        assertEqualEnough(0.9, result.red());
        assertEqualEnough(0.2, result.green());
        assertEqualEnough(0.04, result.blue());
    }
}