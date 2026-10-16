package io.zocalo;

import io.zocalo.kernel.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Sphere;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.zocalo.TestUtils.assertEqualEnough;
import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    @Test
    void intersection_encapsulates_t_and_object() {
        var s = new Sphere();
        var i = new Intersection(3.5, s);

        assertEquals(3.5, i.t());
        assertEquals(s, i.object());
    }

    @Test
    void aggregating_intersections() {
        var s = new Sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);

        var xs = new Intersections(i1, i2);

        assertEquals(2, xs.count());
        assertEquals(s, xs.get(0).object());
        assertEquals(s, xs.get(1).object());
    }

    @Test
    void hit_when_all_intersections_have_positive_t() {
        var s = new Sphere();
        var i1 = new Intersection(1, s);
        var i2 = new Intersection(2, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i1, i.get());
    }

    @Test
    void hit_when_some_intersections_have_negative_t() {
        var s = new Sphere();
        var i1 = new Intersection(-1, s);
        var i2 = new Intersection(1, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isPresent());
        assertEquals(i2, i.get());
    }

    @Test
    void hit_when_all_intersections_have_negative_t() {
        var s = new Sphere();
        var i1 = new Intersection(-2, s);
        var i2 = new Intersection(-1, s);
        var xs = new Intersections(i2, i1);

        var i = xs.hit();

        assertTrue(i.isEmpty());
    }

    @Test
    void hit_is_always_the_lowest_non_negative_intersection() {
        var s = new Sphere();
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
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = new Sphere();
        var i = new Intersection(4, shape);

        var comps = i.prepareComputations(r, new Intersections(i));

        assertEquals(i.t(), comps.t());
        assertEquals(i.object(), comps.object());
        assertEquals(Tuple.point(0, 0, -1), comps.point());
        assertEquals(Tuple.vector(0, 0, -1), comps.eyev());
        assertEquals(Tuple.vector(0, 0, -1), comps.normalv());
    }

    @Test
    void the_hit_when_intersection_occurs_on_the_outside() {
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = new Sphere();
        var i = new Intersection(4, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        assertFalse(comps.inside());
    }

    @Test
    void the_hit_when_intersection_occurs_on_the_inside() {
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var shape = new Sphere();
        var i = new Intersection(1, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        assertEquals(Tuple.point(0, 0, 1), comps.point());
        assertEquals(Tuple.vector(0, 0, -1), comps.eyev());
        assertTrue(comps.inside());
        // normal would have been (0, 0, 1), but is inverted!
        assertEquals(Tuple.vector(0, 0, -1), comps.normalv());
    }

    @Test
    void the_hit_should_offset_the_point() {
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = new Sphere(Matrix4x4.translation(0, 0, 1));
        var i = new Intersection(5, shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        assertTrue(comps.overPoint().z() < -Utils.EPSILON / 2);
        assertTrue(comps.point().z() > comps.overPoint().z());
    }

    @Test
    void precomputingTheReflectionVector() {
        var shape = new Plane();
        var r = new Ray(Tuple.point(0, 1, -1), Tuple.vector(0, -Math.sqrt(2) / 2, Math.sqrt(2) / 2));
        var i = new Intersection(Math.sqrt(2), shape);
        var comps = i.prepareComputations(r, new Intersections(i));
        assertEquals(Tuple.vector(0, Math.sqrt(2) / 2, Math.sqrt(2) / 2), comps.reflectv());
    }

    public static Stream<Arguments> intersectionRefractionExamples() {
        return Stream.of(
                Arguments.of(0, 1.0, 1.5),
                Arguments.of(1, 1.5, 2.0),
                Arguments.of(2, 2.0, 2.5),
                Arguments.of(3, 2.5, 2.5),
                Arguments.of(4, 2.5, 1.5),
                Arguments.of(5, 1.5, 1.0)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectionRefractionExamples")
    void findingN1AndN2AtVariousIntersections(int index, double n1, double n2) {
        var A = Sphere.glassSphere(Matrix4x4.scaling(2, 2, 2), 1.5);
        var B = Sphere.glassSphere(Matrix4x4.translation(0, 0, -0.25), 2.0);
        var C = Sphere.glassSphere(Matrix4x4.translation(0, 0, 0.25), 2.5);

        var r = new Ray(Tuple.point(0, 0, -4), Tuple.vector(0, 0, 1));
        var xs = new Intersections(new Intersection(2, A), new Intersection(2.75, B), new Intersection(3.25, C),
                new Intersection(4.75, B), new Intersection(5.25, C), new Intersection(6, A));
        var comps = xs.get(index).prepareComputations(r, xs);
        assertEquals(n1, comps.n1());
        assertEquals(n2, comps.n2());
    }

    @Test
    void theUnderPointIsOffsetBelowTheSurface() {
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var shape = Sphere.glassSphere(Matrix4x4.translation(0, 0, 1));
        var i = new Intersection(5, shape);
        var xs = new Intersections(i);
        var comps = i.prepareComputations(r, xs);
        assertTrue(comps.underPoint().z() > Utils.EPSILON / 2);
        assertTrue(comps.point().z() < comps.underPoint().z());
    }

    @Test
    void theSchlickApproximationUnderTotalInternalReflection() {
        var shape = Sphere.glassSphere();
        var r = new Ray(Tuple.point(0, 0, -Math.sqrt(2) / 2), Tuple.vector(0, 1, 0));
        var xs = new Intersections(new Intersection(-Math.sqrt(2) / 2, shape), new Intersection(Math.sqrt(2) / 2, shape));
        var comps = xs.get(1).prepareComputations(r, xs);
        var reflectance = comps.schlick();
        assertEquals(1.0, reflectance);
    }

    @Test
    void theSchlickApproximationWithAPerpendicularViewingAngle() {
        var shape = Sphere.glassSphere();
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 1, 0));
        var xs = new Intersections(new Intersection(-1, shape), new Intersection(1, shape));
        var comps = xs.get(1).prepareComputations(r, xs);
        var reflectance = comps.schlick();
        assertEqualEnough(0.04, reflectance);
    }

    @Test
    void theSchlickApproximationWithSmallAngleAndN2GreaterThanN1() {
        var shape = Sphere.glassSphere();
        var r = new Ray(Tuple.point(0, 0.99, -2), Tuple.vector(0, 0, 1));
        var xs = new Intersections(new Intersection(1.8589, shape));
        var comps = xs.get(0).prepareComputations(r, xs);
        var reflectance = comps.schlick();
        assertEqualEnough(0.48873, reflectance);
    }
}