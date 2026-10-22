package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

import static io.zocalo.Utils.fastFloor;

public final class Stripe extends Pattern {

    public final Pattern a, b;

    public Stripe() {
        super();
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public Stripe(final Pattern a, final Pattern b) {
        super();
        this.a = a;
        this.b = b;
    }

    public Stripe(final Matrix4x4 transform) {
        super(transform);
        a = new Solid(Color.WHITE);
        b = new Solid(Color.BLACK);
    }

    public Stripe(final Pattern a, final Pattern b, final Matrix4x4 transform) {
        super(transform);
        this.a = a;
        this.b = b;
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        return fastFloor(patternPoint.x()) % 2 == 0 ? a.patternAt(patternPoint) : b.patternAt(patternPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stripe stripe)) return false;
        if (!super.equals(o)) return false;

        return a.equals(stripe.a) && b.equals(stripe.b);
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
        return "Stripe{" +
                "a=" + a +
                ", b=" + b +
                ", transform=" + transform +
                '}';
    }
}
