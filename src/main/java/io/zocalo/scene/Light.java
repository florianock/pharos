package io.zocalo.scene;

import io.zocalo.kernel.Tuple;

import java.util.concurrent.ThreadLocalRandom;

public record Light(Tuple corner, Tuple uvec, int usteps, Tuple vvec, int vsteps, int samples, boolean jitter, Color intensity) {

    public static Light pointLight(Tuple position, Color intensity) {
        return new Light(position, Tuple.vector(0, 0, 0), 1, Tuple.vector(0, 0, 0), 1, 1, false, intensity);
    }

    public static Light areaLight(Tuple corner, Tuple fullUvec, int usteps, Tuple fullVvec, int vsteps, boolean jitter, Color intensity) {
        var uvec = fullUvec.divide(usteps);
        var vvec = fullVvec.divide(vsteps);
        return new Light(corner, uvec, usteps, vvec, vsteps, usteps * vsteps, jitter, intensity);
    }

    public double intensityAt(Tuple surfacePoint, World w) {
        assert surfacePoint.isPoint();
        var total = 0.0;
        for (var v = 0; v < vsteps; v++) {
            for (var u = 0; u < usteps; u++) {
                var lightPosition = pointOnLight(u, v);
                var inShadow = w.isShadowed(lightPosition, surfacePoint);
                if (!inShadow) {
                    total += 1.0;
                }
            }
        }
        return total / samples;
    }

    public Tuple pointOnLight(int u, int v) {
        var rand = ThreadLocalRandom.current();
        var jittered = corner
                .add(uvec.multiply(u + (jitter ? rand.nextDouble() : 0.5)))
                .add(vvec.multiply(v + (jitter ? rand.nextDouble() : 0.5)));
        return Tuple.point(jittered.x(), jittered.y(), jittered.z());
    }

    Color getAveragedDiffuseAndSpecularFactor(Tuple shadowPoint, Tuple eyev, Tuple normalv, double shadowedIntensity, Color effectiveColor, Material material) {
        var sum = Color.BLACK;

        for (var v = 0; v < vsteps; v++) {
            for (var u = 0; u < usteps; u++) {
                // find the direction to the light source
                var lightPosition = pointOnLight(u, v);
                var toLight = lightPosition.subtract(shadowPoint);
                var lightv = toLight.normalize();
                // lightDotNormal represents the cosine of the angle between the
                // light vector and the normal vector. A negative number means the
                // light is on the other side of the surface.
                var lightDotNormal = lightv.dot(normalv);
                if (lightDotNormal >= 0) {
                    // compute the diffuse contribution
                    sum = sum.add(effectiveColor.multiply(material.diffuse() * lightDotNormal));

                    // reflectDotEye represents the cosine of the angle between the
                    // reflection vector and the eye vector. A negative number means the
                    // light reflects away from the eye.
                    var reflectv = lightv.negate().reflect(normalv);
                    var reflectDotEye = reflectv.dot(eyev);
                    if (reflectDotEye > 0) {
                        // compute the specular contribution
                        var factor = Math.pow(reflectDotEye, material.shininess());
                        sum = sum.add(this.intensity.multiply(material.specular() * factor));
                    }
                }
            }
        }

        return sum.multiply(shadowedIntensity / (vsteps * usteps));
    }
}