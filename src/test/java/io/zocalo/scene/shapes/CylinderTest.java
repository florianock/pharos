package io.zocalo.scene.shapes;

import io.zocalo.base.Ray;
import io.zocalo.base.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.zocalo.Utils.EPSILON;
import static org.junit.jupiter.api.Assertions.*;

class CylinderTest {

    public static Stream<Arguments> missesExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(1, 0, 0), Tuple.vector(0, 1, 0)),
                Arguments.of(Tuple.point(0, 0, 0), Tuple.vector(0, 1, 0)),
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(1, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("missesExamples")
    void aRayMissesACylinder(Tuple origin, Tuple dir) {
        var cyl = new Cylinder();
        var direction = dir.normalize();
        var r = new Ray(origin, direction);
        var xs = cyl.intersect(r);
        assertEquals(0, xs.count());
    }

    public static Stream<Arguments> hitsExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(1, 0, -5), Tuple.vector(0, 0, 1), 5, 5),
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1), 4, 6),
                Arguments.of(Tuple.point(0.5, 0, -5), Tuple.vector(0.1, 1, 1), 6.80798, 7.08872)
        );
    }

    @ParameterizedTest
    @MethodSource("hitsExamples")
    void aRayHitsACylinder(Tuple origin, Tuple dir, double t0, double t1) {
        var cyl = new Cylinder();
        var direction = dir.normalize();
        var r = new Ray(origin, direction);
        var xs = cyl.intersect(r);
        assertEquals(2, xs.count());
        assertEquals(t0, xs.get(0).t(), EPSILON);
        assertEquals(t1, xs.get(1).t(), EPSILON);
    }

    public static Stream<Arguments> normalExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(1, 0, 0), Tuple.vector(1, 0, 0)),
                Arguments.of(Tuple.point(0, 5, -1), Tuple.vector(0, 0, -1)),
                Arguments.of(Tuple.point(0, -2, 1), Tuple.vector(0, 0, 1)),
                Arguments.of(Tuple.point(-1, 1, 0), Tuple.vector(-1, 0, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("normalExamples")
    void normalVectorOnACylinder(Tuple point, Tuple normal) {
        var cyl = new Cylinder();
        var n = cyl.localNormalAt(point);
        assertEquals(normal, n);
    }

    @Test
    void theDefaultMinimumAndMaximumForACylinder() {
        var cyl =  new Cylinder();
        assertEquals(Double.NEGATIVE_INFINITY, cyl.minimum());
        assertEquals(Double.POSITIVE_INFINITY, cyl.maximum());
    }

    public static Stream<Arguments> intersectingConstrainedCylinderExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 1.5, 0), Tuple.vector(0.1, 1, 0), 0),
                Arguments.of(Tuple.point(0, 3, -5), Tuple.vector(0, 0, 1), 0),
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1), 0),
                Arguments.of(Tuple.point(0, 2, -5), Tuple.vector(0, 0, 1), 0),
                Arguments.of(Tuple.point(0, 1, -5), Tuple.vector(0, 0, 1), 0),
                Arguments.of(Tuple.point(0, 1.5, -2), Tuple.vector(0, 0, 1), 2)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingConstrainedCylinderExamples")
    void intersectingAConstrainedCylinder(Tuple point, Tuple direction, int count) {
        var cyl = new Cylinder(1, 2);
        var r = new Ray(point, direction.normalize());
        var xs = cyl.intersect(r);
        assertEquals(count, xs.count());
    }

    @Test
    void theDefaultClosedValueForACylinder() {
        var cyl = new Cylinder();
        assertFalse(cyl.closed());
    }

    public static Stream<Arguments> intersectingCylinderEndCapsExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 3, 0), Tuple.vector(0, -1, 0), 2),
                Arguments.of(Tuple.point(0, 3, -2), Tuple.vector(0, -1, 2), 2),
                Arguments.of(Tuple.point(0, 4, -2), Tuple.vector(0, -1, 1), 2),
                Arguments.of(Tuple.point(0, 0, -2), Tuple.vector(0, 1, 2), 2),
                Arguments.of(Tuple.point(0, -1, -2), Tuple.vector(0, 1, 1), 2)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingCylinderEndCapsExamples")
    void intersectingACylindersEndCaps(Tuple point, Tuple direction, int count) {
        var cyl = new Cylinder(1, 2, true);
        var r = new Ray(point, direction.normalize());
        var xs = cyl.intersect(r);
        assertEquals(count, xs.count());
    }

    public static Stream<Arguments> normalVectorOnACylindersEndCapsExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 1, 0), Tuple.vector(0, -1, 0)),
                Arguments.of(Tuple.point(0.5, 1, 0), Tuple.vector(0, -1, 0)),
                Arguments.of(Tuple.point(0, 1, 0.5), Tuple.vector(0, -1, 0)),
                Arguments.of(Tuple.point(0, 2, 0), Tuple.vector(0, 1, 0)),
                Arguments.of(Tuple.point(0.5, 2, 0), Tuple.vector(0, 1, 0)),
                Arguments.of(Tuple.point(0, 2, 0.5), Tuple.vector(0, 1, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("normalVectorOnACylindersEndCapsExamples")
    void theNormalVectorOnACylindersEndCaps(Tuple point, Tuple normal) {
        var cyl = new Cylinder(1, 2, true);
        var n = cyl.localNormalAt(point);
        assertEquals(normal, n);
    }

    @Test
    void aCylinderHasABoundingBox() {
        var shape = new Cylinder();
        var box = shape.bounds();
        assertEquals(Tuple.point(-1, Double.NEGATIVE_INFINITY, -1), box.min());
        assertEquals(Tuple.point(1, Double.POSITIVE_INFINITY, 1), box.max());
    }

    @Test
    void aBoundedCylinderHasABoundingBox() {
        var shape = new Cylinder(-5, 3);
        var box = shape.bounds();
        assertEquals(Tuple.point(-1, -5, -1), box.min());
        assertEquals(Tuple.point(1, 3, 1), box.max());
    }
}