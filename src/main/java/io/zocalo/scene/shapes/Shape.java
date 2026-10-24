package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;

import java.util.Optional;

// TODO Consider changing to sealed interface
public abstract class Shape {

    protected Shape parent;
    protected final Material material;
    protected final Matrix4x4 transform;
    protected final Matrix4x4 inverseTransform;
    protected final Matrix4x4 inverseTransformTranspose;
    protected final boolean shadow;

    public Shape(Matrix4x4 transform, Material material) {
        this(transform, material, true);
    }

    public Shape(Matrix4x4 transform, Material material, boolean shadow) {
        this.transform = transform;
        this.material = material;
        inverseTransform = transform.invert();
        inverseTransformTranspose = inverseTransform.transpose();
        this.shadow = shadow;
        parent = null;
    }

    public abstract Tuple localNormalAt(Tuple localPoint);

    public abstract Intersections intersect(Ray worldRay);

    public abstract BoundingBox bounds();

    public Tuple normalAt(Tuple worldPoint) {
        assert worldPoint.isPoint();
        var localPoint = worldToObject(worldPoint);
        var localNormal = localNormalAt(localPoint);
        return normalToWorld(localNormal);
    }

    public Shape getParent() {
        return parent;
    }

    public void setParent(Shape parent) {
        this.parent = parent;
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

    public BoundingBox parentSpaceBounds() {
        return bounds().transform(transform);
    }

    public Tuple worldToObject(final Tuple worldPoint) {
        assert worldPoint.isPoint();
        var parentPoint = Optional.ofNullable(parent).map(parentShape -> parentShape.worldToObject(worldPoint)).orElse(worldPoint);
        return inverseTransform.multiply(parentPoint);
    }

    public Tuple normalToWorld(Tuple normal) {
        assert normal.isVector();
        var n = inverseTransformTranspose.multiply(normal);
        var localNormal = Tuple.vector(n.x(), n.y(), n.z()).normalize(); // w must be 0
        return Optional.ofNullable(parent).map(p -> p.normalToWorld(localNormal)).orElse(localNormal);
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
