package io.zocalo.scene;

import io.zocalo.kernel.Tuple;

import java.util.Objects;

public class Material {

    private Color color;
    private double ambient, diffuse, specular;
    private int shininess;

    public Material(Color color, double ambient, double diffuse, double specular, int shininess) {
        this.color = color;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public Material() {
        this(Color.WHITE, 0.1, 0.9, 0.9, 200);
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

    public double ambient() {
        return ambient;
    }

    public void ambient(double ambient) {
        this.ambient = ambient;
    }

    public double diffuse() {
        return diffuse;
    }

    public void diffuse(double diffuse) {
        this.diffuse = diffuse;
    }

    public double specular() {
        return specular;
    }

    public void specular(double specular) {
        this.specular = specular;
    }

    public int shininess() {
        return shininess;
    }

    public void shininess(int shininess) {
        this.shininess = shininess;
    }

    public Color lighting(Light light, Tuple point, Tuple eyev, Tuple normalv, boolean shadowed) {

        // combine the surface color with the light's color / intensity
        var effectiveColor = color.multiply(light.intensity());

        // find the direction to the light source
        var lightv = light.position().subtract(point).normalize();

        // compute the ambient contribution
        var ambient = effectiveColor.multiply(this.ambient);

        if (shadowed) return ambient;

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
                var factor = Math.pow(reflectDotEye, this.shininess);
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
        return Double.compare(ambient, material.ambient) == 0 && Double.compare(diffuse, material.diffuse) == 0 &&
                Double.compare(specular, material.specular) == 0 && shininess == material.shininess &&
                Objects.equals(color, material.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, ambient, diffuse, specular, shininess);
    }

    public static class Builder {
        private Color color = Color.WHITE;
        private double ambient = 0.1;
        private double diffuse = 0.9;
        private double specular = 0.9;
        private int shininess = 200;

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder ambient(double ambient) {
            this.ambient = ambient;
            return this;
        }

        public Builder diffuse(double diffuse) {
            this.diffuse = diffuse;
            return this;
        }

        public Builder specular(double specular) {
            this.specular = specular;
            return this;
        }

        public Builder shininess(int shininess) {
            this.shininess = shininess;
            return this;
        }

        public Builder from(Material material) {
            this.color = material.color;
            this.ambient = material.ambient;
            this.diffuse = material.diffuse;
            this.specular = material.specular;
            this.shininess = material.shininess;
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
