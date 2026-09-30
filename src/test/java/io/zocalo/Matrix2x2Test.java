package io.zocalo;

import io.zocalo.kernel.Matrix2x2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix2x2Test {

    @Test
    void get() {
        var m = new Matrix2x2(new double[][] {
                {-3, 5},
                {1, -2}
        });

        assertEquals(-3, m.get(0,0));
        assertEquals(5,  m.get(0,1));
        assertEquals(1,  m.get(1,0));
        assertEquals(-2, m.get(1,1));
    }

    @Test
    void determinant() {
        var a = new Matrix2x2(new double[] {1, 5, -3, 2});

        assertEquals(17, a.determinant());
    }

    @Test
    void equality() {
        var a = new Matrix2x2(new double[] { 1, 2, 5, 6});
        var b = new Matrix2x2(new double[] { 1.00001, 2.00001, 4.99999, 5.99999});

        assertEquals(a, b);
    }
}