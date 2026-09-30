package io.zocalo.kernel;

import java.util.Arrays;

import static io.zocalo.kernel.Tuple.add;
import static io.zocalo.kernel.Tuple.multiply;

public record Ray(double[] origin, double[] direction) {

    public Ray {
        if (!Tuple.isPoint(origin)) throw new IllegalStateException("Not a valid origin: " + Arrays.toString(origin));
        if (!Tuple.isVector(direction)) throw new IllegalStateException("Not a valid direction: " + Arrays.toString(direction));
    }

    public Tuple position(double t) {
        return new Tuple(position(origin.clone(), direction.clone(), t));
    }

    public static double[] position(double[] o, double[] d, double t) {
        return add(o, multiply(d, t));
    }

    public Ray transform(Matrix4x4 m) {
        return Ray.create(m.multiply(origin), m.multiply(direction));
    }

    public static Ray create(Tuple origin, Tuple direction) {
        return new Ray(origin.values(), direction.values());
    }

    public static Ray create(double[] origin, double[] direction) {
        return new Ray(origin, direction);
    }
}