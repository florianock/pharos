package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

import static io.zocalo.Utils.fastFloor;

public final class Gradient extends Pattern {

    private final Pattern a, b;

    public Gradient() {
        super();
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public Gradient(Pattern a, Pattern b) {
        super();
        this.a = a;
        this.b = b;
    }

    public Gradient(Pattern a, Pattern b, Matrix4x4 transform) {
        super(transform);
        this.a = a;
        this.b = b;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        var colorA = a.patternAt(patternPoint);
        var distance = b.patternAt(patternPoint).subtract(colorA);
        var fraction = patternPoint.x() - fastFloor(patternPoint.x());
        return colorA.add(distance.multiply(fraction));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Gradient gradient)) return false;
        if (!super.equals(o)) return false;

        return a.equals(gradient.a) && b.equals(gradient.b);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Gradient{" +
                "a=" + a +
                ", b=" + b +
                ", transform=" + transform +
                '}';
    }
}
