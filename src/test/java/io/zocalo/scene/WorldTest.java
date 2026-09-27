package io.zocalo.scene;

import io.zocalo.kernel.*;
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
        var c = w.shadeHit(comps);
        assertEquals(Color.create(0.38066f, 0.47583f, 0.2855f), c);
    }

    @Test
    void shadingAnIntersectionFromInside() {
        var w = new World(Light.pointLight(Tuple.point(0, 0.25f, 0), Color.WHITE));
        var r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var shape = (w.objects())[1];
        var i = new Intersection(0.5f, shape);
        var comps = i.prepareComputations(r);
        var c =  w.shadeHit(comps);
        assertEquals(Color.create(0.90498f, 0.90498f, 0.90498f), c);
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
        assertEquals(Color.create(0.38066f, 0.47583f, 0.2855f), c);
    }

    @Test
    void colorWithAnIntersectionBehindTheRay() {
        var w =  World.defaultWorld();
        var outer = (w.objects())[0];
        outer.material().ambient(1);
        var inner = (w.objects())[1];
        inner.material().ambient(1);
        var r = Ray.create(Tuple.point(0, 0, 0.75f), Tuple.vector(0, 0, -1));
        var c = w.colorAt(r);
        assertEquals(inner.material().color(), c);
    }

    @Test
    void noShadowWhenNothingIsCollinearWithPointAndLight() {
        var w = World.defaultWorld();
        var p = Tuple.point(0, 10, 0);

        assertFalse(w.isShadowed(p));
    }

    @Test
    void shadowWhenObjectIsBetweenPointAndLight() {
        var w = World.defaultWorld();
        var p = Tuple.point(10, -10, 10);

        assertTrue(w.isShadowed(p));
    }

    @Test
    void noShadowWhenObjectIsBehindLight() {
        var w = World.defaultWorld();
        var p = Tuple.point(-20, 20, -20);

        assertFalse(w.isShadowed(p));
    }

    @Test
    void noShadowWhenObjectIsBehindPoint() {
        var w = World.defaultWorld();
        var p = Tuple.point(-2, 2, -2);

        assertFalse(w.isShadowed(p));
    }

    @Test
    void shadeHitIsGivenAnIntersectionInShadow() {
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var s1 = Shape.sphere();
        var s2 = Shape.sphere(Matrix4x4.translation(0, 0, 10));
        var w = new World(new Shape[] { s1, s2 }, light);
        var r = Ray.create(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var i = new Intersection(4, s2);

        var comps = i.prepareComputations(r);
        var c = w.shadeHit(comps);

        assertEquals(Color.create(0.1f, 0.1f, 0.1f), c);
    }
}