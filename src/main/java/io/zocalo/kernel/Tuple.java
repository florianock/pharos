package io.zocalo.kernel;

import java.util.Arrays;

import static io.zocalo.Utils.equalEnough;

public class Tuple {

    private final double[] values;

    public Tuple(double x, double y, double z, double w) {
        values = new double[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = w;
    }

    public Tuple(double[] values) {
        if (values.length != 4) throw new IllegalArgumentException();
        this.values = values.clone();
    }

    public double x() {
        return values[0];
    }

    public double y() {
        return values[1];
    }

    public double z() {
        return values[2];
    }

    public double w() {
        return values[3];
    }

    public double[] values() { return values; }

    public boolean isPoint() {
        return isPoint(values);
    }

    public static boolean isPoint(double[] t) { return t.length == 4 && equalEnough(t[3], 1.0);}

    public boolean isVector() {
        return isVector(values);
    }

    public static boolean isVector(double[] t) { return t.length == 4 && equalEnough(t[3], 0.0);}

    public Tuple add(Tuple t) {
        return new Tuple(add(values, t.values()));
    }

    public static double[] add(double[] a, double[] b) {
        return new double[] {
            a[0] + b[0],
            a[1] + b[1],
            a[2] + b[2],
            a[3] + b[3]
        };
    }

    public Tuple subtract(Tuple t) {
        return new Tuple(subtract(values, t.values()));
    }

    public static double[] subtract(double[] a, double[] b) {
        return new double[] {
            a[0] - b[0],
            a[1] - b[1],
            a[2] - b[2],
            a[3] - b[3],
        };
    }

    public Tuple multiply(double scalar) {
        return new Tuple(multiply(values, scalar));
    }

    public static double[] multiply(double[] a, double scalar) {
        return new double[] {
            a[0] * scalar,
            a[1] * scalar,
            a[2] * scalar,
            a[3] * scalar
        };
    }

    public Tuple divide(double scalar) {
        return new Tuple(divide(values, scalar));
    }

    public static double[] divide(double[] a, double scalar) {
        return multiply(a, 1f / scalar);
    }

    public Tuple negate() {
        return new Tuple(negate(values));
    }

    public static double[] negate(double[] a) {
        return new double[] {
            0 - a[0],
            0 - a[1],
            0 - a[2],
            0 - a[3]
        };
    }

    public double magnitude() {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        return magnitude(values);
    }

    public static double magnitude(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2] + a[3] * a[3]);
    }

    public Tuple normalize() {
        return new Tuple(normalize(values));
    }

    public static double[] normalize(double[] a) {
        var magnitude = magnitude(a);
        if (magnitude == 0) return a;
        return new double[] {
            a[0] / magnitude,
            a[1] / magnitude,
            a[2] / magnitude,
            a[3] / magnitude
        };
    }

    public double dot(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return dot(values, t.values());
    }

    public static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3];
    }

    public Tuple cross(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return new Tuple(cross(values, t.values()));
    }

    public static double[] cross(double[] a, double[] b) {
        return new double[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0],
                0
        };
    }

    public Tuple reflect(final Tuple n) {
        return new Tuple(reflect(values, n.values()));
    }

    public static double[] reflect(double[] a, double[] b) {
        return subtract(a, multiply(b, 2 * dot(a, b)));
    }

    public static Tuple point(double x, double y, double z) {
        return new Tuple(x, y, z, 1);
    }

    public static Tuple vector(double x, double y, double z) {
        return new Tuple(x, y, z, 0);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof final Tuple t1)) return false;
        return (equalEnough(t1.x(), this.x()) && equalEnough(t1.y(), this.y()) && equalEnough(t1.z(), this.z()) && equalEnough(t1.w(), this.w()));
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    public Matrix4x4 viewTransform(Tuple to, Tuple up) {
        return Tuple.viewTransform(values, to.values(), up.values());
    }

    public static Matrix4x4 viewTransform(double[] from, double[] to, double[] up) {
        var forward = Tuple.normalize(Tuple.subtract(to, from));
        var upn = Tuple.normalize(up);
        var left = Tuple.cross(forward, upn);
        var trueUp = Tuple.cross(left, forward);

        var orientation = new Matrix4x4(new double[] {
                left[0],     left[1],     left[2], 0,
              trueUp[0],   trueUp[1],   trueUp[2], 0,
            -forward[0], -forward[1], -forward[2], 0,
                      0,           0,           0, 1
        });

        return orientation.multiply(Matrix4x4.translation(-from[0], -from[1], -from[2]));
    }
}
