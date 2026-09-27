package io.zocalo.kernel;

import io.zocalo.Utils;

import java.util.Arrays;
import java.util.StringJoiner;

public class Matrix4x4 {

    private final float[] m;

    public Matrix4x4() {
        this(new float[16]);
    }

    public Matrix4x4(float[] data) {
        if (data.length != 16) throw new IllegalArgumentException(Arrays.toString(data));

        this.m = data.clone();
    }

    public Matrix4x4(float[][] m) {
        if (m.length != 4 || m[0].length != 4 || m[1].length != 4 || m[2].length != 4 || m[3].length != 4) {
            throw new IllegalArgumentException(Arrays.toString(m));
        }

        var data = new float[16];
        for (var r = 0; r < 4; r++) {
            System.arraycopy(m[r], 0, data, r * 4, 4);
        }

        this(data);
    }

    public float get(int r, int c) {
        if (r < 0 || 3 < r) throw new IndexOutOfBoundsException(r);
        if (c < 0 || 3 < c) throw new IndexOutOfBoundsException(c);

        return m[r * 4 + c];
    }

    public Matrix4x4 set(int r, int c, float value) {
        if (r < 0 || 3 < r) throw new IndexOutOfBoundsException(r);
        if (c < 0 || 3 < c) throw new IndexOutOfBoundsException(c);

        m[r * 4 + c] = value;
        return this;
    }

    public Matrix4x4 multiply(Matrix4x4 b) {
        var result = new float[4][4];
        for (var r = 0; r < 4; r++) {
            for (var c = 0; c < 4; c++) {
                result[r][c] =
                        get(r, 0) * b.get(0, c) +
                                get(r, 1) * b.get(1, c) +
                                get(r, 2) * b.get(2, c) +
                                get(r, 3) * b.get(3, c);
            }
        }
        return new Matrix4x4(result);
    }

    public Tuple multiply(Tuple b) {
        return new Tuple(
                get(0, 0) * b.x() + get(0, 1) * b.y() + get(0, 2) * b.z() + get(0, 3) * b.w(),
                get(1, 0) * b.x() + get(1, 1) * b.y() + get(1, 2) * b.z() + get(1, 3) * b.w(),
                get(2, 0) * b.x() + get(2, 1) * b.y() + get(2, 2) * b.z() + get(2, 3) * b.w(),
                get(3, 0) * b.x() + get(3, 1) * b.y() + get(3, 2) * b.z() + get(3, 3) * b.w()
        );
    }

    public float[] multiply(float[] b) {
        return new float[] {
                get(0, 0) * b[0] + get(0, 1) * b[1] + get(0, 2) * b[2] + get(0, 3) * b[3],
                get(1, 0) * b[0] + get(1, 1) * b[1] + get(1, 2) * b[2] + get(1, 3) * b[3],
                get(2, 0) * b[0] + get(2, 1) * b[1] + get(2, 2) * b[2] + get(2, 3) * b[3],
                get(3, 0) * b[0] + get(3, 1) * b[1] + get(3, 2) * b[2] + get(3, 3) * b[3]
        };
    }

    public Matrix4x4 transpose() {
        return new Matrix4x4(new float[] {m[0], m[4], m[8], m[12], m[1], m[5], m[9], m[13], m[2], m[6], m[10], m[14], m[3], m[7], m[11], m[15]});
    }

    public Matrix3x3 subMatrix(int r, int c) {
        var counter = 0;
        var result = new float[9];
        for (var i = 0; i < 4; i++) {
            for (var j = 0; j < 4; j++) {
                if (i == r || j == c) continue;
                result[counter++] = get(i, j);
            }
        }
        return new Matrix3x3(result);
    }

    public float minor(int r, int c) {
        return subMatrix(r, c).determinant();
    }

    public float cofactor(int r, int c) {
        return (r + c) % 2 == 0 ? minor(r, c) : -minor(r, c);
    }

    public float determinant() {
        var result = 0f;
        for (var c = 0; c < 4; c++) {
            result += get(0, c) * cofactor(0, c);
        }
        return result;
    }

    public boolean isInvertible() {
        return determinant() != 0;
    }

    public Matrix4x4 invert() {
        var determinant = determinant();
        var result = new float[4][4];
        for (var r = 0; r < 4; r++) {
            for (var c = 0; c < 4; c++) {
                result[c][r] = cofactor(r, c) / determinant;
            }
        }
        return new Matrix4x4(result);
    }

    public Matrix4x4 translate(float x, float y, float z) {
        return translation(x, y, z).multiply(this);
    }

    public Matrix4x4 scale(float x, float y, float z) {
        return scaling(x, y, z).multiply(this);
    }

    public Matrix4x4 rotateOnX(float r) {
        return rotationOnX(r).multiply(this);
    }

    public Matrix4x4 rotateOnY(float r) {
        return rotationOnY(r).multiply(this);
    }

    public Matrix4x4 rotateOnZ(float r) {
        return rotationOnZ(r).multiply(this);
    }

    public Matrix4x4 shear(float xInProportionToY, float xInProportionToZ, float yInProportionToX,
                           float yInProportionToZ, float zInProportionToX, float zInProportionToY) {
        return shearing(xInProportionToY, xInProportionToZ, yInProportionToX, yInProportionToZ, zInProportionToX, zInProportionToY).multiply(this);
    }

    // transformations

    public static Matrix4x4 identity() {
        return new Matrix4x4(new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 translation(float x, float y, float z) {
        return new Matrix4x4(new float[] {
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 scaling(float x, float y, float z) {
        return new Matrix4x4(new float[] {
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnX(float r) {
        return new Matrix4x4(new float[] {
                1, 0, 0, 0,
                0, (float) Math.cos(r), (float) -Math.sin(r), 0,
                0, (float) Math.sin(r), (float) Math.cos(r), 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnY(float r) {
        return new Matrix4x4(new float[] {
                (float) Math.cos(r), 0, (float) Math.sin(r), 0,
                0, 1, 0, 0,
                (float) -Math.sin(r), 0, (float) Math.cos(r), 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnZ(float r) {
        return new Matrix4x4(new float[] {
                (float) Math.cos(r), (float) -Math.sin(r), 0, 0,
                (float) Math.sin(r), (float) Math.cos(r), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 shearing(float xInProportionToY, float xInProportionToZ, float yInProportionToX,
                                     float yInProportionToZ, float zInProportionToX, float zInProportionToY) {
        return new Matrix4x4(new float[] {
                1, xInProportionToY, xInProportionToZ, 0,
                yInProportionToX, 1, yInProportionToZ, 0,
                zInProportionToX, zInProportionToY, 1, 0,
                0, 0, 0, 1
        });
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof final Matrix4x4 m2))
            return false;
        for (var r = 0; r < 4; r++) {
            for (var c = 0; c < 4; c++) {
                if (!Utils.equalEnough(get(r, c), m2.get(r, c))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var result = new StringJoiner(" | ");
        for (var r = 0; r < 4; r++) {
            for (var c = 0; c < 4; c++) {
                result.add(String.valueOf(get(r, c)));
            }
        }
        return result.toString();
    }
}
