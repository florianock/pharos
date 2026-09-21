package io.zocalo;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {

    private Material m;
    private Tuple position;

    @BeforeEach
    void setUp() {
        m = new Material();
        position = Tuple.point(0, 0, 0);
    }

    @Test
    void default_material() {
        assertEquals(Color.create(1, 1, 1), m.color());
        assertEquals(0.1f, m.ambient());
        assertEquals(0.9f, m.diffuse());
        assertEquals(0.9f, m.specular());
        assertEquals(200, m.shininess());
    }

    @Test
    void lighting_with_eye_between_light_and_surface() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv);
        assertEquals(Color.create(1.9f, 1.9f, 1.9f), result);
    }

    @Test
    void lighting_with_eye_between_light_and_surface_eye_offset_45_degree_angle() {
        var eyev = Tuple.vector(0, (float) Math.sqrt(2)/2, (float) Math.sqrt(2)/2);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv);
        assertEquals(Color.create(1.0f, 1.0f, 1.0f), result);
    }

    @Test
    void lighting_with_eye_opposite_surface_light_offset_45_degree_angle() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv);
        assertEquals(Color.create(0.7364f, 0.7364f, 0.7364f), result);
    }

    @Test
    void lighting_with_eye_in_path_of_reflection_vector() {
        var eyev = Tuple.vector(0, (float) -Math.sqrt(2)/2, (float) -Math.sqrt(2)/2);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv);
        assertEquals(Color.create(1.63639f, 1.63639f, 1.63639f), result);
    }

    @Test
    void lighting_with_light_behind_surface() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, 10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv);
        assertEquals(Color.create(0.1f, 0.1f, 0.1f), result);
    }
}