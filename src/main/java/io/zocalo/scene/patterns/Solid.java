package io.zocalo.scene.patterns;

import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

public final class Solid extends Pattern {

    private final Color color;

    public Solid() {
        super();
        color = Color.BLACK;
    }

    public Solid(double r, double g, double b) {
        this(new Color(r, g, b));
    }

    public Solid(Color color) {
        super();
        this.color = color;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Solid solid)) return false;
        if (!super.equals(o)) return false;

        return color.equals(solid.color);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Solid{" +
                "color=" + color +
                ", transform=" + transform +
                '}';
    }
}
