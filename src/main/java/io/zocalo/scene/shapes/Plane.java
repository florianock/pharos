package io.zocalo.scene.shapes;

import io.zocalo.kernel.*;
import io.zocalo.scene.Material;

import static io.zocalo.Utils.EPSILON;

public class Plane extends Shape {

    public Plane() {
        super(Matrix4x4.identity(), new Material());
    }

    public Plane(Material material) {
        super(Matrix4x4.identity(), material);
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
        assert localPoint.isPoint();
        return Tuple.vector(0, 1, 0);
    }
}
