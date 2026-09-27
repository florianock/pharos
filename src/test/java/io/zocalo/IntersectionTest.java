package io.zocalo;

import io.zocalo.kernel.*;
import io.zocalo.scene.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    @Test
    void intersection_encapsulates_t_and_object() {
        var s = Shape.sphere();
        var i = new Intersection(3.5f, s);

        assertEquals(3.5f, i.t());
        assertEquals(s, i.shape());
    }

    @Test
    void aggregating_intersections() {
        var s = Shape.sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);

        var xs = new Intersections(i1, i2);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).shape());
        assertEquals(s, xs.get(1).shape());
    }

    @Test
    void hit_when_all_intersections_have_positive_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i1, i.get());
    }

    @Test
    void hit_when_some_intersections_have_negative_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(-1, s);
        var i2 = new Intersection(1, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i2, i.get());
    }

    @Test
    void hit_when_all_intersections_have_negative_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(-2, s);
        var i2 = new Intersection(-1, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isEmpty());
    }

    @Test
    void hit_is_always_the_lowest_non_negative_intersection() {
        var s = Shape.sphere();
        var i1 = new Intersection(5, s);
        var i2 = new Intersection(7, s);
        var i3 = new Intersection(-3, s);
        var i4 = new Intersection(2, s);
        var xs = new Intersections(i1, i2, i3, i4);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i4, i.get());
    }

    @Test
    void precomputing_state_of_intersection() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = Shape.sphere();
        var i = new Intersection(4, shape);
        var comps = i.prepareComputations(r);
        assertEquals(i.t(), comps.t());
        assertEquals(i.shape(), comps.object());
        assertEquals(Tuple.point(0, 0, -1), comps.point());
        assertEquals(Tuple.vector(0, 0, -1), comps.eyev());
        assertEquals(Tuple.vector(0, 0, -1), comps.normalv());
    }

    @Test
    void the_hit_when_intersection_occurs_on_the_outside() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = Shape.sphere();
        var i = new Intersection(4, shape);
        var comps = i.prepareComputations(r);
        assertFalse(comps.inside());
    }

    @Test
    void the_hit_when_intersection_occurs_on_the_inside() {
        var r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var shape = Shape.sphere();
        var i = new Intersection(1, shape);
        var comps = i.prepareComputations(r);
        assertEquals(Tuple.point(0, 0, 1), comps.point());
        assertEquals(Tuple.vector(0, 0, -1), comps.eyev());
        assertTrue(comps.inside());
        // normal would have been (0, 0, 1), but is inverted!
        assertEquals(Tuple.vector(0, 0, -1), comps.normalv());
    }

    @Test
    void the_hit_should_offset_the_point() {
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = Shape.sphere(Matrix4x4.translation(0, 0, 1));
        var i = new Intersection(5, shape);
        var comps = i.prepareComputations(r);
        assertTrue(comps.overPoint().z() < -Utils.EPSILON/2);
        assertTrue(comps.point().z() > comps.overPoint().z());
    }
}