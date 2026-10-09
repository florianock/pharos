package io.zocalo.kernel;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Shape;

public record Computations(double t, Shape object, Tuple point, Tuple eyev, Tuple normalv, boolean inside, Tuple overPoint) {

    public Color shadeHit(Light light, double shadowedIntensity) {
        return object.lighting(light, overPoint, eyev, normalv, shadowedIntensity);
    }
}
