package io.zocalo.scene.patterns;

import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

public final class TestPattern extends Pattern {

    public TestPattern() {
        super();
    }

    public TestPattern(Matrix4x4 transform) {
        super(transform);
    }

    public Color patternAt(Tuple point) {
        assert point.isPoint();
        var patternPoint = inverseTransform.multiply(point);
        return new Color(patternPoint.x(), patternPoint.y(), patternPoint.z());
    }
}
