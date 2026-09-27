package io.zocalo.scene;

import io.zocalo.kernel.*;

import java.util.Arrays;

public record World(Shape[] objects, Light light) {

    public World() {
        this(new Shape[0], null);
    }

    public World(Light light) {
        this(getDefaultObjects(), light);
    }

    public World(Shape[] objects) {
        this(objects, getDefaultLight());
    }

    public static World defaultWorld() {
        return new World(getDefaultObjects(), getDefaultLight());
    }

    public Intersections intersect(Ray r) {
        return Arrays.stream(objects)
                .map(o -> o.intersect(r))
                .reduce(new Intersections(), (a, b) ->
                        a.addAll(b.intersections().toArray(new Intersection[b.count()])));
    }

    private static Material getDefaultMaterial() {
        return new Material.Builder()
                .color(Color.create(0.8f, 1.0f, 0.6f))
                .diffuse(0.7f)
                .specular(0.2f)
                .build();
    }

    private static Shape[] getDefaultObjects() {
        return new Shape[]{
                new Sphere.Builder().material(getDefaultMaterial()).build(),
                new Sphere.Builder().transform(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)).build(),
        };
    }

    private static Light getDefaultLight() {
        return Light.pointLight(Tuple.point(-10, 10, -10), Color.create(1, 1, 1));
    }

    public Color shadeHit(Computations comps) {
        // TODO: for multiple light sources, call comps.shadeHit() for each of them and add colors
        return comps.shadeHit(light);
    }

    public Color colorAt(Ray r) {
        var xs = intersect(r);
        var hit = xs.hit();
        return hit.map(i -> shadeHit(i.prepareComputations(r))).orElse(Color.BLACK);
    }
}
