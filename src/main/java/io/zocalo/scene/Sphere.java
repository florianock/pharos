package io.zocalo.scene;

import io.zocalo.*;

public class Sphere implements Shape {

    private final Material material;
    private final Matrix4x4 transform;
    private final Matrix4x4 inverseTransform;
    private final Matrix4x4 inverseTransformTranspose;

    public Sphere(Matrix4x4 t) {
        this(t, Material.create());
    }

    public Sphere() {
        this(Matrix4x4.identity());
    }

    public Sphere(Builder builder) {
        this(builder.transform, builder.material);
    }

    public Sphere(Matrix4x4 transform, Material material) {
        this.material = material;
        this.transform = transform;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
    }

    public Material material() { return material; }

    public Matrix4x4 transform() { return transform; }

    public Intersections intersect(Ray r) {
        var r2 = r.transform(transform.invert());
        var sphereToRay = Tuple.subtract(r2.origin(), new float[] { 0, 0, 0, 1 });

        var direction = r2.direction();
        var a = Tuple.dot(direction.clone(), direction);
        var b = 2 * Tuple.dot(direction.clone(), sphereToRay);
        var c = Tuple.dot(sphereToRay.clone(), sphereToRay) - 1;

        var discriminant = (b * b) - (4 * a * c);

        if (discriminant < 0) return new Intersections();

        return new Intersections(
                new Intersection((-b - (float)Math.sqrt(discriminant)) / (2 * a), this),
                new Intersection((-b + (float)Math.sqrt(discriminant)) / (2 * a), this)
        );
    }

    public Tuple normalAt(Tuple worldPoint) {
        if (!worldPoint.isPoint()) throw new IllegalArgumentException();

        var objectPoint = inverseTransform.multiply(worldPoint);
        var objectNormal = objectPoint.subtract(Tuple.point(0, 0, 0));
//        var objectNormal = objectPoint.subtract(new float[] {0, 0, 0, 1});
        var worldNormal = inverseTransformTranspose.multiply(objectNormal);

        return Tuple.vector(worldNormal.x(), worldNormal.y(), worldNormal.z()).normalize();
    }

    public float[] normalAt(float[] worldPoint) {
        if (!Tuple.isPoint(worldPoint)) throw new IllegalArgumentException();

        var objectPoint = inverseTransform.multiply(worldPoint);
        var objectNormal = Tuple.subtract(objectPoint.clone(), new float[] {0, 0, 0, 1});
        var worldNormal = inverseTransformTranspose.multiply(objectNormal);
        worldNormal[3] = 0;

        return Tuple.normalize(worldNormal);
    }

    public static class Builder {
        private Matrix4x4 transform = Matrix4x4.identity();
        private Material material = Material.create();

        public Builder transform(Matrix4x4 transform) {
            this.transform = transform;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Sphere build() {
            return new Sphere(this);
        }
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