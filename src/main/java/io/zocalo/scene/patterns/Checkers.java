package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

import static io.zocalo.Utils.fastFloor;

public final class Checkers extends Pattern {

    private final Pattern a, b;

    public Checkers() {
        super();
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public Checkers(Pattern a, Pattern b) {
        super();
        this.a = a;
        this.b = b;
    }

    public Checkers(Pattern a, Pattern b, final Matrix4x4 transform) {
        super(transform);
        this.a = a;
        this.b = b;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        var distance = fastFloor(patternPoint.x()) + fastFloor(patternPoint.y()) + fastFloor(patternPoint.z());
        return distance % 2 == 0 ? a.patternAt(patternPoint) : b.patternAt(patternPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Checkers checkers)) return false;
        if (!super.equals(o)) return false;

        return a.equals(checkers.a) && b.equals(checkers.b);
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
        return "Checkers{" +
                "a=" + a +
                ", b=" + b +
                ", transform=" + transform +
                '}';
    }
}
