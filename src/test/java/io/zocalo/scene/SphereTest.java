package io.zocalo.scene;

import io.zocalo.*;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.zocalo.Utils.EPSILON;
import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

    @Test
    void ray_intersects_sphere_at_two_points() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(4.0, xs.get(0).t());
        assertEquals(6.0, xs.get(1).t());
    }

    @Test
    void ray_intersects_sphere_at_tangent() {
        var r = Ray.create(Tuple.point(0, 1, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(5.0, xs.get(0).t());
        assertEquals(5.0, xs.get(1).t());
    }

    @Test
    void ray_misses_sphere() {
        var r = Ray.create(Tuple.point(0, 2, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(0, xs.count());
    }

    @Test
    void ray_originates_inside_sphere() {
        var r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-1.0, xs.get(0).t());
        assertEquals(1.0, xs.get(1).t());
    }

    @Test
    void ray_behind_sphere() {
        var r = Ray.create(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var s = new Sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-6.0, xs.get(0).t());
        assertEquals(-4.0, xs.get(1).t());
    }

    @Test
    void intersect_sets_object_on_intersection() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere();
        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).shape());
        assertEquals(s, xs.get(1).shape());
    }

    @Test
    void default_transform_is_identity_matrix() {
        var s = new Sphere();

        Assertions.assertEquals(Matrix4x4.identity(), s.getTransform());
    }

    @Test
    void changing_spheres_default_transformation() {
        var t = Matrix4x4.translation(2, 3, 4);
        var s = new Sphere(t);

        assertEquals(t, s.getTransform());
    }

    @Test
    void intersecting_scaled_sphere_with_ray() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere(Matrix4x4.scaling(2, 2, 2));

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(3, xs.get(0).t());
        assertEquals(7, xs.get(1).t());
    }

    @Test
    void intersecting_translated_sphere_with_ray() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere(Matrix4x4.translation(5, 0, 0));

        var xs = s.intersect(r);

        assertEquals(0, xs.count());
    }

    @Test
    void normal_on_sphere_at_point_on_x_axis() {
        var s = new Sphere();
        var n = s.normalAt(Tuple.point(1, 0, 0));

        assertArrayEquals(new double[] { 1.0, 0.0, 0.0, 0 }, n);
    }

    @Test
    void normal_on_sphere_at_point_on_y_axis() {
        var s = new Sphere();
        var n = s.normalAt(Tuple.point(0, 1, 0));

        assertArrayEquals(new double[] { 0.0, 1.0, 0.0, 0 }, n);
    }

    @Test
    void normal_on_sphere_at_point_on_z_axis() {
        var s = new Sphere();
        var n = s.normalAt(Tuple.point(0, 0, 1));

        assertArrayEquals(new double[] { 0.0, 0.0, 1.0, 0 }, n);
    }

    @Test
    void normal_on_sphere_at_nonaxial_point() {
        var s = new Sphere();
        var v = (float) Math.sqrt(3)/3;
        var n = s.normalAt(Tuple.point(v, v, v));

        assertArrayEquals(new double[] { Math.sqrt(3)/3, Math.sqrt(3)/3, Math.sqrt(3)/3, 0 }, n);
    }

    @Test
    void normal_is_normalized() {
        var s = new Sphere();
        var v = (float) Math.sqrt(3)/3;
        var n = s.normalAt(Tuple.point(v, v, v));

        assertEquals(n, Tuple.normalize(n));
    }

    @Test
    void computing_normal_on_translated_sphere() {
        var s = new Sphere(Matrix4x4.translation(0, 1, 0));
        var n = s.normalAt(Tuple.point(0, 1.70711, -0.70711));

        assertArrayEquals(new double[] { 0, 0.70711, -0.70711, 0 }, n, EPSILON);
    }

    @Test
    void computing_normal_on_transformed_sphere() {
        var t = Matrix4x4.scaling(1, 0.5, 1).multiply(Matrix4x4.rotationOnZ(Utils.PI/5));
        var s = new Sphere(t);
        var n = s.normalAt(Tuple.point(0, Math.sqrt(2)/2, -Math.sqrt(2)/2));

        assertArrayEquals(new double[] { 0, 0.97014, -0.24254, 0 }, n, EPSILON);
    }

    @Test
    void sphere_has_default_material() {
        var s = new Sphere();
        var m = s.getMaterial();

        assertEquals(new Material(), m);
    }

    @Test
    void sphere_may_be_assigned_material() {
        var m = new Material.Builder().ambient(1).build();
        var s = new Sphere(m);

        assertEquals(m, s.getMaterial());
    }
}