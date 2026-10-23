package io.zocalo.scene;

import io.zocalo.base.Ray;
import io.zocalo.base.Tuple;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

    @Test
    void aSphereIsAShape() {
        var s = new Sphere();
        assertInstanceOf(Shape.class, s);
    }

    @Test
    void ray_intersects_sphere_at_two_points() {
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(4.0, xs.get(0).t());
        assertEquals(6.0, xs.get(1).t());
    }

    @Test
    void ray_intersects_sphere_at_tangent() {
        var r = new Ray(Tuple.point(0, 1, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(5.0, xs.get(0).t());
        assertEquals(5.0, xs.get(1).t());
    }

    @Test
    void ray_misses_sphere() {
        var r = new Ray(Tuple.point(0, 2, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(0, xs.count());
    }

    @Test
    void ray_originates_inside_sphere() {
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-1.0, xs.get(0).t());
        assertEquals(1.0, xs.get(1).t());
    }

    @Test
    void ray_behind_sphere() {
        var r = new Ray(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-6.0, xs.get(0).t());
        assertEquals(-4.0, xs.get(1).t());
    }

    @Test
    void intersect_sets_object_on_intersection() {
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).object());
        assertEquals(s, xs.get(1).object());
    }

    @Test
    void normal_on_sphere_at_point_on_x_axis() {
        var s = new Sphere();

        var n = s.localNormalAt(Tuple.point(1, 0, 0));

        assertEquals(Tuple.vector(1.0, 0.0, 0.0), n);
    }

    @Test
    void normal_on_sphere_at_point_on_y_axis() {
        var s = new Sphere();

        var n = s.localNormalAt(Tuple.point(0, 1, 0));

        assertEquals(Tuple.vector(0.0, 1.0, 0.0), n);
    }

    @Test
    void normal_on_sphere_at_point_on_z_axis() {
        var s = new Sphere();

        var n = s.localNormalAt(Tuple.point(0, 0, 1));

        assertEquals(Tuple.vector(0.0, 0.0, 1.0), n);
    }

    @Test
    void normal_on_sphere_at_nonaxial_point() {
        var s = new Sphere();
        var v = Math.sqrt(3)/3;

        var n = s.localNormalAt(Tuple.point(v, v, v));

        assertEquals(Tuple.vector(Math.sqrt(3)/3, Math.sqrt(3)/3, Math.sqrt(3)/3), n);
    }

    @Test
    void normal_is_normalized() {
        var s = new Sphere();
        var v = Math.sqrt(3)/3;

        var n = s.localNormalAt(Tuple.point(v, v, v));

        var normalizedN = n.normalize();
        assertEquals(normalizedN, n);
    }

    @Test
    void aHelperForProducingASphereWithAGlassyMaterial() {
        var s = Sphere.glassSphere();
        assertEquals(1, s.getMaterial().transparency());
        assertEquals(1.5, s.getMaterial().refractiveIndex());
    }
}