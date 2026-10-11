package io.zocalo.scene.patterns;

import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
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
