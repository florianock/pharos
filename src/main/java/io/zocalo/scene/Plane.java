package io.zocalo.scene;

import io.zocalo.kernel.*;

import static io.zocalo.Utils.EPSILON;

public class Plane extends Shape {

    public Plane() {
        super(Matrix4x4.identity(), new Material());
    }

    public Plane(Matrix4x4 transform, Material material) {
        super(transform, material);
    }

    @Override
    public Intersections localIntersect(Ray r) {
        if (Math.abs(r.direction().y()) < EPSILON) {
            return new Intersections();
        }
        var t = -r.origin().y() / r.direction().y();
        return new Intersections(new Intersection(t, this));
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        if (!localPoint.isPoint()) throw new IllegalArgumentException();
        return Tuple.vector(0, 1, 0);
    }
}
