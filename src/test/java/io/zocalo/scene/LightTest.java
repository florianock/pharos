package io.zocalo.scene;

import io.zocalo.kernel.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LightTest {

    @Test
    void point_light_has_position_and_intensity() {
        var position = Tuple.point(0, 0, 0);
        var intensity = Color.WHITE;
        var light = Light.pointLight(position, intensity);

        assertEquals(light.position(), position);
        assertEquals(light.intensity(), intensity);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1.0001, 0, 1.0",
            "-1.0001, 0, 0, 1.0",
            "0, 0, -1.0001, 1.0",
            "0, 0, 1.0001, 0.0",
            "1.0001, 0, 0, 0.0",
            "0, -1.0001, 0, 0.0",
            "0, 0, 0, 0.0"
    })
    void pointLightsEvaluateTheLightIntensityAtAGivenPoint(double px, double py, double pz, double expected) {
        var w = World.defaultWorld();
        var light = w.lights()[0];
        var pt = Tuple.point(px, py, pz);
        var intensity = light.intensityAt(pt, w);

        assertEquals(expected, intensity);
    }

    @Test
    void creatingAnAreaLight() {
        var corner = Tuple.point(0, 0, 0);
        var v1 = Tuple.vector(2, 0, 0);
        var v2 = Tuple.vector(0, 0, 1);
        var light = Light.areaLight(corner, v1, 4, v2, 2, false, Color.WHITE);
        assertEquals(corner, light.corner());
        assertEquals(Tuple.vector(0.5, 0, 0), light.uvec());
        assertEquals(4, light.usteps());
        assertEquals(Tuple.vector(0, 0, 0.5), light.vvec());
        assertEquals(2, light.vsteps());
        assertEquals(8, light.samples());
        assertEquals(Tuple.point(1, 0, 0.5), light.position());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0.25, 0, 0.25",
            "1, 0, 0.75, 0, 0.25",
            "0, 1, 0.25, 0, 0.75",
            "2, 0, 1.25, 0, 0.25",
            "3, 1, 1.75, 0, 0.75",
    })
    void findingASinglePointOnAnAreaLight(int u, int v, double px, double py, double pz) {
        var corner = Tuple.point(0, 0, 0);
        var v1 = Tuple.vector(2, 0, 0);
        var v2 = Tuple.vector(0, 0, 1);
        var light = Light.areaLight(corner, v1, 4, v2, 2, false, Color.WHITE);
        var pt = light.pointOnLight(u, v);
        assertEquals(Tuple.point(px, py, pz), pt);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 2, 0.0",
            "1, -1, 2, 0.25",
            "1.5, 0, 2, 0.5",
            "1.25, 1.25, 3, 0.75",
            "0, 0, -2, 1.0"
    })
    void theAreaLightIntensityFunction(double px, double py, double pz, double expected) {
        var w = World.defaultWorld();
        var corner = Tuple.point(-0.5, -0.5, -5);
        var v1 = Tuple.vector(1, 0, 0);
        var v2 = Tuple.vector(0, 1, 0);
        var light = Light.areaLight(corner, v1, 2, v2, 2, false, Color.WHITE);
        var pt = Tuple.point(px, py, pz);

        var intensity = light.intensityAt(pt, w);

        assertEquals(expected, intensity);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 0",
            "0, 1",
            "2, 0",
            "3, 1"
    })
    void findingASinglePointOnAJitteredAreaLight(int u, int v) {
        var corner =  Tuple.point(0, 0, 0);
        var v1 =  Tuple.vector(2, 0, 0);
        var v2 = Tuple.vector(0, 0, 1);
        var light = Light.areaLight(corner, v1, 4, v2, 2, true, Color.WHITE);

        var pointA = light.pointOnLight(u, v);
        var pointB = light.pointOnLight(u, v);

        assertNotEquals(pointA, pointB);
    }
}