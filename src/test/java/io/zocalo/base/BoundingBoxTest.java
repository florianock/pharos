package io.zocalo.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.zocalo.Utils.QTR_PI;
import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {

    @Test
    void creatingAnEmptyBoundingBox() {
        var box = new BoundingBox();
        assertEquals(Tuple.point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), box.min());
        assertEquals(Tuple.point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), box.max());
    }

    @Test
    void creatingABoundingBoxWithVolume() {
        var box = new BoundingBox(Tuple.point(-1, -2, -3), Tuple.point(3, 2, 1));
        assertEquals(Tuple.point(-1, -2, -3), box.min());
        assertEquals(Tuple.point(3, 2, 1), box.max());
    }

    @Test
    void addingPointsToAnEmptyBoundingBox() {
        var box = new BoundingBox();
        var p1 = Tuple.point(-5, 2, 0);
        var p2 = Tuple.point(7, 0, -3);
        var box2 = box.add(p1);
        var box3 = box2.add(p2);
        assertEquals(Tuple.point(-5, 0, -3), box3.min());
        assertEquals(Tuple.point(7, 2, 0), box3.max());
    }

    @Test
    void addingOneBoundingBoxToAnother() {
        var box1 = new BoundingBox(Tuple.point(-5, -2, 0), Tuple.point(7, 4, 4));
        var box2 = new BoundingBox(Tuple.point(8, -7, -2), Tuple.point(14, 2, 8));
        var box3 = box1.add(box2);
        assertEquals(Tuple.point(-5, -7, -2), box3.min());
        assertEquals(Tuple.point(14, 4, 8), box3.max());
    }

    public static Stream<Arguments> containsPointExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(5, -2, 0), true),
                Arguments.of(Tuple.point(11, 4, 7), true),
                Arguments.of(Tuple.point(8, 1, 3), true),
                Arguments.of(Tuple.point(3, 0, 3), false),
                Arguments.of(Tuple.point(8, -4, 3), false),
                Arguments.of(Tuple.point(8, 1, -1), false),
                Arguments.of(Tuple.point(13, 1, 3), false),
                Arguments.of(Tuple.point(8, 5, 3), false),
                Arguments.of(Tuple.point(8, 1, 8), false)
        );
    }

    @ParameterizedTest
    @MethodSource("containsPointExamples")
    void checkingToSeeIfABoxContainsAGivenPoint(Tuple point, boolean result) {
        var box = new BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7));
        assertEquals(result, box.contains(point));
    }

    public static Stream<Arguments> containsBoxExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7), true),
                Arguments.of(Tuple.point(6, -1, 1), Tuple.point(10, 3, 6), true),
                Arguments.of(Tuple.point(4, -3, -1), Tuple.point(10, 3, 6), false),
                Arguments.of(Tuple.point(6, -1, 1), Tuple.point(12, 5, 8), false)
        );
    }

    @ParameterizedTest
    @MethodSource("containsBoxExamples")
    void checkingToSeeIfABoxContainsAGivenBox(Tuple min, Tuple max, boolean result) {
        var box = new BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7));
        var box2 = new BoundingBox(min, max);
        assertEquals(result, box.contains(box2));
    }

    @Test
    void transformingABoundingBox() {
        var box = new BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1));
        var matrix = Transform.rotationOnX(QTR_PI).multiply(Transform.rotationOnY(QTR_PI));
        var box2 = box.transform(matrix);
        assertEquals(Tuple.point(-1.4142, -1.7071, -1.7071), box2.min());
        assertEquals(Tuple.point(1.4142, 1.7071, 1.7071), box2.max());
    }

    public static Stream<Arguments> intersectingExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(5, 0.5, 0), Tuple.vector(-1, 0, 0), true),
                Arguments.of(Tuple.point(-5, 0.5, 0), Tuple.vector(1, 0, 0), true),
                Arguments.of(Tuple.point(0.5, 5, 0), Tuple.vector(0, -1, 0), true),
                Arguments.of(Tuple.point(0.5, -5, 0), Tuple.vector(0, 1, 0), true),
                Arguments.of(Tuple.point(0.5, 0, 5), Tuple.vector(0, 0, -1), true),
                Arguments.of(Tuple.point(0.5, 0, -5), Tuple.vector(0, 0, 1), true),
                Arguments.of(Tuple.point(0, 0.5, 0), Tuple.vector(0, 0, 1), true),
                Arguments.of(Tuple.point(-2, 0, 0), Tuple.vector(2, 4, 6), false),
                Arguments.of(Tuple.point(0, -2, 0), Tuple.vector(6, 2, 4), false),
                Arguments.of(Tuple.point(0, 0, -2), Tuple.vector(4, 6, 2), false),
                Arguments.of(Tuple.point(2, 0, 2), Tuple.vector(0, 0, -1), false),
                Arguments.of(Tuple.point(0, 2, 2), Tuple.vector(0, -1, 0), false),
                Arguments.of(Tuple.point(2, 2, 0), Tuple.vector(-1, 0, 0), false)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingExamples")
    void intersectingARayWithABoundingBoxAtTheOrigin(Tuple origin, Tuple direction, boolean result) {
        var box = new BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1));
        var r = new Ray(origin, direction.normalize());
        assertEquals(result, box.intersects(r));
    }

    public static Stream<Arguments> intersectingNonCubicExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(15, 1, 2), Tuple.vector(-1, 0, 0), true),
                Arguments.of(Tuple.point(-5, -1, 4), Tuple.vector(1, 0, 0), true),
                Arguments.of(Tuple.point(7, 6, 5), Tuple.vector(0, -1, 0), true),
                Arguments.of(Tuple.point(9, -5, 6), Tuple.vector(0, 1, 0), true),
                Arguments.of(Tuple.point(8, 2, 12), Tuple.vector(0, 0, -1), true),
                Arguments.of(Tuple.point(6, 0, -5), Tuple.vector(0, 0, 1), true),
                Arguments.of(Tuple.point(8, 1, 3.5), Tuple.vector(0, 0, 1), true),
                Arguments.of(Tuple.point(9, -1, -8), Tuple.vector(2, 4, 6), false),
                Arguments.of(Tuple.point(8, 3, -4), Tuple.vector(6, 2, 4), false),
                Arguments.of(Tuple.point(9, -1, -2), Tuple.vector(4, 6, 2), false),
                Arguments.of(Tuple.point(4, 0, 9), Tuple.vector(0, 0, -1), false),
                Arguments.of(Tuple.point(8, 6, -1), Tuple.vector(0, -1, 0), false),
                Arguments.of(Tuple.point(12, 5, 4), Tuple.vector(-1, 0, 0), false)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingNonCubicExamples")
    void intersectingARayWithANonCubicBoundingBox(Tuple origin, Tuple direction, boolean result) {
        var box = new BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7));
        var r = new Ray(origin, direction.normalize());
        assertEquals(result, box.intersects(r));
    }
}