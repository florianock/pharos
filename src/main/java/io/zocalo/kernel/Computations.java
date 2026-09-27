package io.zocalo.kernel;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Shape;

public record Computations(float t, Shape object, Tuple point, Tuple eyev, Tuple normalv, boolean inside, Tuple overPoint) {

    public Color shadeHit(Light light, boolean shadowed) {
        return object.lighting(light, point, eyev, normalv, shadowed); // TODO or should we use overPoint as 2nd parameter, as stated in book?
    }
}
