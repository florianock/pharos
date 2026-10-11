package io.zocalo.scene.patterns;

import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.shapes.Shape;

public abstract class Pattern {

    public final Matrix4x4 transform;
    public final Matrix4x4 inverseTransform;

    public Pattern() {
        transform = Matrix4x4.identity();
        inverseTransform = Matrix4x4.identity();
    }

    public Pattern(final Matrix4x4 transform) {
        this.transform = transform;
        this.inverseTransform = transform.invert();
    }

    public abstract Color patternAt(Tuple point);

    public Color patternAtObject(Shape obj, Tuple worldPoint) {
        assert worldPoint.isPoint();
        var objectPoint = obj.worldToObject(worldPoint);
        return patternAt(objectPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pattern pattern)) return false;

        return transform.equals(pattern.transform);
    }

    @Override
    public int hashCode() {
        return transform.hashCode();
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "transform=" + transform +
                '}';
    }
}
