package io.zocalo.scene;

import io.zocalo.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {

    @Test
    void createWorldTest() {
        var w = new World();
        assertEquals(0, w.objects().length);
        assertNull(w.light());
    }

    @Test
    void defaultWorldTest() {
        var light = Light.pointLight(Tuple.point(-10, 10, -10), Color.create(1, 1, 1));
        var s1 = new Sphere
                .Builder()
                .material(new Material
                        .Builder()
                        .color(Color.create(0.8f, 1.0f, 0.6f))
                        .diffuse(0.7f)
                        .specular(0.2f)
                        .build())
                .build();
        var s2 = new Sphere.Builder().transform(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)).build();
        var w = World.defaultWorld();
        assertEquals(light, w.light());
        assertTrue(Arrays.asList(w.objects()).contains(s1));
        assertTrue(Arrays.asList(w.objects()).contains(s2));
    }

    @Test
    void intersectWorldWithRay() {
        var w = World.defaultWorld();
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        IntersectionList xs = w.intersect(r);
        assertEquals(4, xs.count());
        assertEquals(4, xs.get(0).t());
        assertEquals(4.5, xs.get(1).t());
        assertEquals(5.5, xs.get(2).t());
        assertEquals(6, xs.get(3).t());
    }
}