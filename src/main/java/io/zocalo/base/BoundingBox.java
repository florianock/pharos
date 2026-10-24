package io.zocalo.base;

import io.zocalo.scene.shapes.Cube;
import io.zocalo.scene.shapes.Shape;

import java.util.Arrays;

/**
 * Axis-Aligned Bounding Box (AABB) describing the bounds of its contents with minimum and maximum points.
 *
 * @param min The minimum bounds
 * @param max The maximum bounds
 */
public record BoundingBox(Tuple min, Tuple max) {

    public BoundingBox {
        assert min.isPoint() && max.isPoint();
    }

    public BoundingBox() {
        this(Tuple.point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                Tuple.point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    public BoundingBox(Shape shape) {
        var bounds = shape.bounds();
        var min = shape.getTransform().multiply(bounds.min());
        var max = shape.getTransform().multiply(bounds.max());
        this(Tuple.point(min.x(), min.y(), min.z()), Tuple.point(max.x(), max.y(), max.z()));
    }

    public BoundingBox add(Tuple point) {
        assert point.isPoint();
        return new BoundingBox(
                Tuple.point(
                        Math.min(min.x(), point.x()),
                        Math.min(min.y(), point.y()),
                        Math.min(min.z(), point.z())),
                Tuple.point(
                        Math.max(max.x(), point.x()),
                        Math.max(max.y(), point.y()),
                        Math.max(max.z(), point.z())));    }

    public BoundingBox add(BoundingBox b) {
        return new BoundingBox(
                Tuple.point(
                        Math.min(min.x(), b.min().x()),
                        Math.min(min.y(), b.min().y()),
                        Math.min(min.z(), b.min().z())),
                Tuple.point(
                        Math.max(max.x(), b.max().x()),
                        Math.max(max.y(), b.max().y()),
                        Math.max(max.z(), b.max().z())));
    }

    public boolean contains(Tuple point) {
        assert point.isPoint();
        return min.x() <= point.x() && point.x() <= max.x() &&
                min.y() <= point.y() && point.y() <= max.y() &&
                min.z() <= point.z() && point.z() <= max.z();
    }

    public boolean contains(BoundingBox boundingBox) {
        return contains(boundingBox.min()) && contains(boundingBox.max());
    }

    public BoundingBox transform(Matrix4x4 t) {
        var points = new Tuple[] {
                min,
                Tuple.point(min.x(), min.y(), max.z()),
                Tuple.point(min.x(), max.y(), min.z()),
                Tuple.point(min.x(), max.y(), max.z()),
                Tuple.point(max.x(), min.y(), min.z()),
                Tuple.point(max.x(), min.y(), max.z()),
                Tuple.point(max.x(), max.y(), min.z()),
                max
        };
        return Arrays.stream(points).reduce(new BoundingBox(), (acc, p) -> acc.add(t.multiply(p)), BoundingBox::add);
    }

    public boolean intersects(Ray r) {
        return Cube.getIntersect(r, min, max).isPresent(); // TODO possibly do something more with the result than return a boolean?
    }
}
