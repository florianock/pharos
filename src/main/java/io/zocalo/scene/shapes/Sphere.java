package io.zocalo.scene.shapes;

import io.zocalo.Utils;
import io.zocalo.kernel.*;
import io.zocalo.scene.Material;

// TODO Consider making a record
public class Sphere extends Shape {

    public Sphere(Matrix4x4 t) {
        this(t, Material.create(), true);
    }

    public Sphere() {
        this(Matrix4x4.identity());
    }

    public Sphere(Material m) {
        this(Matrix4x4.identity(), m, true);
    }

    public Sphere(Matrix4x4 transform, Material material) {
        super(transform, material, true);
    }
    public Sphere(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
    }

    @Override
    public Intersections localIntersect(Ray r) {
        var sphereToRay = r.origin().subtract(Utils.ORIGIN);

        var direction = r.direction();
        var a = direction.dot(direction);
        var b = 2 * direction.dot(sphereToRay);
        var c = sphereToRay.dot(sphereToRay) - 1;

        var discriminant = (b * b) - (4 * a * c);

        if (discriminant < 0) return new Intersections();

        return new Intersections(
                new Intersection((-b - Math.sqrt(discriminant)) / (2 * a), this),
                new Intersection((-b + Math.sqrt(discriminant)) / (2 * a), this)
        );
    }

    @Override
    public Tuple localNormalAt(Tuple localPoint) {
        assert localPoint.isPoint();
        return localPoint.subtract(Utils.ORIGIN);
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