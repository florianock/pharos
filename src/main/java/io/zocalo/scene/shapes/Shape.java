package io.zocalo.scene.shapes;

import io.zocalo.kernel.Intersections;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;

// TODO Consider changing to sealed interface
public abstract class Shape {

    protected final Material material;
    protected final Matrix4x4 transform;
    protected final Matrix4x4 inverseTransform;
    protected final Matrix4x4 inverseTransformTranspose;
    protected final boolean shadow;

    public Shape(Matrix4x4 transform, Material material) {
        this.transform = transform;
        this.material = material;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
        shadow = true;
    }

    public Shape(Matrix4x4 transform, Material material, boolean shadow) {
        this.transform = transform;
        this.material = material;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
        this.shadow = shadow;
    }

    public abstract Tuple localNormalAt(Tuple worldPoint);

    public abstract Intersections localIntersect(Ray localRay);

    public Tuple normalAt(Tuple worldPoint) {
        assert worldPoint.isPoint();
        var localPoint = inverseTransform.multiply(worldPoint);
        var localNormal = localNormalAt(localPoint);
        var worldNormal = inverseTransformTranspose.multiply(localNormal);
        return Tuple.vector(worldNormal.x(), worldNormal.y(), worldNormal.z()).normalize();
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

    public Matrix4x4 getInverseTransform() {
        return inverseTransform;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public Tuple worldToObject(Tuple worldPoint) {
        return inverseTransform.multiply(worldPoint);
    }

    public Color lighting(Light light, Tuple shadowPoint, Tuple eyev, Tuple normalv, double shadowedIntensity) {
        return material.lighting(this, light, shadowPoint, eyev, normalv, shadowedIntensity);
    }

    public double getReflective() {
        return material.reflective();
    }

    public double getRefractiveIndex() {
        return material.refractiveIndex();
    }

    public double getTransparency() {
        return material.transparency();
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

    @Override
    public String toString() {
        return "Shape{" +
                "transform=" + transform +
                ", material=" + material +
                '}';
    }
}
