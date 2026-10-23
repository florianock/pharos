package io.zocalo.scene;

import io.zocalo.base.*;
import io.zocalo.scene.shapes.Shape;

public class TestShape extends Shape {

    private Ray savedRay;

    public TestShape() {
        super(Transform.identity(), new Material());
    }

    public TestShape(Matrix4x4 transform) {
        super(transform, new Material());
    }

    public TestShape(Material material) {
        super(Transform.identity(), material);
    }

    public TestShape(Matrix4x4 transform, Material material) {
        super(transform, material);
    }

    public Ray getSavedRay() {
        return savedRay;
    }

    @Override
    public Intersections localIntersect(Ray r) {
        savedRay = r;
        return new Intersections();
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        assert localPoint.isPoint();
        return Tuple.vector(localPoint.x(), localPoint.y(), localPoint.z());
    }
}
