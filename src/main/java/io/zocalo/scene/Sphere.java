package io.zocalo.scene;

import io.zocalo.Utils;
import io.zocalo.kernel.*;

public class Sphere extends Shape {

    public Sphere(Matrix4x4 t) {
        this(t, Material.create());
    }

    public Sphere() {
        this(Matrix4x4.identity());
    }

    public Sphere(Material m) {
        this(Matrix4x4.identity(), m);
    }

    public Sphere(Matrix4x4 transform, Material material) {
        super(transform, material);
    }

    @Override
    public Intersections localIntersect(Ray r) {
        var sphereToRay = Tuple.subtract(r.origin(), new double[] { 0, 0, 0, 1 });

        var direction = r.direction();
        var a = Tuple.dot(direction, direction);
        var b = 2 * Tuple.dot(direction, sphereToRay);
        var c = Tuple.dot(sphereToRay, sphereToRay) - 1;

        var discriminant = (b * b) - (4 * a * c);

        if (discriminant < 0) return new Intersections();

//        double q = (b < 0)
//                ? -(b + Math.sqrt(discriminant)) / 2
//                : -(b - Math.sqrt(discriminant)) / 2;
//
//        return new Intersections(new Intersection(q, this), new Intersection(c / q, this));
        return new Intersections(
                new Intersection((-b - Math.sqrt(discriminant)) / (2 * a), this),
                new Intersection((-b + Math.sqrt(discriminant)) / (2 * a), this)
        );
    }

    @Override
    public double[] localNormalAt(double[] localPoint) {
        if (!Tuple.isPoint(localPoint)) throw new IllegalArgumentException();
        return Tuple.subtract(localPoint, Utils.ORIGIN);
    }

    @Override
    public String toString() {
        return "Sphere{" +
                "material=" + material +
                ", transform=" + transform +
                '}';
    }
}