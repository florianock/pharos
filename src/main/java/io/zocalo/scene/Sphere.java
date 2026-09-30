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

    public Intersections intersect(Ray r) {
        var r2 = r.transform(inverseTransform);
        var sphereToRay = Tuple.subtract(r2.origin(), new double[] { 0, 0, 0, 1 });

        var direction = r2.direction();
        var a = Tuple.dot(direction.clone(), direction);
        var b = 2 * Tuple.dot(direction.clone(), sphereToRay);
        var c = Tuple.dot(sphereToRay.clone(), sphereToRay) - 1;

        var discriminant = (b * b) - (4 * a * c);

        if (discriminant < 0) return new Intersections();

        return new Intersections(
                new Intersection((-b - Math.sqrt(discriminant)) / (2 * a), this),
                new Intersection((-b + Math.sqrt(discriminant)) / (2 * a), this)
        );
    }

    public double[] normalAt(Tuple worldPoint) {
        if (!worldPoint.isPoint()) throw new IllegalArgumentException();

        var objectPoint = inverseTransform.multiply(worldPoint);
        var objectNormal = objectPoint.subtract(new Tuple(Utils.ORIGIN));
//        var objectNormal = objectPoint.subtract(new float[] {0, 0, 0, 1});
        var worldNormal = inverseTransformTranspose.multiply(objectNormal);

        return Tuple.normalize(new double[] { worldNormal.x(), worldNormal.y(), worldNormal.z(), 0 });
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Sphere sphere)) return false;

        return material.equals(sphere.material) && transform.equals(sphere.transform);
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + transform.hashCode();
        return result;
    }
}