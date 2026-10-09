package io.zocalo.kernel;

import io.zocalo.scene.Shape;

import static io.zocalo.Utils.SHADOW_OFFSET;

public record Intersection(double t, Shape object) {

    public Computations prepareComputations(Ray r) {
        var point = r.position(t);
        var eyev = r.direction().negate();
        var normalv = object.normalAt(point);
        var inside = false;

        if (normalv.dot(eyev) < 0) {
            inside = true;
            normalv = normalv.negate();
        }

        var overPoint = normalv.multiply(SHADOW_OFFSET).add(point);

        return new Computations(t, object, point, eyev, normalv, inside, overPoint);
    }
}