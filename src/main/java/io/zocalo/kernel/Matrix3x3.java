package io.zocalo.kernel;

import io.zocalo.Utils;

import java.util.Arrays;
import java.util.StringJoiner;

public class Matrix3x3 {
    private final double[] m;

    public Matrix3x3() {
        this(new double[9]);
    }

    public Matrix3x3(double[] data) {
        assert data.length == 9;
        this.m = data.clone();
    }

    public Matrix3x3(double[][] m) {
        assert m.length == 3 && m[0].length == 3 && m[1].length == 3 && m[2].length == 3;
        this(new double[] {
                m[0][0], m[0][1], m[0][2],
                m[1][0], m[1][1], m[1][2],
                m[2][0], m[2][1], m[2][2]
        });
    }

    public double get(int r, int c) {
        assert 0 <= r && r <= 2 && 0 <= c && c <= 2;
        return m[r * 3 + c];
    }

    public Matrix2x2 subMatrix(int r, int c) {
        var counter = 0;
        var result = new double[4];
        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 3; j++) {
                if (i == r || j == c) continue;
                result[counter++] = get(i, j);
            }
        }
        return new Matrix2x2(result);
    }

    public double minor(int r, int c) {
        return subMatrix(r, c).determinant(); // m[0] * m[3] - m[1] * m[2]
    }

    public double cofactor(int r, int c) {
        return (r + c) % 2 == 0 ? minor(r, c) : -minor(r, c);
    }

    public double determinant() {
        return get(0, 0) * cofactor(0, 0) +
                get(0, 1) * cofactor(0, 1) +
                get(0, 2) * cofactor(0, 2);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof final Matrix3x3 m2)) return false;
        for (var r = 0; r < 3; r++) {
            for (var c = 0; c < 3; c++) {
                if (!Utils.equalEnough(get(r, c), m2.get(r, c))) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var result = new StringJoiner(" | ");
        for (var r = 0; r < 3; r++) {
            for (var c = 0; c < 3; c++) {
                result.add(String.valueOf(get(r, c)));
            }
        }
        return result.toString();
    }
}
