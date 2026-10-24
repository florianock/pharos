package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

import static io.zocalo.Utils.EPSILON;

public class Plane extends Shape {

    private static final BoundingBox BOUNDING_BOX = new BoundingBox(
            Tuple.point(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY),
            Tuple.point(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY)
    );

    public Plane() {
        this(Transform.identity(), new Material(), true);
    }

    public Plane(Material material) {
        this(Transform.identity(), material, true);
    }

    public Plane(Matrix4x4 transform, Material material) {
        this(transform, material, true);
    }
    public Plane(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
    }

    @Override
    public Intersections intersect(Ray worldRay) {
        var r = worldRay.transform(inverseTransform);
        if (Math.abs(r.direction().y()) < EPSILON) {
            return Intersections.empty();
        }
        var t = -r.origin().y() / r.direction().y();
        return new Intersections(new Intersection(t, this));
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        assert localPoint.isPoint();
        return Tuple.vector(0, 1, 0);
    }

    @Override
    public BoundingBox bounds() {
        return BOUNDING_BOX;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "material=" + material +
                ", transform=" + transform +
                '}';
    }
}
