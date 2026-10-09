package io.zocalo;

import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import org.junit.jupiter.api.Test;

import static io.zocalo.TestUtils.assertEqualEnough;
import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

    @Test
    void testPoint() {
        var a = Tuple.point(4.3, -4.2, 3.1);

        assertEquals(new Tuple(4.3, -4.2, 3.1, 1), a);
        assertTrue(a.isPoint());
        assertFalse(a.isVector());
    }

    @Test
    void testVector() {
        var a = Tuple.vector(4.3, -4.2, 3.1);

        assertEquals(new Tuple(4.3, -4.2, 3.1, 0), a);
        assertFalse(a.isPoint());
        assertTrue(a.isVector());
    }

    @Test
    void addTuples() {
        var a1 = Tuple.point(3, -2, 5);
        var a2 = Tuple.vector(-2, 3, 1);

        assertEquals(new Tuple(1, 1, 6, 1), a1.add(a2));
    }

    @Test
    void subtractTuples() {
        var a1 = Tuple.point(3, -2, 5);
        var a2 = Tuple.vector(-2, 3, 1);

        assertEquals(new Tuple(5, -5, 4, 1), a1.subtract(a2));
    }

    @Test
    void multiplyTupleByScalar() {
        var a = new Tuple(1, -2, 3, -4);

        assertEquals(new Tuple(3.5, -7, 10.5, -14), a.multiply(3.5));
    }

    @Test
    void multiplyTupleByFraction() {
        var a = new Tuple(1, -2, 3, -4);

        assertEquals(new Tuple(0.5, -1, 1.5, -2), a.multiply(0.5));
    }

    @Test
    void divideTupleByScalar() {
        var a = new Tuple(1, -2, 3, -4);

        assertEquals(new Tuple(0.5, -1, 1.5, -2), a.divide(2));
    }

    @Test
    void negateTuple() {
        var a = Tuple.point(3, -2, 5);

        assertEquals(new Tuple(-3, 2, -5, -1), a.negate());
    }

    @Test
    void magnitudeVectorUnitX() {
        var a = Tuple.vector(1, 0, 0);

        assertEqualEnough(1, a.magnitude());
    }
    @Test
    void magnitudeVectorUnitY() {
        var a = Tuple.vector(0, 1, 0);

        assertEqualEnough(1, a.magnitude());
    }
    @Test
    void magnitudeVectorUnitZ() {
        var a = Tuple.vector(0, 0, 1);

        assertEqualEnough(1, a.magnitude());
    }

    @Test
    void magnitudePositiveVector() {
        var a = Tuple.vector(1, 2, 3);

        assertEqualEnough(Math.sqrt(14), a.magnitude());
    }

    @Test
    void magnitudeNegativeVector() {
        var a = Tuple.vector(-1, -2, -3);

        assertEqualEnough(Math.sqrt(14), a.magnitude());
    }

    @Test
    void normalizeVector() {
        var a = Tuple.vector(4, 0, 0);

        assertEquals(Tuple.vector(1, 0, 0), a.normalize());
    }

    @Test
    void normalizeVector2() {
        var a = Tuple.vector(1, 2, 3);

        assertEquals(Tuple.vector(1/Math.sqrt(14), 2/Math.sqrt(14), 3/Math.sqrt(14)), a.normalize());
        assertEquals(Tuple.vector(0.26726, 0.53452, 0.80178), a.normalize());
    }

    @Test
    void magnitudeOfNormalizedVector() {
        var a = Tuple.vector(1, 2, 3);

        assertEqualEnough(1, a.normalize().magnitude());
    }

    @Test
    void dotProductOfTwoVectors() {
        var a = Tuple.vector(1, 2, 3);
        var b = Tuple.vector(2, 3, 4);

        assertEqualEnough(20, a.dot(b));
        assertEqualEnough(20, b.dot(a));
    }

    @Test
    void crossProductOfTwoVectors() {
        var a = Tuple.vector(1, 2, 3);
        var b = Tuple.vector(2, 3, 4);

        assertEquals(Tuple.vector(-1, 2, -1), a.cross(b));
        assertEquals(Tuple.vector(1, -2, 1), b.cross(a));
    }

    @Test
    void reflecting_vector_approaching_at_45_degrees() {
        var v = Tuple.vector(1, -1, 0);
        var n = Tuple.vector(0, 1, 0);
        var r = v.reflect(n);

        assertEquals(Tuple.vector(1, 1, 0), r);
    }

    @Test
    void reflecting_vector_off_slanted_surface() {
        var v = Tuple.vector(0, -1, 0);
        var n = Tuple.vector(Math.sqrt(2) /2, Math.sqrt(2) /2, 0);
        var r = v.reflect(n);

        assertEquals(Tuple.vector(1, 0, 0), r);
    }

    // TRANSFORMATIONS

    @Test
    void the_transformation_matrix_for_the_default_orientation() {
        var from = Tuple.point(0, 0, 0);
        var to = Tuple.point(0, 0, -1);
        var up = Tuple.vector(0, 1, 0);
        var t = from.viewTransform(to, up);
        assertEquals(Matrix4x4.identity(), t);
    }

    @Test
    void a_view_transformation_matrix_looking_in_positive_z_direction() {
        var from = Tuple.point(0, 0, 0);
        var to = Tuple.point(0, 0, 1);
        var up = Tuple.vector(0, 1, 0);
        var t = from.viewTransform(to, up);
        assertEquals(Matrix4x4.scaling(-1, 1, -1), t);
    }

    @Test
    void the_view_transformation_moves_the_world() {
        var from = Tuple.point(0, 0, 8);
        var to = Tuple.point(0, 0, 0);
        var up = Tuple.vector(0, 1, 0);
        var t = from.viewTransform(to, up);
        assertEquals(Matrix4x4.translation(0, 0, -8), t);
    }

    @Test
    void an_arbitrary_view_transformation() {
        var from = Tuple.point(1, 3, 2);
        var to = Tuple.point(4, -2, 8);
        var up = Tuple.vector(1, 1, 0);
        var t = from.viewTransform(to, up);
        var expected = new Matrix4x4(new double[] {
                -0.50709, 0.50709,  0.67612, -2.36642,
                 0.76772, 0.60609,  0.12122, -2.82843,
                -0.35857, 0.59761, -0.71714,  0.00000,
                 0.00000, 0.00000,  0.00000,  1.00000
        });
        assertEquals(expected, t);
    }
}