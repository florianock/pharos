package io.zocalo;

import io.zocalo.scene.Sphere;

public record Intersection(float t, Sphere object) {

    public Computations prepareComputations(Ray r) {
        var point = r.position(t);
        var eyev = new Tuple(Tuple.negate(r.direction()));
        var normalv = object.normalAt(point);
        var inside = false;
        if (normalv.dot(eyev) < 0) {
            inside = true;
            normalv = normalv.negate();
        }
        return new Computations(t, object, point, eyev, normalv, inside);
    }
}