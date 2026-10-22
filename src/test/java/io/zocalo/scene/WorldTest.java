package io.zocalo.scene;

import io.zocalo.base.*;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;
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
        var light = Light.pointLight(Tuple.point(-10, 10, -10), new Color(1, 1, 1));
        var s1 = new Sphere(new Material.Builder()
                .color(new Color(0.8, 1.0, 0.6))
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
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
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
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = (w.objects())[0];
        var i = new Intersection(4, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var c = w.shadeHit(comps, 5, false, false, false);
        assertEquals(new Color(0.38066, 0.47583, 0.2855), c);
    }

    @Test
    void shadingAnIntersectionFromInside() {
        var w = new World(new Light[]{Light.pointLight(Tuple.point(0, 0.25, 0), Color.WHITE)});
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var shape = (w.objects())[1];
        var i = new Intersection(0.5, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var c = w.shadeHit(comps, 5, false, false, false);
        assertEquals(new Color(0.90498, 0.90498, 0.90498), c);
    }

    @Test
    void colorWhenARayMisses() {
        var w = World.defaultWorld();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 1, 0));
        var c = w.colorAt(r, 5);
        assertEquals(Color.BLACK, c);
    }

    @Test
    void colorWhenARayHits() {
        var w = World.defaultWorld();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var c = w.colorAt(r, 5);
        assertEquals(new Color(0.38066, 0.47583, 0.2855), c);
    }

    @Test
    void colorWithAnIntersectionBehindTheRay() {
        var defaultWorld = World.defaultWorld();
        var outer = (defaultWorld.objects())[0];
        var outerMaterial = new Material.Builder().from(outer.getMaterial()).ambient(1).build();
        var inner = (defaultWorld.objects())[1];
        var innerMaterial = new Material.Builder().from(inner.getMaterial()).ambient(1).build();
        var objects = new Shape[]{
                new Sphere(outer.getTransform(), outerMaterial),
                new Sphere(inner.getTransform(), innerMaterial)
        };
        var w = new World(objects);
        var r = new Ray(Tuple.point(0, 0, 0.75), Tuple.vector(0, 0, -1));
        var c = w.colorAt(r, 5);
        assertEquals(inner.getMaterial().color(), c);
    }

    @Test
    void shadeHitIsGivenAnIntersectionInShadow() {
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var s1 = new Sphere();
        var s2 = new Sphere(Matrix4x4.translation(0, 0, 10));
        var w = new World(new Shape[]{s1, s2}, new Light[]{light});
        var r = new Ray(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
        var i = new Intersection(4, s2);

        var comps = i.prepareComputations(r, new Intersections(i));
        var c = w.shadeHit(comps, 5, true, false, false);

        assertEquals(new Color(0.1, 0.1, 0.1), c);
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
        var lightPosition = Tuple.point(-10, -10, -10);
        var point = Tuple.point(px, py, pz);

        assertEquals(expected, w.isShadowed(lightPosition, point));
    }

    @Test
    void theReflectedColorForANonreflectiveMaterial() {
        var w = World.defaultWorld();
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var secondDefaultShape = w.objects()[1];
        var m = new Material.Builder().from(secondDefaultShape.getMaterial()).ambient(1).build();
        var t = secondDefaultShape.getTransform();
        var shape = new Sphere(t, m);
        var i = new Intersection(1, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var color = w.reflectedColor(comps, 5);
        assertEquals(Color.BLACK, color);
    }

    @Test
    void theReflectedColorForAReflectiveMaterial() {
        var shape = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().reflective(0.5).build());
        var w1 = World.defaultWorld();
        var w = new World(new Shape[]{w1.objects()[0], w1.objects()[1], shape});
        var r = new Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -Math.sqrt(2) / 2, Math.sqrt(2) / 2));
        var i = new Intersection(Math.sqrt(2), shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var color = w.reflectedColor(comps, 5);
        assertEquals(new Color(0.19032, 0.2379, 0.14274), color);
    }

    @Test
    void shadeHitWithAReflectiveMaterial() {
        var shape = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().reflective(0.5).build());
        var w1 = World.defaultWorld();
        var w = new World(new Shape[]{w1.objects()[0], w1.objects()[1], shape});
        var r = new Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -Math.sqrt(2) / 2, Math.sqrt(2) / 2));
        var i = new Intersection(Math.sqrt(2), shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var color = w.shadeHit(comps, 5);
        assertEquals(new Color(0.87677, 0.92436, 0.82918), color);
    }

    @Test
    void colorAtWithMutuallyReflectiveSurfaces() {
        var light = Light.pointLight(Tuple.point(0, 0, 0), Color.WHITE);
        var lower = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().reflective(1).build());
        var upper = new Plane(Matrix4x4.translation(0, 1, 0), new Material.Builder().reflective(1).build());
        var w = new World(new Shape[]{lower, upper}, new Light[]{light});
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 1, 0));
        assertDoesNotThrow(() -> w.colorAt(r, 5));
    }

    @Test
    void theReflectedColorAtTheMaximumRecursiveDepth() {
        var w1 = World.defaultWorld();
        var shape = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().reflective(0.5).build());
        var w = new World(new Shape[]{w1.objects()[0], w1.objects()[1], shape});
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 1, 0));
        var i = new Intersection(Math.sqrt(2), shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        var color = w.reflectedColor(comps, 0);
        assertEquals(Color.BLACK, color);
    }

    @Test
    void theRefractedColorWithAnOpaqueSurface() {
        var w = World.defaultWorld();
        var shape = w.objects()[0];
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var xs = new Intersections(new Intersection(4, shape), new Intersection(6, shape));
        var comps = xs.get(0).prepareComputations(r, xs);
        var c = w.refractedColor(comps, 5);
        assertEquals(Color.BLACK, c);
    }

    @Test
    void theRefractedColorAtTheMaximumRecursiveDepth() {
        var w = World.defaultWorld();
        var wo = w.objects()[0];
        var m = new Material.Builder().from(wo.getMaterial()).transparency(1.0).refractiveIndex(1.5).build();
        var shape = new Sphere(m);
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var xs = new Intersections(new Intersection(4, shape), new Intersection(6, shape));
        var comps = xs.get(0).prepareComputations(r, xs);
        var c = w.refractedColor(comps, 0);
        assertEquals(Color.BLACK, c);
    }

    @Test
    void theRefractedColorUnderTotalInternalReflection() {
        var w = World.defaultWorld();
        var wo = w.objects()[0];
        var m = new Material.Builder().from(wo.getMaterial()).transparency(1.0).refractiveIndex(1.5).build();
        var shape = new Sphere(m);
        var r = new Ray(Tuple.point(0, 0, Math.sqrt(2) / 2), Tuple.vector(0, 1, 0));
        var xs = new Intersections(new Intersection(-Math.sqrt(2) / 2, shape), new Intersection(Math.sqrt(2) / 2, shape));
        // NOTE: this time you're inside the sphere, so you need
        // to look at the second intersection, xs[1], not xs[0]
        var comps = xs.get(1).prepareComputations(r, xs);
        var c = w.refractedColor(comps, 5);
        assertEquals(Color.BLACK, c);
    }

    @Test
    void theRefractedColorWithARefractedRay() {
        var dw = World.defaultWorld();
        var wo1 = dw.objects()[0];
        var m1 = new Material.Builder().from(wo1.getMaterial()).ambient(1.0).pattern(new TestPattern()).build();
        var A = new Sphere(m1);
        var wo2 = dw.objects()[1];
        var m2 = new Material.Builder().from(wo2.getMaterial()).transparency(1.0).refractiveIndex(1.5).build();
        var B = new Sphere(m2);
        var w = new World(new Shape[]{A, B});
        var r = new Ray(Tuple.point(0, 0, 0.1), Tuple.vector(0, 1, 0));
        var xs = new Intersections(new Intersection(-0.9899, A), new Intersection(-0.4899, B), new Intersection(0.4899, B), new Intersection(0.9899, A));
        var comps = xs.get(2).prepareComputations(r, xs);
        var c = w.refractedColor(comps, 5);
        assertEquals(new Color(0, 0.99888, 0.04725), c);
    }

    @Test
    void shadeHitWithATransparentMaterial() {
        var dw = World.defaultWorld();
        var floor = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().transparency(0.5).refractiveIndex(1.5).build());
        var ball = new Sphere(Matrix4x4.translation(0, -3.5, -0.5), new Material.Builder().color(1, 0, 0).ambient(0.5).build());
        var wo1 = dw.objects()[0];
        var wo2 = dw.objects()[1];
        var w = new World(new Shape[]{wo1, wo2, floor, ball});
        var r = new Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -Math.sqrt(2) / 2, Math.sqrt(2) / 2));
        var xs = new Intersections(new Intersection(Math.sqrt(2), floor));
        var comps = xs.get(0).prepareComputations(r, xs);
        var c = w.shadeHit(comps, 5);
        assertEquals(new Color(0.93642, 0.68642, 0.68642), c);
    }

    @Test
    void shadeHitWithAReflectiveTransparentMaterial() {
        var dw = World.defaultWorld();
        var floor = new Plane(Matrix4x4.translation(0, -1, 0), new Material.Builder().reflective(0.5).transparency(0.5).refractiveIndex(1.5).build());
        var ball = new Sphere(Matrix4x4.translation(0, -3.5, -0.5), new Material.Builder().color(1, 0, 0).ambient(0.5).build());
        var wo1 = dw.objects()[0];
        var wo2 = dw.objects()[1];
        var w = new World(new Shape[]{wo1, wo2, floor, ball});
        var r = new Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -Math.sqrt(2) / 2, Math.sqrt(2) / 2));
        var xs = new Intersections(new Intersection(Math.sqrt(2), floor));
        var comps = xs.get(0).prepareComputations(r, xs);
        var c = w.shadeHit(comps, 5);
        assertEquals(new Color(0.93391, 0.69643, 0.69243), c);
    }
}