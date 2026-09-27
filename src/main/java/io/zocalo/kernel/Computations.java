package io.zocalo.kernel;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Shape;

public record Computations(float t, Shape object, Tuple point, Tuple eyev, Tuple normalv, boolean inside) {

    public Color shadeHit(Light light) {
        return object.lighting(light, point, eyev, normalv);
    }
}
