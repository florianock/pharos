package io.zocalo.scene;

import io.zocalo.kernel.Tuple;
import io.zocalo.scene.patterns.Pattern;
import io.zocalo.scene.shapes.Shape;

import java.util.Objects;
import java.util.Optional;

public record Material(Color color, Pattern pattern, double ambient, double diffuse, double specular, double reflective,
                       double transparency, double refractiveIndex, double shininess) {

    public Material() {
        this(Color.WHITE, null, 0.1, 0.9, 0.9, 0.0,
                0.0, 1.0, 200.0);
    }

    public Material(Builder builder) {
        this(builder.color, builder.pattern, builder.ambient, builder.diffuse, builder.specular, builder.reflective, builder.transparency, builder.refractiveIndex, builder.shininess);
    }

    public Color lighting(Shape obj, Light light, Tuple point, Tuple eyev, Tuple normalv, double shadowedIntensity) {
        // combine the surface color with the light's color / intensity
        var colorAtPoint = Optional.ofNullable(pattern).map(p -> p.patternAtObject(obj, point)).orElse(color);
        var effectiveColor = colorAtPoint.multiply(light.intensity());

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
        if (!(o instanceof Material(
                Color color1, Pattern pattern1, double ambient1, double diffuse1, double specular1, double reflective1,
                double transparency1, double refractiveIndex1, double shininess1
        ))) return false;
        return Double.compare(ambient, ambient1) == 0 && Double.compare(diffuse, diffuse1) == 0 &&
                Double.compare(specular, specular1) == 0 && reflective == reflective1 && transparency == transparency1
                && refractiveIndex == refractiveIndex1 && shininess == shininess1 &&
                Objects.equals(color, color1) && Objects.equals(pattern, pattern1);
    }

    public static class Builder {
        private Color color = Color.WHITE;
        private Pattern pattern = null;
        private double ambient = 0.1;
        private double diffuse = 0.9;
        private double specular = 0.9;
        private double reflective = 0.0;
        private double transparency = 0.0;
        private double refractiveIndex = 1.0;
        private double shininess = 200.0;

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder color(double r, double g, double b) {
            this.color = new Color(r, g, b);
            return this;
        }

        public Builder pattern(Pattern pattern) {
            this.pattern = pattern;
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

        public Builder reflective(double reflective) {
            this.reflective = reflective;
            return this;
        }

        public Builder transparency(double transparency) {
            this.transparency = transparency;
            return this;
        }

        public Builder refractiveIndex(double refractiveIndex) {
            this.refractiveIndex = refractiveIndex;
            return this;
        }

        public Builder shininess(int shininess) {
            this.shininess = shininess;
            return this;
        }

        public Builder from(Material material) {
            this.color = material.color;
            this.pattern = material.pattern;
            this.ambient = material.ambient;
            this.diffuse = material.diffuse;
            this.specular = material.specular;
            this.reflective = material.reflective;
            this.transparency = material.transparency;
            this.refractiveIndex = material.refractiveIndex;
            this.shininess = material.shininess;
            return this;
        }

        public Material build() {
            return new Material(this);
        }
    }
}
