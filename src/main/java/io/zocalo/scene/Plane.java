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
        if (Math.abs(r.direction()[1]) < EPSILON) {
            return new Intersections();
        }
        var t = -r.origin()[1] / r.direction()[1];
        return new Intersections(new Intersection(t, this));
    }

    @Override
    public double[] localNormalAt(double[] localPoint) {
        if (!Tuple.isPoint(localPoint)) throw new IllegalArgumentException();
        return new double[] { 0, 1, 0, 0 };
    }
}
