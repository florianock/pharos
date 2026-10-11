package io.zocalo.scene;

import io.zocalo.kernel.Intersections;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.shapes.Shape;

public class TestShape extends Shape {

    private Ray savedRay;

    public TestShape() {
        super(Matrix4x4.identity(), new Material());
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
