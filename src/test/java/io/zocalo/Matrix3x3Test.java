package io.zocalo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix3x3Test {

    @Test
    void get() {
        var m = new Matrix3x3(new float[][] {
                {-3, 5, 0},
                {1, -2, -7},
                {0, 1, 1}
        });

        assertEquals(-3, m.get(0,0));
        assertEquals(-2, m.get(1,1));
        assertEquals(1,  m.get(2,2));
    }

    @Test
    void subMatrix() {
        var a = new Matrix3x3(new float[][] {
                {1, 5, 0},
                {-3, 2, 7},
                {0, 6, -3}
        });

        var expected = new Matrix2x2(new float[][] {
                {-3, 2},
                {0, 6}
        });

        assertEquals(expected, a.subMatrix(0, 2));
    }

    @Test
    void equality() {
        var a = new Matrix3x3(new float[] { 1, 2, 5, 6, 7, 8, 9, 3, 4 });
        var b = new Matrix3x3(new float[] { 1.00001f, 2.00001f, 4.99999f, 5.99999f, 7, 8, 9, 3, 4 });

        assertEquals(a, b);
    }

    @Test
    void minor() {
        var a = new Matrix3x3(new float[][]{
                {3, 5, 0},
                {2, -1, -7},
                {6, -1, 5}
        });
        var b = a.subMatrix(1, 0);

        assertEquals(25, b.determinant());
        assertEquals(25, a.minor(1, 0));
    }

    @Test
    void cofactor() {
        var a = new Matrix3x3(new float[][]{
                {3, 5, 0},
                {2, -1, -7},
                {6, -1, 5}
        });

        assertEquals(-12, a.minor(0, 0));
        assertEquals(-12, a.cofactor(0, 0));
        assertEquals(25, a.minor(1, 0));
        assertEquals(-25, a.cofactor(1, 0));
    }

    @Test
    void determinant() {
        var a = new Matrix3x3(new float[][]{
                {1, 2, 6},
                {-5, 8, -4},
                {2, 6, 4}
        });

        assertEquals(56, a.cofactor(0, 0));
        assertEquals(12, a.cofactor(0, 1));
        assertEquals(-46, a.cofactor(0, 2));
        assertEquals(-196, a.determinant());
    }
}