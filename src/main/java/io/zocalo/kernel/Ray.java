package io.zocalo.kernel;

public record Ray(Tuple origin, Tuple direction) {

    public Ray {
        assert origin.isPoint() && direction.isVector();
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