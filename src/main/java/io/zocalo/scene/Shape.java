package io.zocalo.scene;

import io.zocalo.IntersectionList;
import io.zocalo.Matrix4x4;
import io.zocalo.Ray;

public interface Shape {

    IntersectionList intersect(Ray r);

    static Sphere sphere() {
        return new Sphere();
    }

    static Sphere sphere(Matrix4x4 t) {
        return new Sphere(t);
    }
}
