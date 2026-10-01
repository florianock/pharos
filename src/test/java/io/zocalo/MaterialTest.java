package io.zocalo;

import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {

    private Material m;
    private double[] position;

    @BeforeEach
    void setUp() {
        m = new Material();
        position = new double[]{0, 0, 0, 1};
    }

    @Test
    void default_material() {
        assertEquals(Color.create(1, 1, 1), m.color());
        assertEquals(0.1, m.ambient());
        assertEquals(0.9, m.diffuse());
        assertEquals(0.9, m.specular());
        assertEquals(200, m.shininess());
    }

    @Test
    void lighting_with_eye_between_light_and_surface() {
        var eyev = new double[]{0, 0, -1, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, false);
        assertEquals(Color.create(1.9, 1.9, 1.9), result);
    }

    @Test
    void lighting_with_eye_between_light_and_surface_eye_offset_45_degree_angle() {
        var eyev = new double[]{0, Math.sqrt(2)/2, Math.sqrt(2)/2, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, false);
        assertEquals(Color.create(1.0, 1.0, 1.0), result);
    }

    @Test
    void lighting_with_eye_opposite_surface_light_offset_45_degree_angle() {
        var eyev = new double[]{0, 0, -1, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, false);
        assertEquals(Color.create(0.7364, 0.7364, 0.7364), result);
    }

    @Test
    void lighting_with_eye_in_path_of_reflection_vector() {
        var eyev = new double[]{0, (float) -Math.sqrt(2)/2, (float) -Math.sqrt(2)/2, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, false);
        assertEquals(Color.create(1.63639, 1.63639, 1.63639), result);
    }

    @Test
    void lighting_with_light_behind_surface() {
        var eyev = new double[]{0, 0, -1, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 0, 10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, false);
        assertEquals(Color.create(0.1, 0.1, 0.1), result);
    }

    @Test
    void lighting_with_surface_in_shadow() {
        var eyev = new double[]{0, 0, -1, 0};
        var normalv = new double[]{0, 0, -1, 0};
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var inShadow = true;

        var result = m.lighting(light, position, eyev, normalv, inShadow);
        assertEquals(Color.create(0.1, 0.1, 0.1), result);
    }
}