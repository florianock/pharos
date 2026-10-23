package io.zocalo.base;

import io.zocalo.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformTest {

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