package io.zocalo.scene.shapes;

import io.zocalo.base.Ray;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CubeTest {

    @Test
    void defaultCube() {
        var c = new Cube();
        assertEquals(Transform.identity(), c.getInverseTransform());
    }

    public static Stream<Arguments> intersectExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(5, 0.5, 0), Tuple.vector(-1, 0, 0), 4, 6),
                Arguments.of(Tuple.point(-5, 0.5, 0), Tuple.vector(1, 0, 0), 4, 6),
                Arguments.of(Tuple.point(0.5, 5, 0), Tuple.vector(0, -1, 0), 4, 6),
                Arguments.of(Tuple.point(0.5, -5, 0), Tuple.vector(0, 1, 0), 4, 6),
                Arguments.of(Tuple.point(0.5, 0, 5), Tuple.vector(0, 0, -1), 4, 6),
                Arguments.of(Tuple.point(0.5, 0, -5), Tuple.vector(0, 0, 1), 4, 6),
                Arguments.of(Tuple.point(0, 0.5, 0), Tuple.vector(0, 0, 1), -1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectExamples")
    void aRayIntersectsACube(Tuple origin, Tuple direction, int t1, int t2) {
        var c = new Cube();
        var r = new Ray(origin, direction);
        var xs = c.intersect(r);
        assertEquals(2, xs.count());
        assertEquals(xs.get(0).t(), t1);
        assertEquals(xs.get(1).t(), t2);
    }

    public static Stream<Arguments> missesExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(-2, 0, 0), Tuple.vector(0.2673, 0.5345, 0.8018)),
                Arguments.of(Tuple.point(0, -2, 0), Tuple.vector(0.8018, 0.2673, 0.5345)),
                Arguments.of(Tuple.point(0, 0, -2), Tuple.vector(0.5345, 0.8018, 0.2673)),
                Arguments.of(Tuple.point(2, 0, 2), Tuple.vector(0, 0, -1)),
                Arguments.of(Tuple.point(0, 2, 2), Tuple.vector(0, -1, 0)),
                Arguments.of(Tuple.point(2, 2, 0), Tuple.vector(-1, 0, 0)),
                Arguments.of(Tuple.point(0, 0, 2), Tuple.vector(0, 0, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("missesExamples")
    void aRayMissesACube(Tuple origin, Tuple direction) {
        var c = new Cube();
        var r = new Ray(origin, direction);
        var xs = c.intersect(r);
        assertEquals(0, xs.count());
    }

    public static Stream<Arguments> normalExamples() {
        return Stream.of(
                Arguments.of(Tuple.point(1, 0.5, -0.8), Tuple.vector(1, 0, 0)),
                Arguments.of(Tuple.point(-1, -0.2, 0.9), Tuple.vector(-1, 0, 0)),
                Arguments.of(Tuple.point(-0.4, 1, -0.1), Tuple.vector(0, 1, 0)),
                Arguments.of(Tuple.point(0.3, -1, -0.7), Tuple.vector(0, -1, 0)),
                Arguments.of(Tuple.point(-0.6, 0.3, 1), Tuple.vector(0, 0, 1)),
                Arguments.of(Tuple.point(0.4, 0.4, -1), Tuple.vector(0, 0, -1)),
                Arguments.of(Tuple.point(1, 1, 1), Tuple.vector(1, 0, 0)),
                Arguments.of(Tuple.point(-1, -1, -1), Tuple.vector(-1, 0, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("normalExamples")
    void theNormalOnTheSurfaceOfACube(Tuple point, Tuple expectedNormal) {
        var c = new Cube();
        var normal = c.localNormalAt(point);
        assertEquals(expectedNormal, normal);
    }
}