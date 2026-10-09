package io.zocalo;

import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;
import io.zocalo.scene.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
        assertEquals(0.1, m.ambient());
        assertEquals(0.9, m.diffuse());
        assertEquals(0.9, m.specular());
        assertEquals(200, m.shininess());
    }

    @Test
    void lighting_with_eye_between_light_and_surface() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, 1.0);
        assertEquals(Color.create(1.9, 1.9, 1.9), result);
    }

    @Test
    void lighting_with_eye_between_light_and_surface_eye_offset_45_degree_angle() {
        var eyev = Tuple.vector(0, Math.sqrt(2)/2, Math.sqrt(2)/2);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, 1.0);
        assertEquals(Color.create(1.0, 1.0, 1.0), result);
    }

    @Test
    void lighting_with_eye_opposite_surface_light_offset_45_degree_angle() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, 1.0);
        assertEquals(Color.create(0.7364, 0.7364, 0.7364), result);
    }

    @Test
    void lighting_with_eye_in_path_of_reflection_vector() {
        var eyev = Tuple.vector(0, (float) -Math.sqrt(2)/2, (float) -Math.sqrt(2)/2);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 10, -10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, 1.0);
        assertEquals(Color.create(1.63639, 1.63639, 1.63639), result);
    }

    @Test
    void lighting_with_light_behind_surface() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, 10), Color.WHITE);

        var result = m.lighting(light, position, eyev, normalv, 1.0);
        assertEquals(Color.create(0.1, 0.1, 0.1), result);
    }

    @Test
    void lighting_with_surface_in_shadow() {
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);
        var light = Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var inShadow = 0.0;

        var result = m.lighting(light, position, eyev, normalv, inShadow);
        assertEquals(Color.create(0.1, 0.1, 0.1), result);
    }

    @ParameterizedTest
    @MethodSource("intensitiesAndColors")
    void lightingUsesLightIntensityToAttenuateColor(double intensity, Color expectedColor){
        var light =  Light.pointLight(Tuple.point(0, 0, -10), Color.WHITE);
        var w = new World(new Light[]{light});
        var shape = w.objects()[0];
        var modifiedMaterial = new Material.Builder().from(shape.getMaterial())
                .ambient(0.1)
                .diffuse(0.9)
                .specular(0)
                .color(Color.WHITE)
                .build();
        shape = new Sphere(modifiedMaterial);
        var pt = Tuple.point(0, 0, -1);
        var eyev = Tuple.vector(0, 0, -1);
        var normalv = Tuple.vector(0, 0, -1);

        var result = shape.lighting(light, pt, eyev, normalv, intensity);

        assertEquals(expectedColor, result);
    }

    @ParameterizedTest
    @MethodSource("normalsAndColors")
    void lightingSamplesTheAreaLight(Tuple point, Color expectedColor) {
        var corner = Tuple.point(-0.5, -0.5, -5);
        var v1 = Tuple.vector(1, 0, 0);
        var v2 = Tuple.vector(0, 1, 0);
        var light = Light.areaLight(corner, v1, 2, v2, 2, false, Color.WHITE);
        var m = new Material.Builder()
                .ambient(0.1)
                .diffuse(0.9)
                .specular(0)
                .color(Color.WHITE)
                .build();
        var eye = Tuple.point(0, 0, -5);
        var eyev = eye.subtract(point).normalize();
        var normalv = Tuple.vector(point.x(), point.y(), point.z());

        var result = m.lighting(light, point, eyev, normalv, 1.0);

        assertEquals(expectedColor, result);
    }

    public static Stream<Arguments> intensitiesAndColors() {
        return Stream.of(
                Arguments.of(1.0, Color.create(1, 1, 1)),
                Arguments.of(0.5, Color.create(0.55, 0.55, 0.55)),
                Arguments.of(0.0, Color.create(0.1, 0.1, 0.1))
        );
    }

    public static Stream<Arguments> normalsAndColors() {
        return Stream.of(
                Arguments.of(Tuple.point(0, 0, -1), Color.create(0.9965, 0.9965, 0.9965)),
                Arguments.of(Tuple.point(0, 0.7071, -0.7071), Color.create(0.6232, 0.6232, 0.6232))
        );
    }
}