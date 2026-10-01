package io.zocalo.scene;

import io.zocalo.kernel.Ray;
import org.junit.jupiter.api.Test;

import static io.zocalo.Utils.EPSILON;
import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

    @Test
    void theNormalOfAPlaneIsConstantEverywhere() {
        var p = new Plane();
        var n1 = p.localNormalAt(new double[]{0, 0, 0, 1});
        var n2 = p.localNormalAt(new double[]{10, 0, -10, 1});
        var n3 = p.localNormalAt(new double[]{-5, 0, 150, 1});

        assertArrayEquals(new double[]{ 0, 1, 0, 0 }, n1, EPSILON);
        assertArrayEquals(new double[]{ 0, 1, 0, 0 }, n2, EPSILON);
        assertArrayEquals(new double[]{ 0, 1, 0, 0 }, n3, EPSILON);
    }

    @Test
    void intersectWithARayParallelToThePlane() {
        var p = new Plane();
        var r = new Ray(new double[]{0, 10, 0, 1}, new double[]{0, 0, 1, 0});
        var xs = p.localIntersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void intersectWithACoplanarRay() {
        var p = new Plane();
        var r = new Ray(new double[]{0, 0, 0, 1}, new double[]{0, 0, 1, 0});
        var xs = p.localIntersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void aRayIntersectingAPlaneFromAbove() {
        var p = new Plane();
        var r = new Ray(new double[]{0, 1, 0, 1}, new double[]{0, -1, 0, 0});
        var xs = p.localIntersect(r);
        assertEquals(1, xs.count());
        var x = xs.get(0);
        assertEquals(1, x.t());
        assertEquals(p, x.object());
    }

    @Test
    void aRayIntersectingAPlaneFromBelow() {
        var p = new Plane();
        var r = new Ray(new double[]{0, -1, 0, 1}, new double[]{0, 1, 0, 0});
        var xs = p.localIntersect(r);
        assertEquals(1, xs.count());
        var x = xs.get(0);
        assertEquals(1, x.t());
        assertEquals(p, x.object());
    }
}