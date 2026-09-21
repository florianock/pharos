package io.zocalo;

import io.zocalo.scene.Color;
import io.zocalo.scene.Light;

public record Material(Color color, float ambient, float diffuse, float specular, int shininess) {

    public Material() {
        this(Color.WHITE, 0.1f, 0.9f, 0.9f, 200);
    }

    public Material(Builder builder) {
        this(builder.color, builder.ambient, builder.diffuse, builder.specular, builder.shininess);
    }

    public Color lighting(final Light light, final Tuple point, final Tuple eyev, final Tuple normalv) {
        // combine the surface color with the light's color / intensity
        var effectiveColor = color.multiply(light.intensity());

        // find the direction to the light source
        var lightv = light.position().subtract(point).normalize();

        // compute the ambient contribution
        var ambient = effectiveColor.multiply(this.ambient);

        // lightDotNormal represents the cosine of the angle between the
        // light vector and the normal vector. A negative number means the
        // light is on the other side of the surface.
        var lightDotNormal = lightv.dot(normalv);
        var diffuse = Color.BLACK;
        var specular = Color.BLACK;
        if (lightDotNormal >= 0) {
            // compute the diffuse contribution
            diffuse = effectiveColor.multiply(this.diffuse).multiply(lightDotNormal);

            // reflectDotEye represents the cosine of the angle between the
            // reflection vector and the eye vector. A negative number means the
            // light reflects away from the eye.
            var reflectv = lightv.negate().reflect(normalv);
            var reflectDotEye = reflectv.dot(eyev);
            if (reflectDotEye > 0) {
                // compute the specular contribution
                var factor = Utils.pow(reflectDotEye, this.shininess);
                specular = light.intensity().multiply(this.specular).multiply(factor);
            }
        }

        // Add the three contributions together to get the final shading
        return ambient.add(diffuse).add(specular);
    }

    public static Material create() {
        return new Material();
    }

    public static class Builder {
        private Color color = Color.WHITE;
        private float ambient = 0.1f;
        private float diffuse = 0.9f;
        private float specular = 0.9f;
        private int shininess = 200;

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder ambient(float ambient) {
            this.ambient = ambient;
            return this;
        }

        public Builder diffuse(float diffuse) {
            this.diffuse = diffuse;
            return this;
        }

        public Builder specular(float specular) {
            this.specular = specular;
            return this;
        }

        public Builder shininess(int shininess) {
            this.shininess = shininess;
            return this;
        }

        public static Material create() {
            return new Material();
        }

        public Material build() {
            return new Material(this);
        }


    }
}
