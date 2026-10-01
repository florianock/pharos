package io.zocalo.scene;

import io.zocalo.kernel.Intersections;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;

public abstract class Shape {

    protected Material material;
    protected Matrix4x4 transform;
    protected Matrix4x4 inverseTransform;
    protected Matrix4x4 inverseTransformTranspose;

    public Shape (Matrix4x4 transform, Material material) {
        this.transform = transform;
        this.material = material;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
    }

    public abstract double[] localNormalAt(double[] worldPoint);

    public abstract Intersections localIntersect(Ray localRay);

    public double[] normalAt(double[] worldPoint) {
        if (!Tuple.isPoint(worldPoint)) throw new IllegalArgumentException();
        var localPoint = inverseTransform.multiply(worldPoint);
        var localNormal = localNormalAt(localPoint);
        var worldNormal = inverseTransformTranspose.multiply(localNormal);
        worldNormal[3] = 0;
        return Tuple.normalize(worldNormal);
    }

    public Intersections intersect(Ray r) {
        var localRay = r.transform(inverseTransform);
        return localIntersect(localRay);
    }

    public Material getMaterial() {
        return material;
    }

    public Matrix4x4 getTransform() {
        return transform;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setTransform(Matrix4x4 transform) {
        this.transform = transform;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
    }

    public Color lighting(Light light, double[] point, double[] eyev, double[] normalv, boolean shadowed) {
        return material.lighting(light, point, eyev, normalv, shadowed);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Shape shape)) return false;

        return material.equals(shape.material) && transform.equals(shape.transform);
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + transform.hashCode();
        return result;
    }
}
