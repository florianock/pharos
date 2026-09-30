package io.zocalo.kernel;

import io.zocalo.scene.Shape;

import static io.zocalo.Utils.SHADOW_OFFSET;

public record Intersection(double t, Shape shape) {

    public Computations prepareComputations(Ray r) {
        var point = r.position(t);
        var eyev = Tuple.negate(r.direction());
        var normalv = shape.normalAt(point);
        var inside = false;

        if (Tuple.dot(normalv, eyev) < 0) {
            inside = true;
            Tuple.negate(normalv);
        }

        var overPoint = point.add(new Tuple(Tuple.multiply(normalv.clone(), SHADOW_OFFSET)));

        return new Computations(t, shape, point, new Tuple(eyev), new Tuple(normalv), inside, overPoint);
    }
}