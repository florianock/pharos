package io.zocalo.scene.shapes;

import io.zocalo.Utils;
import io.zocalo.base.*;
import io.zocalo.scene.Material;

// TODO Consider making a record
public class Sphere extends Shape {

    private static final BoundingBox BOUNDING_BOX = new BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1));

    public Sphere() {
        this(Transform.identity());
    }

    public Sphere(Matrix4x4 t) {
        this(t, Material.create(), true);
    }

    public Sphere(Material m) {
        this(Transform.identity(), m, true);
    }

    public Sphere(Matrix4x4 transform, Material material) {
        this(transform, material, true);
    }
    public Sphere(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
    }

    @Override
    public Intersections intersect(Ray worldRay) {
        var r = worldRay.transform(inverseTransform);
        var sphereToRay = r.origin().subtract(Utils.ORIGIN);

        var direction = r.direction();
        var a = direction.dot(direction);
        var b = 2 * direction.dot(sphereToRay);
        var c = sphereToRay.dot(sphereToRay) - 1;

        var discriminant = (b * b) - (4 * a * c);

        if (discriminant < 0) return Intersections.empty();

        return new Intersections(
                new Intersection((-b - Math.sqrt(discriminant)) / (2 * a), this),
                new Intersection((-b + Math.sqrt(discriminant)) / (2 * a), this)
        );
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        assert localPoint.isPoint();
        return localPoint.subtract(Utils.ORIGIN).normalize();
    }

    @Override
    public BoundingBox bounds() {
        return BOUNDING_BOX;
    }

    @Override
    public String toString() {
        return "Sphere{" +
                "material=" + material +
                ", transform=" + transform +
                '}';
    }

    public static Sphere glassSphere() {
        return new Sphere(new Material.Builder().transparency(1.0).refractiveIndex(1.5).build());
    }

    public static Sphere glassSphere(Matrix4x4 transform) {
        return new Sphere(transform, new Material.Builder().transparency(1.0).refractiveIndex(1.5).build());
    }

    public static Sphere glassSphere(Matrix4x4 transform, double refractiveIndex) {
        return new Sphere(transform, new Material.Builder().transparency(1.0).refractiveIndex(refractiveIndex).build());
    }
}