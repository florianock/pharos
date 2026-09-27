package io.zocalo.scene;

import io.zocalo.kernel.Intersections;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;

public interface Shape {

    Intersections intersect(Ray r);

    Tuple normalAt(Tuple worldPoint);

    Material material();

    default Color lighting(final Light light, final Tuple point, final Tuple eyev, final Tuple normalv) {
        return material().lighting(light, point, eyev, normalv);
    }

    static Sphere sphere() {
        return new Sphere();
    }

    static Sphere sphere(Matrix4x4 t) {
        return new Sphere(t);
    }

    static Sphere sphere(Matrix4x4 t, Material m) {
        return new Sphere(t, m);
    }
}
