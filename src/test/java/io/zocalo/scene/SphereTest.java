package io.zocalo.scene;

import io.zocalo.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

    @Test
    void ray_intersects_sphere_at_two_points() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(4.0, xs.get(0).t());
        assertEquals(6.0, xs.get(1).t());
    }

    @Test
    void ray_intersects_sphere_at_tangent() {
        var r = Ray.create(Tuple.point(0, 1, -5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(5.0, xs.get(0).t());
        assertEquals(5.0, xs.get(1).t());
    }

    @Test
    void ray_misses_sphere() {
        var r = Ray.create(Tuple.point(0, 2, -5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();

        var xs = s.intersect(r);

        assertEquals(0, xs.count());
    }

    @Test
    void ray_originates_inside_sphere() {
        var r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-1.0, xs.get(0).t());
        assertEquals(1.0, xs.get(1).t());
    }

    @Test
    void ray_behind_sphere() {
        var r = Ray.create(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(-6.0, xs.get(0).t());
        assertEquals(-4.0, xs.get(1).t());
    }

    @Test
    void intersect_sets_object_on_intersection() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere();
        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).object());
        assertEquals(s, xs.get(1).object());
    }

    @Test
    void default_transform_is_identity_matrix() {
        var s = Shape.sphere();

        Assertions.assertEquals(Matrix4x4.identity(), s.transform());
    }

    @Test
    void changing_spheres_default_transformation() {
        var t = Matrix4x4.translation(2, 3, 4);
        var s = new Sphere.Builder().transform(t).build();

        assertEquals(t, s.transform());
    }

    @Test
    void intersecting_scaled_sphere_with_ray() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = new Sphere.Builder().transform(Matrix4x4.scaling(2, 2, 2)).build();

        var xs = s.intersect(r);

        assertEquals(2, xs.count());
        assertEquals(3, xs.get(0).t());
        assertEquals(7, xs.get(1).t());
    }

    @Test
    void intersecting_translated_sphere_with_ray() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var s = Shape.sphere(Matrix4x4.translation(5, 0, 0));

        var xs = s.intersect(r);

        assertEquals(0, xs.count());
    }

    @Test
    void normal_on_sphere_at_point_on_x_axis() {
        var s = Shape.sphere();
        var n = s.normalAt(Tuple.point(1, 0, 0));

        assertEquals(Tuple.vector(1, 0, 0), n);
    }

    @Test
    void normal_on_sphere_at_point_on_y_axis() {
        var s = Shape.sphere();
        var n = s.normalAt(Tuple.point(0, 1, 0));

        assertEquals(Tuple.vector(0, 1, 0), n);
    }

    @Test
    void normal_on_sphere_at_point_on_z_axis() {
        var s = Shape.sphere();
        var n = s.normalAt(Tuple.point(0, 0, 1));

        assertEquals(Tuple.vector(0, 0, 1), n);
    }

    @Test
    void normal_on_sphere_at_nonaxial_point() {
        var s = Shape.sphere();
        var v = (float) Math.sqrt(3)/3;
        var n = s.normalAt(Tuple.point(v, v, v));

        assertEquals(Tuple.vector(v, v, v), n);
    }

    @Test
    void normal_is_normalized() {
        var s = Shape.sphere();
        var v = (float) Math.sqrt(3)/3;
        var n = s.normalAt(Tuple.point(v, v, v));

        assertEquals(n, n.normalize());
    }

    @Test
    void computing_normal_on_translated_sphere() {
        var s = new Sphere.Builder().transform(Matrix4x4.translation(0, 1, 0)).build();
        var n = s.normalAt(Tuple.point(0, 1.70711f, -0.70711f));

        assertEquals(Tuple.vector(0, 0.70711f, -0.70711f), n);
    }

    @Test
    void computing_normal_on_transformed_sphere() {
        var m = Matrix4x4.scaling(1, 0.5f, 1).multiply(Matrix4x4.rotationOnZ(Utils.PI/5));
        var s = new Sphere.Builder().transform(m).build();
        var n = s.normalAt(Tuple.point(0, (float) Math.sqrt(2)/2, (float) -Math.sqrt(2)/2));

        assertEquals(Tuple.vector(0, 0.97014f, -0.24254f), n);
    }

    @Test
    void sphere_has_default_material() {
        var s = Shape.sphere();
        var m = s.material();

        assertEquals(new Material(), m);
    }

    @Test
    void sphere_may_be_assigned_material() {
        var m = new Material.Builder().ambient(1).build();
        var s = new Sphere.Builder().material(m).build();

        assertEquals(m, s.material());
    }
}