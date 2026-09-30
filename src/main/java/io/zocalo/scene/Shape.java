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

    public abstract Intersections intersect(Ray r);

    public abstract double[] normalAt(Tuple worldPoint);

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

    public Color lighting(Light light, Tuple point, Tuple eyev, Tuple normalv, boolean shadowed) {
        return material.lighting(light, point, eyev, normalv, shadowed);
    }
}
