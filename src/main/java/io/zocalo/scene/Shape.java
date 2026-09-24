package io.zocalo.scene;

import io.zocalo.*;

public interface Shape {

    Intersections intersect(Ray r);

    Tuple normalAt(Tuple worldPoint);

    Material material();

    static Sphere sphere() {
        return new Sphere();
    }

    static Sphere sphere(Matrix4x4 t) {
        return new Sphere(t);
    }
}
