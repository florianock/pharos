package io.zocalo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    @Test
    void intersection_encapsulates_t_and_object() {
        var s = Shape.sphere();
        var i = new Intersection(3.5f, s);

        assertEquals(3.5f, i.t());
        assertEquals(s, i.object());
    }

    @Test
    void aggregating_intersections() {
        var s = Shape.sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);

        var xs = new IntersectionList(i1, i2);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).object());
        assertEquals(s, xs.get(1).object());
    }

    @Test
    void hit_when_all_intersections_have_positive_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);
        var xs = new IntersectionList(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i1, i.get());
    }

    @Test
    void hit_when_some_intersections_have_negative_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(-1, s);
        var i2 = new Intersection(1, s);
        var xs = new IntersectionList(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i2, i.get());
    }

    @Test
    void hit_when_all_intersections_have_negative_t() {
        var s = Shape.sphere();
        var i1 = new Intersection(-2, s);
        var i2 = new Intersection(-1, s);
        var xs = new IntersectionList(i2, i1);

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
        var xs = new IntersectionList(i1, i2, i3, i4);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i4, i.get());
    }
}