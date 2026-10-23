package io.zocalo.scene.shapes;

import io.zocalo.base.*;
import io.zocalo.scene.Material;

import java.util.ArrayList;
import java.util.Collection;

public class Group extends Shape {

    private final Shape[] children;

    public Group() {
        super(Transform.identity(), Material.create(), false);
        children = new Shape[0];
    }

    public Group(Shape[] children) {
        super(Transform.identity(), Material.create(), false);
        this.children = children;
        for (var child : children) child.setParent(this);
    }

    public Group(Builder builder) {
        super(builder.transform, builder.material, builder.shadow);
        this.children = builder.children.toArray(new Shape[0]);
        for (var child : children) child.setParent(this);
    }

    public Group(Matrix4x4 transform, Material material, boolean shadow) {
        super(transform, material, shadow);
        children = new Shape[0];
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
    public Tuple localNormalAt(Tuple worldPoint) {
        throw new IllegalStateException("Groups have no normal");
    }

    @Override
    public Intersections localIntersect(Ray localRay) {
        var transformedRay = localRay.transform(inverseTransform);
        var hits = new ArrayList<Intersection>();
        for (var child : children) {
            var xs = child.intersect(transformedRay);
            hits.addAll(xs.intersections());
        }
        return new Intersections(hits);
    }

    public static class Builder {
        private Matrix4x4 transform = Transform.identity();
        private Material material = Material.create();
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
