package io.zocalo.kernel;

import io.zocalo.scene.Shape;

import static io.zocalo.Utils.SHADOW_OFFSET;

public record Intersection(double t, Shape object) {

    public Computations prepareComputations(Ray r) {
        var point = Ray.position(r.origin().clone(), r.direction().clone(), t);
        var eyev = Tuple.negate(r.direction());
        var normalv = object.normalAt(point);
        var inside = false;

        if (Tuple.dot(normalv, eyev) < 0) {
            inside = true;
            Tuple.negate(normalv);
        }

        var overPoint = Tuple.add(point.clone(), Tuple.multiply(normalv.clone(), SHADOW_OFFSET));

        return new Computations(t, object, point, eyev, normalv, inside, overPoint);
    }
}