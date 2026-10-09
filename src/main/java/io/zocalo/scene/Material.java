package io.zocalo.scene;

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

    public Color lighting(Light light, double[] point, double[] eyev, double[] normalv, double shadowedIntensity) {

        // combine the surface color with the light's color / intensity
        var effectiveColor = color.multiply(light.intensity());

        // compute the ambient contribution
        var ambient = effectiveColor.multiply(this.ambient);

        var diffuseAndSpecular = light.getAveragedDiffuseAndSpecularFactor(point, eyev, normalv, shadowedIntensity,
                effectiveColor, this);

        // Add the three contributions together to get the final shading
        return ambient.add(diffuseAndSpecular);
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

    @Override
    public String toString() {
        return "Material{" +
                "color=" + color +
                ", ambient=" + ambient +
                ", diffuse=" + diffuse +
                ", specular=" + specular +
                ", shininess=" + shininess +
                '}';
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
