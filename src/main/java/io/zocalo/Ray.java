package io.zocalo;

import static io.zocalo.Tuple.add;
import static io.zocalo.Tuple.multiply;

public record Ray(float[] origin, float[] direction) {

    public Ray {
        if (!Tuple.isPoint(origin)) throw new IllegalStateException("Not a valid origin: " + origin);
        if (!Tuple.isVector(direction)) throw new IllegalStateException("Not a valid direction: " + direction);
    }

    public Tuple position(float t) {
        return new Tuple(position(origin.clone(), direction.clone(), t));
    }

    public static float[] position(float[] o, float[] d, float t) {
        return add(o, multiply(d, t));
    }

    public Ray transform(Matrix4x4 m) {
        return Ray.create(m.multiply(origin), m.multiply(direction));
    }

    public static Ray create(Tuple origin, Tuple direction) {
        return new Ray(origin.values(), direction.values());
    }

    public static Ray create(float[] origin, float[] direction) {
        return new Ray(origin, direction);
    }
}