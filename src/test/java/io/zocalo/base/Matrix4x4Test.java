package io.zocalo.base;

import io.zocalo.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4x4Test {

    @Test
    void construct4x4Matrix_throwsIfNot16values() {
        assertThrows(AssertionError.class, () -> new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 5.5, 6.5, 7.5, 8.5 },
                { 9, 10, 11, 12 },
                { 13.5, 14.5, 15.5 }
        }));
        assertThrows(AssertionError.class, () -> new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 5.5, 6.5, 7.5, 8.5 },
                { 9, 10, 11, 12 },
                { 13.5, 14.5, 15.5, 16.5, 17.5 }
        }));
        assertThrows(AssertionError.class, () -> new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 5.5, 6.5, 7.5, 8.5 },
                { 9, 10, 11, 12 },
                { 13.5, 14.5, 15.5, 16.5 },
                { 1, 2, 5, 6 }
        }));
        assertThrows(AssertionError.class, () -> new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 9, 10, 11, 12 },
                { 13.5, 14.5, 15.5, 16.5, 17.5 }
        }));
    }

    @Test
    void getValues() {
        var m = new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 5.5, 6.5, 7.5, 8.5 },
                { 9, 10, 11, 12 },
                {13.5, 14.5, 15.5, 16.5 }
        });

        assertEquals(1, m.get(0, 0));
        assertEquals(4, m.get(0, 3));
        assertEquals(5.5, m.get(1, 0));
        assertEquals(7.5, m.get(1, 2));
        assertEquals(11, m.get(2, 2));
        assertEquals(13.5, m.get(3, 0));
        assertEquals(15.5, m.get(3, 2));
    }

    @Test
    void getValue_checksBounds() {
        var m = new Matrix4x4(new double[][] {
                { 1, 2, 3, 4 },
                { 5.5, 6.5, 7.5, 8.5 },
                { 9, 10, 11, 12 },
                { 13.5, 14.5, 15.5, 16.5 }
        });

        assertThrows(AssertionError.class, () -> m.get(4, 0));
        assertThrows(AssertionError.class, () -> m.get(0, 4));
        assertThrows(AssertionError.class, () -> m.get(-1, 0));
        assertThrows(AssertionError.class, () -> m.get(0, -1));
    }

    @Test
    void equality() {
        var a = new Matrix4x4(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2 });
        var b = new Matrix4x4(new double[] { 1.000001, 2.000001, 2.99999, 3.99999, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2 });

        assertEquals(a, b);
    }

    @Test
    void inequality() {
        var a = new Matrix4x4(new double[][] {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        var b = new Matrix4x4(new double[][] {
                {2, 3, 4, 5},
                {6, 7, 8, 9},
                {8, 7, 6, 5},
                {4, 3, 2, 1}
        });

        assertNotEquals(a, b);
    }

    @Test
    void multiplication_matrix() {
        var a = new Matrix4x4(new double[][] {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        var b = new Matrix4x4(new double[][] {
                {-2, 1, 2, 3},
                {3, 2, 1, -1},
                {4, 3, 6, 5},
                {1, 2, 7, 8}
        });

        var result = a.multiply(b);

        var expected = new Matrix4x4(new double[][]{
                {20, 22, 50, 48},
                {44, 54, 114, 108},
                {40, 58, 110, 102},
                {16, 26, 46, 42}
        });
        assertEquals(expected, result);
    }

    @Test
    void multiplication_tuple() {
        var a = new Matrix4x4(new double[][] {
                {1, 2, 3, 4},
                {2, 4, 4, 2},
                {8, 6, 4, 1},
                {0, 0, 0, 1}
        });
        var b = new Tuple(1, 2, 3, 1);

        var result = a.multiply(b);

        var expected = new Tuple(18, 24, 33, 1);
        assertEquals(expected, result);
    }

    @Test
    void multiplication_identityMatrix() {
        var a = new Matrix4x4(new double[][] {
                {0, 1, 2, 4},
                {1, 2, 4, 8},
                {2, 4, 8, 16},
                {4, 8, 16, 32}
        });

        assertEquals(a, a.multiply(Transform.identity()));
    }

    @Test
    void transpose() {
        var a = new Matrix4x4(new double[][] {
                { 0, 9, 3, 0 },
                { 9, 8, 0, 8 },
                { 1, 8, 5, 3 },
                { 0, 0, 5, 8 }
        });

        var result = a.transpose();

        var expected = new Matrix4x4(new double[][] {
                { 0, 9, 1, 0 },
                { 9, 8, 8, 0 },
                { 3, 0, 5, 5 },
                { 0, 8, 3, 8 }
        });
        assertEquals(expected, result);
    }

    @Test
    void transpose_identity() {
        assertEquals(Transform.identity(), Transform.identity().transpose());
    }

    @Test
    void subMatrix() {
        var a = new Matrix4x4(new double[][]{
                {-6, 1, 1, 6},
                {-8, 5, 8, 6},
                {-1, 0, 8, 2},
                {-7, 1, -1, 1}
        });

        var expected = new Matrix3x3(new double[][]{
                {-6, 1, 6},
                {-8, 8, 6},
                {-7, -1, 1}
        });

        assertEquals(expected, a.subMatrix(2, 1));
    }

    @Test
    void determinant() {
        var a = new Matrix4x4(new double[][]{
                {-2, -8, 3, 5},
                {-3, 1, 7, 3},
                {1, 2, -9, 6},
                {-6, 7, 7, -9}
        });

        assertEquals(690, a.cofactor(0, 0));
        assertEquals(447, a.cofactor(0, 1));
        assertEquals(210, a.cofactor(0, 2));
        assertEquals(51, a.cofactor(0, 3));
        assertEquals(-4071, a.determinant());
    }

    @Test
    void invertibleMatrix() {
        var a = new Matrix4x4(new double[][]{
                {6, 4, 4, 4},
                {5, 5, 7, 6},
                {4, -9, 3, -7},
                {9, 1, 7, -6}
        });

        assertEquals(-2120, a.determinant());
        assertTrue(a.isInvertible());
    }

    @Test
    void nonInvertibleMatrix() {
        var a = new Matrix4x4(new double[][]{
                {-4, 2, -2, -3},
                {9, 6, 2, 6},
                {0, -5, 1, -5},
                {0, 0, 0, 0}
        });

        assertEquals(0, a.determinant());
        assertFalse(a.isInvertible());
    }

    @Test
    void inverse1() {
        var a = new Matrix4x4(new double[][]{
                {-5, 2, 6, -8},
                {1, -5, 1, 8},
                {7, 7, -6, -7},
                {1, -3, 7, 4}
        });

        var b = a.invert();

        assertEquals(532, a.determinant());
        assertEquals(-160, a.cofactor(2, 3));
        assertEquals(-160d/532, b.get(3, 2));
        assertEquals(105, a.cofactor(3, 2));
        assertEquals(105d/532, b.get(2, 3));
        var expected = new Matrix4x4(new double[][]{
                {0.21805, 0.45113, 0.24060, -0.04511},
                {-0.80827, -1.45677, -0.44361, 0.52068},
                {-0.07895, -0.22368, -0.05263, 0.19737},
                {-0.52256, -0.81391, -0.30075, 0.30639}
        });
        assertEquals(expected, b);
    }

    @Test
    void inverse2() {
        var a = new Matrix4x4(new double[][]{
                {8, -5, 9, 2},
                {7, 5, 6, 1},
                {-6, 0, 9, 6},
                {-3, 0, -9, -4}
        });

        var expected = new Matrix4x4(new double[][]{
                {-0.15385, -0.15385, -0.28205, -0.53846},
                {-0.07692, 0.12308, 0.02564, 0.03077},
                {0.35897, 0.35897, 0.43590, 0.92308},
                {-0.69231, -0.69231, -0.76923, -1.92308}
        });
        assertEquals(expected, a.invert());
    }

    @Test
    void inverse3() {
        var a = new Matrix4x4(new double[][]{
                {9, 3, 0, 9},
                {-5, -2, -6, -3},
                {-4, 9, 6, 4},
                {-7, 6, 6, 2}
        });

        var expected = new Matrix4x4(new double[][]{
                {-0.04074, -0.07778, 0.14444, -0.22222},
                {-0.07778, 0.03333, 0.36667, -0.33333},
                {-0.02901, -0.14630, -0.10926, 0.12963},
                {0.17778, 0.06667, -0.26667, 0.33333}
        });
        assertEquals(expected, a.invert());
    }

    @Test
    void inverse_reversesMultiplication() {
        var a = new Matrix4x4(new double[][]{
                {3, -9, 7, 3},
                {3, -8, 2, -9},
                {-4, 4, 4, 1},
                {-6, 5, -1, 1}
        });
        var b = new Matrix4x4(new double[][]{
                {8, 2, 2, 2},
                {3, -1, 7, 0},
                {7, 0, 5, 4},
                {6, -2, 0, 5}
        });

        assertEquals(a, a.multiply(b).multiply(b.invert()));
    }

    @Test
    void miscellaneous_tests() {
        var a = new Matrix4x4(new double[][]{
                {3, -9, 7, 3},
                {3, -8, 2, -9},
                {-4, 4, 4, 1},
                {-6, 5, -1, 1}
        });
        assertEquals(Transform.identity(), Transform.identity().transpose());

        assertEquals(Transform.identity(), a.multiply(a.invert()));
        assertEquals(a.transpose().invert(), a.invert().transpose());

        var t = new Tuple(1, 2, 3, 4);
        assertEquals(t, Transform.identity().multiply(t));

        assertEquals(new Tuple(1, 4, 3, 12), Transform.identity().set(1, 1, 2).set(3, 3, 3).multiply(t));
    }

    @Test
    void multiplying_translation() {
        var transform = Transform.translation(5, -3, 2);
        var p = Tuple.point(-3, 4, 5);

        assertEquals(Tuple.point(2, 1, 7), transform.multiply(p));
    }

    @Test
    void multiplying_inverseOfTranslation() {
        var transform = Transform.translation(5, -3, 2);
        var p = Tuple.point(-3, 4, 5);

        assertEquals(Tuple.point(-8, 7, 3), transform.invert().multiply(p));
    }

    @Test
    void multiplying_translationDoesNotAffectVectors() {
        var transform = Transform.translation(5, -3, 2);
        var v = Tuple.vector(-3, 4, 5);

        assertEquals(v, transform.multiply(v));
    }

    @Test
    void scaling_point() {
        var transform = Transform.scaling(2, 3, 4);
        var p = Tuple.point(-4, 6, 8);

        assertEquals(Tuple.point(-8, 18, 32), transform.multiply(p));
    }

    @Test
    void scaling_vector() {
        var transform = Transform.scaling(2, 3, 4);
        var v = Tuple.vector(-4, 6, 8);

        assertEquals(Tuple.vector(-8, 18, 32), transform.multiply(v));
    }

    @Test
    void scaling_vector_by_inverse() {
        var transform = Transform.scaling(2, 3, 4);
        var v = Tuple.vector(-4, 6, 8);

        assertEquals(Tuple.vector(-2, 2, 2), transform.invert().multiply(v));
    }

    @Test
    void reflection_is_scaling_by_a_negative_value() {
        var transform = Transform.scaling(-1, 1, 1);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(-2, 3, 4), transform.multiply(p));
    }

    @Test
    void rotation_on_x_axis() {
        var p = Tuple.point(0, 1, 0);
        var halfQuarter = Transform.rotationOnX(Utils.QTR_PI);
        var fullQuarter = Transform.rotationOnX(Utils.HALF_PI);

        assertEquals(Tuple.point(0, Math.sqrt(2) / 2, Math.sqrt(2) / 2), halfQuarter.multiply(p));
        assertEquals(Tuple.point(0, 0, 1), fullQuarter.multiply(p));
    }

    @Test
    void rotation_on_x_axis_inverse_rotates_in_opposite_direction() {
        var p = Tuple.point(0, 1, 0);
        var halfQuarter = Transform.rotationOnX(Utils.QTR_PI);

        assertEquals(Tuple.point(0, Math.sqrt(2) / 2, -Math.sqrt(2) / 2), halfQuarter.invert().multiply(p));
    }

    @Test
    void rotation_on_y_axis() {
        var p = Tuple.point(0, 0, 1);
        var halfQuarter = Transform.rotationOnY(Utils.QTR_PI);
        var fullQuarter = Transform.rotationOnY(Utils.HALF_PI);

        assertEquals(Tuple.point(Math.sqrt(2) / 2, 0, Math.sqrt(2) / 2), halfQuarter.multiply(p));
        assertEquals(Tuple.point(1, 0, 0), fullQuarter.multiply(p));
    }

    @Test
    void rotation_on_z_axis() {
        var p = Tuple.point(0, 1, 0);
        var halfQuarter = Transform.rotationOnZ(Utils.QTR_PI);
        var fullQuarter = Transform.rotationOnZ(Utils.HALF_PI);

        assertEquals(Tuple.point(-Math.sqrt(2) / 2, Math.sqrt(2) / 2, 0), halfQuarter.multiply(p));
        assertEquals(Tuple.point(-1, 0, 0), fullQuarter.multiply(p));
    }

    @Test
    void shearing_moves_x_in_proportion_to_y() {
        var transform = Transform.shearing(1, 0, 0, 0, 0, 0);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(5, 3, 4), transform.multiply(p));
    }

    @Test
    void shearing_moves_x_in_proportion_to_z() {
        var transform = Transform.shearing(0, 1, 0, 0, 0, 0);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(6, 3, 4), transform.multiply(p));
    }

    @Test
    void shearing_moves_y_in_proportion_to_x() {
        var transform = Transform.shearing(0, 0, 1, 0, 0, 0);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(2, 5, 4), transform.multiply(p));
    }

    @Test
    void shearing_moves_y_in_proportion_to_z() {
        var transform = Transform.shearing(0, 0, 0, 1, 0, 0);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(2, 7, 4), transform.multiply(p));
    }

    @Test
    void shearing_moves_z_in_proportion_to_x() {
        var transform = Transform.shearing(0, 0, 0, 0, 1, 0);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(2, 3, 6), transform.multiply(p));
    }

    @Test
    void shearing_moves_z_in_proportion_to_y() {
        var transform = Transform.shearing(0, 0, 0, 0, 0, 1);
        var p = Tuple.point(2, 3, 4);

        assertEquals(Tuple.point(2, 3, 7), transform.multiply(p));
    }

    @Test
    void transformations_in_sequence() {
        var p = Tuple.point(1, 0, 1);
        var a = Transform.rotationOnX(Utils.HALF_PI);
        var b = Transform.scaling(5, 5, 5);
        var c = Transform.translation(10, 5, 7);

        // apply rotation first
        var p2 = a.multiply(p);
        assertEquals(Tuple.point(1, -1, 0), p2);

        // then apply scaling
        var p3 = b.multiply(p2);
        assertEquals(Tuple.point(5, -5, 0), p3);

        // then apply translation
        var p4 = c.multiply(p3);
        assertEquals(Tuple.point(15, 0, 7), p4);
    }

    @Test
    void transformations_must_be_applied_in_reverse_order() {
        var p = Tuple.point(1, 0, 1);
        var a = Transform.rotationOnX(Utils.HALF_PI);
        var b = Transform.scaling(5, 5, 5);
        var c = Transform.translation(10, 5, 7);

        var t = c.multiply(b.multiply(a));

        assertEquals(Tuple.point(15, 0, 7), t.multiply(p));
    }

    @Test
    void transformations_can_be_chained_fluently_in_intuitive_order() {
        var p = Tuple.point(1, 0, 1);
        var transform = Transform.identity()
                .rotateOnX(Utils.HALF_PI)
                .scale(5, 5, 5)
                .translate(10, 5, 7);

        assertEquals(Tuple.point(15, 0, 7), transform.multiply(p));
    }
}