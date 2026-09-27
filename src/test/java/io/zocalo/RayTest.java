package io.zocalo;

import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTest {

    @Test
    void create_and_query() {
        var origin = new float[] {1, 2, 3, 1};
        var direction = new float[] {4, 5, 6, 0};
        var r = Ray.create(origin, direction);

        assertArrayEquals(origin, r.origin());
        assertArrayEquals(direction, r.direction());
    }

    @Test
    void create_and_query_throwsIfIllegalArguments() {
        var origin = Tuple.point(1, 2, 3);
        var direction = Tuple.vector(4, 5, 6);

        assertThrows(IllegalStateException.class, () -> Ray.create(direction, direction));
        assertThrows(IllegalStateException.class, () -> Ray.create(origin, origin));
    }

    @Test
    void position() {
        var r = Ray.create(Tuple.point(2, 3, 4), Tuple.vector(1, 0, 0));

        assertEquals(Tuple.point(2, 3, 4), r.position(0));
        assertEquals(Tuple.point(3, 3, 4), r.position(1));
        assertEquals(Tuple.point(1, 3, 4), r.position(-1));
        assertEquals(Tuple.point(4.5f, 3, 4), r.position(2.5f));
    }

    @Test
    void translating_ray() {
        var r = Ray.create(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
        var m = Matrix4x4.translation(3, 4, 5);

        var r2 = r.transform(m);

        assertArrayEquals(new float[]{4, 6, 8, 1}, r2.origin());
        assertArrayEquals(new float[]{0, 1, 0, 0}, r2.direction());
    }

    @Test
    void scaling_ray() {
        var r = Ray.create(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
        var m = Matrix4x4.scaling(2, 3, 4);

        var r2 = r.transform(m);

        assertArrayEquals(new float[] {2, 6, 12, 1}, r2.origin());
        assertArrayEquals(new float[] {0, 3, 0, 0}, r2.direction());
    }
}