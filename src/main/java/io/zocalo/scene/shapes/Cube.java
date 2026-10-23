package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

public class Cube extends Shape {

    public Cube(Matrix4x4 t) {
        this(t, Material.create(), true);
    }

    public Cube() {
        this(Transform.identity());
    }

    public Cube(Material m) {
        this(Transform.identity(), m, true);
    }

    public Cube(Matrix4x4 transform, Material material) {
        super(transform, material, true);
    }
    public Cube(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
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
    public Intersections localIntersect(Ray localRay) {
        var x = checkAxis(localRay.origin().x(), localRay.direction().x());
        var tmin = x.tmin();
        var tmax = x.tmax();

        var y = checkAxis(localRay.origin().y(), localRay.direction().y());
        tmin = Math.max(y.tmin(), tmin);
        tmax = Math.min(y.tmax(), tmax);

        if (tmin > tmax || tmax < 0) return new Intersections();

        var z = checkAxis(localRay.origin().z(), localRay.direction().z());
        tmin = Math.max(z.tmin(), tmin);
        tmax = Math.min(z.tmax(), tmax);

        if (tmin > tmax || tmax < 0) return new Intersections();

        return new Intersections(new Intersection(tmin, this), new Intersection(tmax, this));
    }

    private IntersectResult checkAxis(double origin, double direction) {
        var tminNumerator = -1 - origin;
        var tmaxNumerator = 1 - origin;

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

    private record IntersectResult(double tmin, double tmax){}
}
