package io.zocalo.base;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.shapes.Shape;

public record Computations(
        double t,
        Shape object,
        Tuple point,
        Tuple eyev,
        Tuple normalv,
        Tuple reflectv,
        boolean inside,
        Tuple overPoint,
        Tuple underPoint,
        double n1,
        double n2
) {
    public Color shadeHit(Light light, double shadowedIntensity) {
        return object.lighting(light, overPoint, eyev, normalv, shadowedIntensity);
    }

    public double schlick() {
        // find the cosine of the angle between the eye and normal vectors
        var cos = eyev.dot(normalv);

        // total internal reflection can only occur if n1 > n2
        if (n1 > n2) {
            var n = n1 / n2;
            var sin2t = n * n * (1.0 - cos * cos);
            if (sin2t > 1.0) return 1.0;

            // compute cosine of theta_t using trig identity &
            // when n1 > n2, use cos(theta_t) instead
            cos = Math.sqrt(1.0 - sin2t);
        }

        var r0 = ((n1 - n2) / (n1 + n2)) * ((n1 - n2) / (n1 + n2));
        return r0 + (1 - r0) * (1 - cos) * (1 - cos) * (1 - cos) * (1 - cos) * (1 - cos);
    }
}
