package io.zocalo.scene;

import io.zocalo.kernel.*;

import java.util.Arrays;

public record World(Shape[] objects, Light[] lights) {

    public World() {
        this(new Shape[0], null);
    }

    public World(Light[] lights) {
        this(getDefaultObjects(), lights);
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
                .color(Color.create(0.8, 1.0, 0.6))
                .diffuse(0.7)
                .specular(0.2)
                .build();
    }

    private static Shape[] getDefaultObjects() {
        return new Shape[]{
                new Sphere(getDefaultMaterial()),
                new Sphere(Matrix4x4.scaling(0.5, 0.5, 0.5)),
        };
    }

    private static Light[] getDefaultLight() {
        return new Light[] { Light.pointLight(Tuple.point(-10, 10, -10), Color.create(1, 1, 1)) };
    }

    public Color shadeHit(Computations comps, boolean shadows) {
        return Arrays.stream(lights)
                .map(light -> comps.shadeHit(light, shadows && isShadowed(comps.overPoint())))
                .reduce(Color.BLACK, Color::add);
    }

    public Color colorAt(Ray r) {
        return colorAt(r, RenderMode.SHADE, false);
    }

    public Color colorAt(Ray r, RenderMode mode, boolean shadows) {
        return intersect(r)
                .hit()
                .map(i -> i.prepareComputations(r))
                .map(c -> switch(mode) {
                    case DEPTH -> renderDepth(r, c);
                    case NORMALS -> renderNormals(c);
                    case SHADE -> shadeHit(c, shadows);
                })
                .orElse(Color.BLACK);
    }

    private Color renderDepth(Ray r, Computations comps) {
        var hitPoint = comps.point();

        // Distance from ray origin (camera) to hit point
        var distance = Tuple.magnitude(Tuple.subtract(hitPoint, r.origin()));

        // Option A: linear normalization (works for bounded scenes)
        var maxDistance = 10;  // tune based on your scene scale
        var normalized = Math.clamp(distance / maxDistance, 0.0, 1.0);

        // Option B: logarithmic (better for wide range, handles infinity)
//        var normalized = (float) (Math.log(distance + 1.0f) / Math.log(maxDistance + 1.0f));

        // Grayscale: white = close, black = far (or invert)
        return new Color(normalized, normalized, normalized);
    }

    private Color renderNormals(Computations comps) {
        var normal = comps.normalv();

        // Remap [-1, +1] → [0, 1]
        var red = normal[0] * 0.5 + 0.5;
        var green = normal[1] * 0.5 + 0.5;
        var blue = normal[2] * 0.5 + 0.5;

        return Color.create(red, green, blue);
    }

    public boolean isShadowed(double[] point) {

        return Arrays.stream(lights)
                .allMatch(light -> {
                    var v = Tuple.subtract(light.position().values().clone(), point);
                    var distance = Tuple.magnitude(v);
                    var direction = Tuple.normalize(v);
                    var r = Ray.create(point, direction);
                    var xs = intersect(r);
                    return xs.hit().map(i -> i.t() < distance).orElse(false);
                });
    }
}
