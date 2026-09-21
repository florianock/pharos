package io.zocalo.scene;

import io.zocalo.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record World(Shape[] objects, Light light) {

    public World() {
        this(new Shape[]{}, null);
    }

    public static World defaultWorld() {
        var defaultMaterial = new Material.Builder()
                .color(Color.create(0.8f, 1.0f, 0.6f))
                .diffuse(0.7f)
                .specular(0.2f)
                .build();
        var defaultObjects = new Shape[]{
                new Sphere.Builder().material(defaultMaterial).build(),
                new Sphere.Builder().transform(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)).build(),
        };
        var defaultLight = Light.pointLight(Tuple.point(-10, 10, -10), Color.create(1, 1, 1));
        return new World(defaultObjects, defaultLight);
    }

    public IntersectionList intersect(Ray r) {
        return Arrays.stream(objects)
                .map(o -> o.intersect(r))
                .reduce(new IntersectionList(), (a, b) ->
                        a.addAll(b.getIntersections().toArray(new Intersection[b.count()])));
    }
}
