package io.zocalo.scene;

import io.zocalo.kernel.Tuple;
import io.zocalo.scene.patterns.Pattern;
import io.zocalo.scene.shapes.Shape;

import java.util.Objects;
import java.util.Optional;

public record Material(Color color, double ambient, double diffuse, double specular, double shininess, Pattern pattern) {

    public Material() {
        this(Color.WHITE, 0.1, 0.9, 0.9, 200.0, null);
    }

    public Material(Builder builder) {
        this(builder.color, builder.ambient, builder.diffuse, builder.specular, builder.shininess, builder.pattern);
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
                Color color1, double ambient1, double diffuse1, double specular1, double shininess1, Pattern pattern1
        ))) return false;
        return Double.compare(ambient, ambient1) == 0 && Double.compare(diffuse, diffuse1) == 0 &&
                Double.compare(specular, specular1) == 0 && shininess == shininess1 &&
                Objects.equals(color, color1) && Objects.equals(pattern, pattern1);
    }

    public static class Builder {
        private Color color = Color.WHITE;
        private double ambient = 0.1;
        private double diffuse = 0.9;
        private double specular = 0.9;
        private double shininess = 200.0;
        private Pattern pattern = null;

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

        public Builder pattern(Pattern pattern) {
            this.pattern = pattern;
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

        public Material build() {
            return new Material(this);
        }
    }
}
