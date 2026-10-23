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

class ConeTest {

    public static Stream<Arguments> intersectingExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1), 5, 5),
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(1, 1, 1), 8.66025, 8.66025),
                Arguments.of(Tuple.point(1, 1, -5), Tuple.vector(-0.5, -1, 1), 4.55006, 49.44994)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingExamples")
    void intersectingAConeWitARay(Tuple origin, Tuple direction, double t0, double t1) {
        var shape = new Cone();
        var r = new Ray(origin, direction.normalize());
        var xs = shape.intersect(r);
        assertEquals(2, xs.count());
        assertEquals(t0, xs.get(0).t(), EPSILON);
        assertEquals(t1, xs.get(1).t(), EPSILON);
    }

    @Test
    void intersectingAConeWithARayParallelToOneOfItsHalves() {
        var shape = new Cone();
        var direction = Tuple.vector(0, 1, 1).normalize();
        var r = new Ray(Tuple.point(0, 0, -1), direction);
        var xs = shape.intersect(r);
        assertEquals(1, xs.count());
        assertEquals(0.35355, xs.get(0).t(), EPSILON);
    }

    public static Stream<Arguments> intersectingEndCapsExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 0, -5), Tuple.vector(0, 1, 0), 0),
                Arguments.of(Tuple.point(0, 0, -0.25), Tuple.vector(0, 1, 1), 2),
                Arguments.of(Tuple.point(0, 0, -0.25), Tuple.vector(0, 1, 0), 4)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectingEndCapsExamples")
    void intersectingAConesEndCaps(Tuple origin, Tuple direction, double count) {
        var shape = new Cone(-0.5, 0.5, true);
        var r = new Ray(origin, direction.normalize());
        var xs = shape.intersect(r);
        assertEquals(count, xs.count());
    }


    public static Stream<Arguments> normalVectorExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 0)),
                Arguments.of(Tuple.point(1, 1, 1), Tuple.vector(1, -Math.sqrt(2), 1)),
                Arguments.of(Tuple.point(-1, -1, 0), Tuple.vector(-1, 1, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("normalVectorExamples")
    void computingTheNormalVectorOnACone(Tuple point, Tuple normal) {
        var shape = new Cone();
        var n = shape.localNormalAt(point);
        assertEquals(normal, n);
    }
}