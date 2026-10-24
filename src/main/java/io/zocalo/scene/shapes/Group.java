package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class Group extends Shape {

    private final Shape[] children;
    private final BoundingBox boundingBox;

    public Group() {
        this(Transform.identity(), null, false, new Shape[0]);
    }

    public Group(Builder builder) {
        this(builder.transform, builder.material, builder.shadow, builder.children.toArray(new Shape[0]));
    }

    public Group(Matrix4x4 transform, Material material, boolean shadow,  Shape[] children) {
        super(transform, material, shadow);
        this.children = children;
        var box = new BoundingBox();
        for (var child : children) {
            child.setParent(this);
            var cbox = child.parentSpaceBounds();
            box = box.add(cbox);
        }
        this.boundingBox = box;
    }

    public boolean isEmpty() {
        return children.length == 0;
    }

    public int count() {
        return children.length;
    }

    public boolean includes(Shape s) {
        for (Shape child : children) if (child == s) return true;
        return false;
    }

    @Override
    public BoundingBox bounds() {
        return boundingBox;
    }

    @Override
    public Tuple localNormalAt(Tuple worldPoint) {
        throw new IllegalStateException("Groups have no normal");
    }

    @Override
    public Intersections intersect(Ray worldRay) {
        var localRay = worldRay.transform(inverseTransform);
        if (boundingBox.intersects(localRay)) {
            var hits = new ArrayList<Intersection>();
            for (var child : children) {
                var xs = child.intersect(localRay);
                hits.addAll(xs.intersections());
            }
            return new Intersections(hits);
        }
        return Intersections.empty();
    }

    /**
     * Implement inherited material from a parent by overriding the Shape::getMaterial method and making
     * the parent's material take precedence over the object's material. Returns null if there is no parent the parent
     * returns null for its material, and this Group also has null for material.
     *
     * @return The parent's material if present, or else the Group's material or null.
     */
    @Override
    public Material getMaterial() {
        return Optional.ofNullable(parent).map(Shape::getMaterial).orElse(material);
    }

    public static class Builder {
        private Matrix4x4 transform = Transform.identity();
        private Material material = null;
        private boolean shadow = false;
        private final ArrayList<Shape> children = new ArrayList<>();

        public Builder add(Shape shape) {
            children.add(shape);
            return this;
        }

        public Builder add(Collection<Shape> shapes) {
            children.addAll(shapes);
            return this;
        }

        public Builder transform(Matrix4x4 transform) {
            this.transform = transform;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }
}
