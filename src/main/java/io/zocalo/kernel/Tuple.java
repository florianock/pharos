package io.zocalo.kernel;

import static io.zocalo.Utils.equalEnough;

public record Tuple(double x, double y, double z, double w) {

    public Tuple(double[] values) {
        if (values.length != 4) throw new IllegalArgumentException();
        this(values[0], values[1], values[2], values[3]);
    }

    public boolean isPoint() {
        return equalEnough(w, 1.0);
    }

    public boolean isVector() {
        return equalEnough(w, 0.0);
    }

    public Tuple add(Tuple t) {
        return new Tuple(x + t.x(), y + t.y(), z + t.z(), w + t.w());
    }

    public Tuple subtract(Tuple t) {
        return new Tuple(x - t.x(), y - t.y(), z - t.z(), w - t.w());
    }

    public Tuple multiply(double scalar) {
        return new Tuple(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Tuple divide(double scalar) {
        return multiply(1d / scalar);
    }

    public Tuple negate() {
        return new Tuple(-x, -y, -z, -w);
    }

    public double magnitude() {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Tuple normalize() {
        var magnitude = magnitude();
        if (magnitude <= 0) return this;
        return new Tuple(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
    }

    public double dot(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return x * t.x() + y * t.y() + z * t.z() + w * t.w();
    }

    public Tuple cross(Tuple t) {
        if (this.isPoint()) throw new IllegalStateException(this.toString());
        if (t.isPoint()) throw new IllegalArgumentException(t.toString());

        return new Tuple(
                y * t.z() - z * t.y(),
                z * t.x() - x * t.z(),
                x * t.y() - y * t.x(),
                0
        );
    }

    public Tuple reflect(final Tuple n) {
        return subtract(n.multiply(2 * dot(n)));
    }

    public static Tuple point(double x, double y, double z) {
        return new Tuple(x, y, z, 1);
    }

    public static Tuple vector(double x, double y, double z) {
        return new Tuple(x, y, z, 0);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Tuple(double x1, double y1, double z1, double w1))) return false;
        return (equalEnough(x1, this.x()) && equalEnough(y1, this.y()) && equalEnough(z1, this.z()) && equalEnough(w1, this.w()));
    }

    public Matrix4x4 viewTransform(Tuple to, Tuple up) {
        var forward = to.subtract(this).normalize();
        var upn = up.normalize();
        var left = forward.cross(upn);
        var trueUp = left.cross(forward);
        var orientation = new Matrix4x4(new double[]{
                    left.x(),     left.y(),     left.z(), 0,
                  trueUp.x(),   trueUp.y(),   trueUp.z(), 0,
                -forward.x(), -forward.y(), -forward.z(), 0,
                           0,            0,            0, 1
        });
        return orientation.multiply(Matrix4x4.translation(-x, -y, -z));
    }
}
