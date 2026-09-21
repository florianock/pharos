package io.zocalo;

import org.junit.jupiter.api.Test;

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
}