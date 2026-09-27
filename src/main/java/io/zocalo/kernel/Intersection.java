package io.zocalo.kernel;

import io.zocalo.scene.Shape;

import static io.zocalo.Utils.SHADOW_OFFSET;

public record Intersection(float t, Shape shape) {

    public Computations prepareComputations(Ray r) {
        var point = r.position(t);
        var eyev = new Tuple(Tuple.negate(r.direction()));
        var normalv = shape.normalAt(point);
        var inside = false;

        if (normalv.dot(eyev) < 0) {
            inside = true;
            normalv = normalv.negate();
        }

        var overPoint = point.add(normalv.multiply(SHADOW_OFFSET));

        return new Computations(t, shape, point, eyev, normalv, inside, overPoint);
    }
}