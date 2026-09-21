package io.zocalo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

    @Test
    void testPoint() {
        var a = Tuple.point(4.3f, -4.2f, 3.1f);

        assertEquals(new Tuple(4.3f, -4.2f, 3.1f, 1), a);
        assertTrue(a.isPoint());
        assertFalse(a.isVector());
    }

    @Test
    void testVector() {
        var a = Tuple.vector(4.3f, -4.2f, 3.1f);

        assertEquals(new Tuple(4.3f, -4.2f, 3.1f, 0), a);
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

        assertEquals(new Tuple(3.5f, -7, 10.5f, -14), a.multiply(3.5f));
    }

    @Test
    void multiplyTupleByFraction() {
        var a = new Tuple(1, -2, 3, -4);

        assertEquals(new Tuple(0.5f, -1, 1.5f, -2), a.multiply(0.5f));
    }

    @Test
    void divideTupleByScalar() {
        var a = new Tuple(1, -2, 3, -4);

        assertEquals(new Tuple(0.5f, -1, 1.5f, -2), a.divide(2));
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

        assertEqualEnough((float) Math.sqrt(14), a.magnitude());
    }

    @Test
    void magnitudeNegativeVector() {
        var a = Tuple.vector(-1, -2, -3);

        assertEqualEnough((float) Math.sqrt(14), a.magnitude());
    }

    @Test
    void normalizeVector() {
        var a = Tuple.vector(4, 0, 0);

        assertEquals(Tuple.vector(1, 0, 0), a.normalize());
    }

    @Test
    void normalizeVector2() {
        var a = Tuple.vector(1, 2, 3);

        assertEquals(Tuple.vector((float) (1/Math.sqrt(14)), (float) (2/Math.sqrt(14)), (float) (3/Math.sqrt(14))), a.normalize());
        assertEquals(Tuple.vector(0.26726f, 0.53452f, 0.80178f), a.normalize());
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
        var n = Tuple.vector((float) Math.sqrt(2)/2, (float) Math.sqrt(2)/2, 0);
        var r = v.reflect(n);

        assertEquals(Tuple.vector(1, 0, 0), r);
    }

    private static void assertEqualEnough(float a, float b) {
        assertTrue(Utils.equalEnough(a, b));
    }
}