package io.zocalo.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTest {

    @Test
    void create_and_query() {
        var origin =Tuple.point(1, 2, 3);
        var direction = Tuple.vector(4, 5, 6);
        var r = new Ray(origin, direction);

        assertEquals(origin, r.origin());
        assertEquals(direction, r.direction());
    }

    @Test
    void create_and_query_throwsIfIllegalArguments() {
        var origin = Tuple.point(1, 2, 3);
        var direction = Tuple.vector(4, 5, 6);

        assertThrows(AssertionError.class, () -> new Ray(direction, direction));
        assertThrows(AssertionError.class, () -> new Ray(origin, origin));
    }

    @Test
    void position() {
        var r = new Ray(Tuple.point(2, 3, 4), Tuple.vector(1, 0, 0));

        assertEquals(Tuple.point(2, 3, 4), r.position(0));
        assertEquals(Tuple.point(3, 3, 4), r.position(1));
        assertEquals(Tuple.point(1, 3, 4), r.position(-1));
        assertEquals(Tuple.point(4.5, 3, 4), r.position(2.5));
    }

    @Test
    void translating_ray() {
        var r = new Ray(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
        var m = Transform.translation(3, 4, 5);

        var r2 = r.transform(m);

        assertEquals(Tuple.point(4, 6, 8), r2.origin());
        assertEquals(Tuple.vector(0, 1, 0), r2.direction());
    }

    @Test
    void scaling_ray() {
        var r = new Ray(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
        var m = Transform.scaling(2, 3, 4);

        var r2 = r.transform(m);

        assertEquals(Tuple.point(2, 6, 12), r2.origin());
        assertEquals(Tuple.vector(0, 3, 0), r2.direction());
    }
}