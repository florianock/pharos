package io.zocalo.kernel;

import io.zocalo.Utils;

import java.util.Arrays;
import java.util.StringJoiner;

public class Matrix2x2 {
    private final double[] m;

    public Matrix2x2() {
        this(new double[4]);
    }

    public Matrix2x2(double[] data) {
        assert data.length == 4;
        this.m = data.clone();
    }

    public Matrix2x2(double[][] m) {
        assert m.length == 2 && m[0].length == 2 && m[1].length == 2;
        this(new double[] { m[0][0], m[0][1], m[1][0], m[1][1] });
    }

    public double get(int r, int c) {
        assert 0 <= r && r <= 1 && 0 <= c && c <= 1;
        return m[r * 2 + c];
    }

    public double determinant() {
        return m[0] * m[3] - m[1] * m[2];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof final Matrix2x2 m2)) return false;
        for (var r = 0; r < 2; r++) {
            for (var c = 0; c < 2; c++) {
                if (!Utils.equalEnough(get(r, c), m2.get(r, c))) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var result = new StringJoiner(" | ");
        for (var r = 0; r < 2; r++) {
            for (var c = 0; c < 2; c++) {
                result.add(String.valueOf(get(r, c)));
            }
        }
        return result.toString();
    }
}
