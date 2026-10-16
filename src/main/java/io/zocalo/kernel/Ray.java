package io.zocalo.kernel;

public record Ray(Tuple origin, Tuple direction) {

    public Ray {
        assert origin.isPoint() && direction.isVector();
    }

    public Tuple position(double t) {
        return direction.multiply(t).add(origin);
    }

    public Ray transform(Matrix4x4 m) {
        return new Ray(m.multiply(origin), m.multiply(direction));
    }
}