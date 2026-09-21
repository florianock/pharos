package io.zocalo;

import java.util.Arrays;

public class Tuple {

    private final float[] values;

    public Tuple(float x, float y, float z, float w) {
        values = new float[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = w;
    }

    public Tuple(float[] values) {
        if (values.length != 4) throw new IllegalArgumentException();
        this.values = values.clone();
    }

    public float x() {
        return values[0];
    }

    public float y() {
        return values[1];
    }

    public float z() {
        return values[2];
    }

    public float w() {
        return values[3];
    }

    public float[] values() { return values; }

    public boolean isPoint() {
        return isPoint(values);
    }

    public static boolean isPoint(float[] t) { return t[3] == 1;}

    public boolean isVector() {
        return isVector(values);
    }

    public static boolean isVector(float[] t) { return t[3] == 0;}

    public Tuple add(Tuple t) {
        return new Tuple(add(values.clone(), t.values()));
    }

    public static float[] add(float[] a, float[] b) {
        a[0] += b[0];
        a[1] += b[1];
        a[2] += b[2];
        a[3] += b[3];
        return a;
    }

    public Tuple subtract(Tuple t) {
        return new Tuple(subtract(values.clone(), t.values()));
    }

    public static float[] subtract(float[] a, float[] b) {
        a[0] -= b[0];
        a[1] -= b[1];
        a[2] -= b[2];
        a[3] -= b[3];
        return a;
    }

    public Tuple multiply(float scalar) {
        return new Tuple(multiply(values.clone(), scalar));
    }

    public static float[] multiply(float[] a, float scalar) {
        a[0] *= scalar;
        a[1] *= scalar;
        a[2] *= scalar;
        a[3] *= scalar;
        return a;
    }

    public Tuple divide(float scalar) {
        return new Tuple(divide(values.clone(), scalar));
    }

    public static float[] divide(float[] a, float scalar) {
        return multiply(a, 1f / scalar);
    }

    public Tuple negate() {
        return new Tuple(negate(values.clone()));
    }

    public static float[] negate(float[] a) {
        a[0] = 0 - a[0];
        a[1] = 0 - a[1];
        a[2] = 0 - a[2];
        a[3] = 0 - a[3];
        return a;
    }

    public float magnitude() {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        return magnitude(values.clone());
    }

    public static float magnitude(float[] a) {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2] + a[3] * a[3]);
    }

    public Tuple normalize() {
        return new Tuple(normalize(values.clone()));
    }

    public static float[] normalize(float[] a) {
        var magnitude = magnitude(a);
        a[0] /= magnitude;
        a[1] /= magnitude;
        a[2] /= magnitude;
        a[3] /= magnitude;
        return a;
    }

    public float dot(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return dot(values, t.values());
    }

    public static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3];
    }

    public Tuple cross(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return new Tuple(cross(values, t.values()));
    }

    public static float[] cross(float[] a, float[] b) {
        return new float[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0],
                0
        };
    }

    public Tuple reflect(final Tuple n) {
        return new Tuple(reflect(values.clone(), n.values()));
    }

    public static float[] reflect(float[] a, float[] b) {
        return subtract(a, multiply(b, 2f * dot(a, b)));
    }

    public static Tuple point(float x, float y, float z) {
        return new Tuple(x, y, z, 1);
    }

    public static Tuple vector(float x, float y, float z) {
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
        return (Utils.equalEnough(t1.x(), this.x()) && Utils.equalEnough(t1.y(), this.y()) && Utils.equalEnough(t1.z(), this.z()) && Utils.equalEnough(t1.w(), this.w()));
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
