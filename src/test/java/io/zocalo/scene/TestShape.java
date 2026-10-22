package io.zocalo.scene;

import io.zocalo.base.Intersections;
import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Ray;
import io.zocalo.base.Tuple;
import io.zocalo.scene.shapes.Shape;

public class TestShape extends Shape {

    private Ray savedRay;

    public TestShape() {
        super(Matrix4x4.identity(), new Material());
    }

    public TestShape(Matrix4x4 transform) {
        super(transform, new Material());
    }

    public TestShape(Material material) {
        super(Matrix4x4.identity(), material);
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
