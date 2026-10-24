package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;

import java.util.Objects;
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

    /**
     * Calculate the shape's material's lighting color. If the shape has a parent group, the parent's material will take
     * precedence over the shape's material and will be used to calculate the lighting.
     *
     * @param light The Light object.
     * @param hitPoint The point where the shape was hit by a ray.
     * @param eyev The eye vector.
     * @param normalv The normal vector on the hit point.
     * @param shadowedIntensity A double value between 0 and 1 denoting the intensity of the light. This enables soft shadows.
     * @return The Color for the hit point on the object, given material, lighting, etc.
     */
    public Color lighting(Light light, Tuple hitPoint, Tuple eyev, Tuple normalv, double shadowedIntensity) {
        return Optional.ofNullable(parent)
                .flatMap(p -> Optional.ofNullable(p.getMaterial())
                        .map(m -> m.lighting(p, light, hitPoint, eyev, normalv, shadowedIntensity)))
                .orElse(material.lighting(this, light, hitPoint, eyev, normalv, shadowedIntensity));
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

        return Objects.equals(material, shape.material) && transform.equals(shape.transform);
    }

    // TODO generate correct hashCode and toString for all objects, also using parent, shadow, etc.
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
