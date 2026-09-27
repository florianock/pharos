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
        return colorAt(r, RenderMode.SHADE);
    }

    public Color colorAt(Ray r, RenderMode mode) {
        var xs = intersect(r);
        var hit = xs.hit();

        if (hit.isEmpty()) return Color.BLACK;

        var comps = hit.get().prepareComputations(r);
        return switch (mode) {
            case DEPTH -> renderDepth(r, comps);
            case NORMALS -> renderNormals(comps);
            case SHADE -> shadeHit(comps);
        };
    }

    private Color renderDepth(Ray r, Computations comps) {

        var hitPoint = comps.point().values();

        // Distance from ray origin (camera) to hit point
        var distance = Tuple.magnitude(Tuple.subtract(hitPoint, r.origin()));

        // Option A: linear normalization (works for bounded scenes)
        var maxDistance = 10f;  // tune based on your scene scale
        var normalized = Math.clamp(distance / maxDistance, 0.0f, 1.0f);

        // Option B: logarithmic (better for wide range, handles infinity)
//        var normalized = (float) (Math.log(distance + 1.0f) / Math.log(maxDistance + 1.0f));

        // Grayscale: white = close, black = far (or invert)
        return new Color(normalized, normalized, normalized);
    }

    private Color renderNormals(Computations comps) {
        var normal = comps.normalv();

        // Remap [-1, +1] → [0, 1]
        var red = normal.x() * 0.5f + 0.5f;
        var green = normal.y() * 0.5f + 0.5f;
        var blue = normal.z() * 0.5f + 0.5f;

        return Color.create(red, green, blue);
    }
}
