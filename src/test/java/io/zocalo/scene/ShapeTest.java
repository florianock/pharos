package io.zocalo.scene;

import io.zocalo.Utils;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {

    @Test
    void theDefaultTransformation() {
        var s = new TestShape();
        assertEquals(Matrix4x4.identity(), s.getTransform());
    }

    @Test
    void assigningTransformation() {
        var s = new TestShape();
        s.setTransform(Matrix4x4.translation(2, 3, 4));
        assertEquals(Matrix4x4.translation(2, 3, 4), s.getTransform());
    }

    @Test
    void theDefaultMaterial() {
        var s = new TestShape();
        var m = s.getMaterial();
        assertEquals(new Material(), m);
    }

    @Test
    void assigningMaterial() {
        var s = new TestShape();
        var m = new Material.Builder().ambient(1.0).build();
        s.setMaterial(m);
        assertEquals(m, s.getMaterial());
    }


    @Test
    void intersectingScaledSphereWithRay() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new TestShape();
        s.setTransform(Matrix4x4.scaling(2, 2, 2));

        var _ = s.intersect(r);

        var savedRay = s.getSavedRay();
        assertEquals(Tuple.point(0, 0, -2.5), savedRay.origin());
        assertEquals(Tuple.vector(0, 0, 0.5), savedRay.direction());
    }

    @Test
    void intersectingTranslatedSphereWithRay() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new TestShape();
        s.setTransform(Matrix4x4.translation(5, 0, 0));

        var _ = s.intersect(r);

        var savedRay = s.getSavedRay();
        assertEquals(Tuple.point(-5, 0, -5), savedRay.origin());
        assertEquals(Tuple.vector(0, 0, 1), savedRay.direction());
    }

    @Test
    void computingNormalOnTranslatedSphere() {
        var s = new TestShape();
        s.setTransform(Matrix4x4.translation(0, 1, 0));

        var n = s.normalAt(Tuple.point(0, 1.70711, -0.70711));

        assertEquals(Tuple.vector(0, 0.70711, -0.70711), n);
    }

    @Test
    void computingNormalOnTransformedSphere() {
        var s = new TestShape();
        var m = Matrix4x4.scaling(1, 0.5, 1).multiply(Matrix4x4.rotationOnZ(Utils.PI / 5));
        s.setTransform(m);

        var n = s.normalAt(Tuple.point(0, Math.sqrt(2) / 2, -Math.sqrt(2) / 2));

        assertEquals(Tuple.vector(0, 0.97014, -0.24254), n);
    }
}