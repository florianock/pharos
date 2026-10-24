package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

import java.util.Optional;

public class Cube extends Shape {

    private static final Tuple MIN = Tuple.point(-1, -1, -1);
    private static final Tuple MAX = Tuple.point(1, 1, 1);
    private static final BoundingBox BOUNDING_BOX = new BoundingBox(MIN, MAX);

    public Cube() {
        this(Transform.identity());
    }

    public Cube(Matrix4x4 t) {
        this(t, Material.create(), true);
    }

    public Cube(Material m) {
        this(Transform.identity(), m, true);
    }

    public Cube(Matrix4x4 transform, Material material) {
        this(transform, material, true);
    }
    public Cube(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
    }

    @Override
    public BoundingBox bounds() {
        return BOUNDING_BOX;
    }

    @Override
    public Tuple localNormalAt(Tuple worldPoint) {
        double x = Math.abs(worldPoint.x());
        double y = Math.abs(worldPoint.y());
        double z = Math.abs(worldPoint.z());

        var maxc = Math.max(Math.max(x, y), z);
        if (maxc == x) return Tuple.vector(worldPoint.x(), 0, 0);
        else if (maxc == y) return Tuple.vector(0, worldPoint.y(), 0);

        return Tuple.vector(0, 0, worldPoint.z());
    }

    @Override
    public Intersections intersect(Ray worldRay) {
        var localRay = worldRay.transform(inverseTransform);
        var result = getIntersect(localRay, MIN, MAX);
        return result
                .map(r -> new Intersections(
                        new Intersection(r.tmin(), this),
                        new Intersection(r.tmax(), this))
                ).orElse(new Intersections());
    }

    public static Optional<IntersectResult> getIntersect(Ray localRay, Tuple min, Tuple max) {
        var x = checkAxis(localRay.origin().x(), localRay.direction().x(), min.x(), max.x());
        var tmin = x.tmin();
        var tmax = x.tmax();

        if (tmin > tmax || tmax < 0) return Optional.empty();

        var y = checkAxis(localRay.origin().y(), localRay.direction().y(), min.y(), max.y());
        tmin = Math.max(y.tmin(), tmin);
        tmax = Math.min(y.tmax(), tmax);

        if (tmin > tmax || tmax < 0) return Optional.empty();

        var z = checkAxis(localRay.origin().z(), localRay.direction().z(), min.z(), max.z());
        tmin = Math.max(z.tmin(), tmin);
        tmax = Math.min(z.tmax(), tmax);

        if (tmin > tmax || tmax < 0) return Optional.empty();

        return Optional.of(new IntersectResult(tmin, tmax));
    }

    private static IntersectResult checkAxis(double origin, double direction, double min, double max) {
        var tminNumerator = min - origin;
        var tmaxNumerator = max - origin;

        var tmin = tminNumerator / direction;
        var tmax = tmaxNumerator / direction;

        if (tmin > tmax) return new IntersectResult(tmax, tmin);

        return  new IntersectResult(tmin, tmax);
    }

    @Override
    public String toString() {
        return "Cube{" +
                "material=" + material +
                ", transform=" + transform +
                '}';
    }
}
