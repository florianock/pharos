package io.zocalo.kernel;

public record Ray(Tuple origin, Tuple direction) {

    public Ray {
        if (!origin.isPoint()) throw new IllegalStateException("Not a valid origin: " + origin);
        if (!direction.isVector()) throw new IllegalStateException("Not a valid direction: " + direction);
    }

    public Tuple position(double t) {
        return direction.multiply(t).add(origin);
    }

    public Ray transform(Matrix4x4 m) {
        return Ray.create(m.multiply(origin), m.multiply(direction));
    }

    public static Ray create(Tuple origin, Tuple direction) {
        return new Ray(origin, direction);
    }
}