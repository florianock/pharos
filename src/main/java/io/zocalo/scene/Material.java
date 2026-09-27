package io.zocalo.scene;

import io.zocalo.kernel.Tuple;
import io.zocalo.Utils;

import java.util.Objects;

public class Material {

    private Color color;
    private float ambient, diffuse, specular;
    private int shininess;

    public Material(Color color, float ambient, float diffuse, float specular, int shininess) {
        this.color = color;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public Material() {
        this(Color.WHITE, 0.1f, 0.9f, 0.9f, 200);
    }

    public Material(Builder builder) {
        this(builder.color, builder.ambient, builder.diffuse, builder.specular, builder.shininess);
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }

    public float ambient() {
        return ambient;
    }

    public void ambient(float ambient) {
        this.ambient = ambient;
    }

    public float diffuse() {
        return diffuse;
    }

    public void diffuse(float diffuse) {
        this.diffuse = diffuse;
    }

    public float specular() {
        return specular;
    }

    public void specular(float specular) {
        this.specular = specular;
    }

    public int shininess() {
        return shininess;
    }

    public void shininess(int shininess) {
        this.shininess = shininess;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Material material)) return false;
        return Float.compare(ambient, material.ambient) == 0 && Float.compare(diffuse, material.diffuse) == 0 &&
                Float.compare(specular, material.specular) == 0 && shininess == material.shininess &&
                Objects.equals(color, material.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, ambient, diffuse, specular, shininess);
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
