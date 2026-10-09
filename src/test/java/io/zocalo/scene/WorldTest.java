package io.zocalo.scene;

import io.zocalo.kernel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {

    @Test
    void createWorldTest() {
        var w = new World();
        assertEquals(0, w.objects().length);
        assertNull(w.lights());
    }

    @Test
    void defaultWorldTest() {
        var light = Light.pointLight(Tuple.point(-10, 10, -10), Color.create(1, 1, 1));
        var s1 = new Sphere(new Material
                        .Builder()
                        .color(Color.create(0.8, 1.0, 0.6))
                        .diffuse(0.7)
                        .specular(0.2)
                        .build());
        var s2 = new Sphere(Matrix4x4.scaling(0.5, 0.5, 0.5));
        var w = World.defaultWorld();
        assertEquals(light, (w.lights())[0]);
        assertTrue(Arrays.asList(w.objects()).contains(s1));
        assertTrue(Arrays.asList(w.objects()).contains(s2));
    }

    @Test
    void intersectWorldWithRay() {
        var w = World.defaultWorld();
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        Intersections xs = w.intersect(r);
        assertEquals(4, xs.count());
        assertEquals(4, xs.get(0).t());
        assertEquals(4.5, xs.get(1).t());
        assertEquals(5.5, xs.get(2).t());
        assertEquals(6, xs.get(3).t());
    }

    @Test
    void shadingAnIntersection() {
        var w = World.defaultWorld();
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = (w.objects())[0];
        var i = new Intersection(4, shape);
        var comps = i.prepareComputations(r);
        var c = w.shadeHit(comps, false);
        assertEquals(Color.create(0.38066, 0.47583, 0.2855), c);
    }

    @Test
    void shadingAnIntersectionFromInside() {
        var w = new World(new Light[]{Light.pointLight(Tuple.point(0, 0.25, 0), Color.WHITE)});
        var r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var shape = (w.objects())[1];
        var i = new Intersection(0.5, shape);
        var comps = i.prepareComputations(r);
        var c =  w.shadeHit(comps, false);
        assertEquals(Color.create(0.90498, 0.90498, 0.90498), c);
    }

    @Test
    void colorWhenARayMisses() {
        var w = World.defaultWorld();
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 1, 0));
        var c = w.colorAt(r);
        assertEquals(Color.BLACK, c);
    }

    @Test
    void colorWhenARayHits() {
        var w =  World.defaultWorld();
        var r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var c = w.colorAt(r);
        assertEquals(Color.create(0.38066, 0.47583, 0.2855), c);
    }

    @Test
    void colorWithAnIntersectionBehindTheRay() {
        var w =  World.defaultWorld();
        var outer = (w.objects())[0];
        outer.getMaterial().ambient(1);
        var inner = (w.objects())[1];
        inner.getMaterial().ambient(1);
        var r = Ray.create(Tuple.point(0, 0, 0.75), Tuple.vector(0, 0, -1));
        var c = w.colorAt(r);
        assertEquals(inner.getMaterial().color(), c);
    }

    @Test
    void shadeHitIsGivenAnIntersectionInShadow() {
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var s1 = new Sphere();
        var s2 = new Sphere(Matrix4x4.translation(0, 0, 10));
        var w = new World(new Shape[] { s1, s2 }, new Light[]{light});
        var r = Ray.create(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var i = new Intersection(4, s2);

        var comps = i.prepareComputations(r);
        var c = w.shadeHit(comps, true);

        assertEquals(Color.create(0.1, 0.1, 0.1), c);
    }

    @ParameterizedTest
    @CsvSource({
            "-10, -10, -10, false",
            "10, 10, 10, true",
            "-20, -20, -20, false",
            "-5, -5, -5, false"

    })
    void isShadowedTestsForOcclusionBetweenTwoPoints(int px, int py, int pz, boolean expected) {
        var w = World.defaultWorld();
        var lightPosition = new double[] { -10, -10, -10, 1 };
        var point = new double[] { px, py, pz, 1 };

        assertEquals(expected, w.isShadowed(lightPosition, point));
    }
}