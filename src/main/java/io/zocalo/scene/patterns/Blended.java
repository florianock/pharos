package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

public final class Blended extends Pattern {

    private final Pattern a, b;
    private final double t;

    public Blended(Pattern a, Pattern b) {
        this(a, b, 0.5);
    }

    public Blended(Pattern a, Pattern b, double t) {
        super();
        this.a = a;
        this.b = b;
        this.t = t;
    }

    public Blended(Pattern a, Pattern b, double t, Matrix4x4 transform) {
        super(transform);
        this.a = a;
        this.b = b;
        this.t = t;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        var left = a.patternAt(patternPoint).multiply(1.0 - t);
        var right = b.patternAt(patternPoint).multiply(t);
        return left.add(right);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Blended blended)) return false;
        if (!super.equals(o)) return false;

        return Double.compare(t, blended.t) == 0 && a.equals(blended.a) && b.equals(blended.b);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + a.hashCode();
        result = 31 * result + b.hashCode();
        result = 31 * result + Double.hashCode(t);
        return result;
    }

    @Override
    public String toString() {
        return "Blended{" +
                "a=" + a +
                ", b=" + b +
                ", t=" + t +
                ", transform=" + transform +
                '}';
    }
}
