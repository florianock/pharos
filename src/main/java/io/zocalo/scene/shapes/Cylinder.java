package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

import java.util.ArrayList;

import static io.zocalo.Utils.EPSILON;

public class Cylinder extends Shape {

    private final double minimum;
    private final double maximum;
    private final boolean closed;
    private final BoundingBox boundingBox;

    public Cylinder() {
        this(Transform.identity());
    }

    public Cylinder(double minimum, double maximum) {
        this(Transform.identity(), Material.create(), minimum, maximum, false, true);
    }

    public Cylinder(double minimum, double maximum, boolean closed) {
        this(Transform.identity(), Material.create(), minimum, maximum, closed, true);
    }

    public Cylinder(Matrix4x4 t) {
        this(t, Material.create(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, true);
    }

    public Cylinder(Material m) {
        this(Transform.identity(), m, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, true);
    }

    public Cylinder(Matrix4x4 transform, Material material) {
        this(transform, material, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, true);
    }

    public Cylinder(Matrix4x4 transform, Material material, double minimum, double maximum) {
        this(transform, material, minimum, maximum, false, true);
    }

    public Cylinder(Matrix4x4 transform, Material material, double minimum, double maximum, boolean closed) {
        this(transform, material, minimum, maximum, closed, true);
    }

    public Cylinder(Matrix4x4 transform, Material material, double minimum, double maximum, boolean closed, boolean shadow) {
        super(transform, material, shadow);
        this.minimum = minimum;
        this.maximum = maximum;
        this.closed = closed;
        this.boundingBox = new BoundingBox(Tuple.point(-1, minimum, -1), Tuple.point(1, maximum, 1));
    }

    public double minimum() {
        return minimum;
    }

    public double maximum() {
        return maximum;
    }

    public boolean closed() {
        return closed;
    }

    @Override
    public BoundingBox bounds() {
        return boundingBox;
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        assert localPoint.isPoint();
        // compute the square of the distance from the y axis
        var dist = localPoint.x() * localPoint.x() + localPoint.z() * localPoint.z();
        if (dist < 1 && localPoint.y() >= maximum - EPSILON) return Tuple.vector(0, 1, 0);
        else if (dist < 1 && localPoint.y() <= minimum + EPSILON) return Tuple.vector(0, -1, 0);
        return Tuple.vector(localPoint.x(), 0, localPoint.z()).normalize();
    }

    @Override
    public Intersections intersect(Ray worldRay) {
        var localRay = worldRay.transform(inverseTransform);
        var a = localRay.direction().x() * localRay.direction().x() + localRay.direction().z() * localRay.direction().z();

        // ray is parallel to the y axis
        if (Math.abs(a) <= EPSILON) return intersectCaps(localRay, new Intersections());

        var b = 2 * localRay.origin().x() * localRay.direction().x() + 2 * localRay.origin().z() * localRay.direction().z();
        var c = localRay.origin().x() * localRay.origin().x() + localRay.origin().z() * localRay.origin().z() - 1;

        var disc = b * b - 4 * a * c;

        // ray does not intersect the cylinder
        if (disc < 0) return Intersections.empty();

        var t0 = (-b - Math.sqrt(disc)) / (2 * a);
        var t1 = (-b + Math.sqrt(disc)) / (2 * a);

        var arr = new ArrayList<Intersection>(2);
        var y0 = localRay.origin().y() + t0 * localRay.direction().y();
        if (minimum < y0 && y0 < maximum) arr.add(new Intersection(t0, this));
        var y1 = localRay.origin().y() + t1 * localRay.direction().y();
        if (minimum < y1 && y1 < maximum) arr.add(new Intersection(t1, this));
        return intersectCaps(localRay, new Intersections(arr));
    }

    private Intersections intersectCaps(Ray localRay, Intersections xs) {
        // caps only matter if the cylinder is closed, and might possibly be
        // intersected by the ray.
        if (!closed || Math.abs(localRay.direction().y()) < EPSILON) return xs;

        // check for an intersection with the lower end cap by intersecting
        // the ray with the plane at y=cyl.min
        var tMin = (minimum - localRay.origin().y()) / localRay.direction().y();
        // check for an intersection with the upper end cap by intersecting
        // the ray with the plane at y=cyl.max
        var tMax = (maximum - localRay.origin().y()) / localRay.direction().y();
        var arr = new ArrayList<Intersection>(2);
        if (checkCap(localRay, tMin)) arr.add(new Intersection(tMin, this));
        if (checkCap(localRay, tMax)) arr.add(new Intersection(tMax, this));
        return xs.addAll(arr.toArray(new Intersection[0]));
    }

    // A helper function to reduce duplication.
    // checks to see if the intersection of 't' is within a radius
    // of 1 (the radius of your cylinders) from the y axis.
    private boolean checkCap(Ray ray, double t) {
        var x = ray.origin().x() + t * ray.direction().x();
        var z = ray.origin().z() + t * ray.direction().z();
        return (x * x + z * z) <= 1;
    }

    @Override
    public String toString() {
        return "Cylinder{" +
                "min=" + minimum +
                ", max=" + maximum +
                ", closed=" + closed +
                ", material=" + material +
                ", transform=" + transform +
                ", shadow=" + shadow +
                '}';
    }
}
