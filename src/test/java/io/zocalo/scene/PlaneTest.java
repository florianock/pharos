package io.zocalo.scene;

import io.zocalo.base.Ray;
import io.zocalo.base.Tuple;
import io.zocalo.scene.shapes.Plane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

    @Test
    void theNormalOfAPlaneIsConstantEverywhere() {
        var p = new Plane();
        var n1 = p.localNormalAt(Tuple.point(0, 0, 0));
        var n2 = p.localNormalAt(Tuple.point(10, 0, -10));
        var n3 = p.localNormalAt(Tuple.point(-5, 0, 150));

        assertEquals(Tuple.vector(0, 1, 0), n1);
        assertEquals(Tuple.vector(0, 1, 0), n2);
        assertEquals(Tuple.vector(0, 1, 0), n3);
    }

    @Test
    void intersectWithARayParallelToThePlane() {
        var p = new Plane();
        var r = new Ray(Tuple.point(0, 10, 0),Tuple.vector(0, 0, 1));
        var xs = p.intersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void intersectWithACoplanarRay() {
        var p = new Plane();
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var xs = p.intersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void aRayIntersectingAPlaneFromAbove() {
        var p = new Plane();
        var r = new Ray(Tuple.point(0, 1, 0), Tuple.vector(0, -1, 0));
        var xs = p.intersect(r);
        assertEquals(1, xs.count());
        var x = xs.get(0);
        assertEquals(1, x.t());
        assertEquals(p, x.object());
    }

    @Test
    void aRayIntersectingAPlaneFromBelow() {
        var p = new Plane();
        var r = new Ray(Tuple.point(0, -1, 0), Tuple.vector(0, 1, 0));
        var xs = p.intersect(r);
        assertEquals(1, xs.count());
        var x = xs.get(0);
        assertEquals(1, x.t());
        assertEquals(p, x.object());
    }
}