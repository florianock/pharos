package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

import static io.zocalo.Utils.fastFloor;

public final class RadialGradient extends Pattern {

    private final Pattern a, b;

    public RadialGradient() {
        super();
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public RadialGradient(Matrix4x4 transform) {
        super(transform);
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public RadialGradient(final Pattern a, final Pattern b) {
        super();
        this.a = a;
        this.b = b;
    }

    public RadialGradient(final Pattern a, final Pattern b, final Matrix4x4 transform) {
        super(transform);
        this.a = a;
        this.b = b;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        var distance = Math.sqrt(patternPoint.x() * patternPoint.x() + patternPoint.z() * patternPoint.z());
        var fraction = distance - fastFloor(distance);
        var ca = a.patternAt(patternPoint);
        var cb = b.patternAt(patternPoint);
        return ca.add(cb.subtract(ca).multiply(fraction));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadialGradient that)) return false;
        if (!super.equals(o)) return false;

        return a.equals(that.a) && b.equals(that.b);
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
        return "RadialGradient{" +
                "a=" + a +
                ", b=" + b +
                ", transform=" + transform +
                '}';
    }
}
