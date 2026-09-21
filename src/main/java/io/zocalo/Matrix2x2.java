package io.zocalo;

import java.util.Arrays;
import java.util.StringJoiner;

public class Matrix2x2 {
    private final float[] m;

    public Matrix2x2() {
        this(new float[4]);
    }

    public Matrix2x2(float[] data) {
        if (data.length != 4) throw new IllegalArgumentException(Arrays.toString(data));

        this.m = data.clone();
    }

    public Matrix2x2(float[][] m) {
        if (m.length != 2 || m[0].length != 2 || m[1].length != 2) {
            throw new IllegalArgumentException(Arrays.toString(m));
        }

        this(new float[] { m[0][0], m[0][1], m[1][0], m[1][1] });
    }

    public float get(int r, int c) {
        if (r < 0 || 1 < r) throw new IndexOutOfBoundsException(r);
        if (c < 0 || 1 < c) throw new IndexOutOfBoundsException(c);

        return m[r * 2 + c];
    }

    public float determinant() {
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
