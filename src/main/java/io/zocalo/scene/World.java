package io.zocalo.scene;

import io.zocalo.kernel.*;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

import java.util.Arrays;

import static io.zocalo.Utils.EPSILON;

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
                        a.addAll(b.intersections().toArray(new Intersection[b.count()]))); // TODO clean this up
    }

    private static Material getDefaultMaterial() {
        return new Material.Builder()
                .color(new Color(0.8, 1.0, 0.6))
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
        return new Light[] { Light.pointLight(Tuple.point(-10, 10, -10), new Color(1, 1, 1)) };
    }

    public Color shadeHit(Computations comps, boolean shadows) {
        return Arrays.stream(lights)
                .map(light -> comps.shadeHit(light, shadows ? light.intensityAt(comps.overPoint(), this) : 1.0))
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
        var distance = hitPoint.subtract(r.origin()).magnitude();

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
        var red = normal.x() * 0.5 + 0.5;
        var green = normal.y() * 0.5 + 0.5;
        var blue = normal.z() * 0.5 + 0.5;

        return new Color(red, green, blue);
    }

    public boolean isShadowed(Tuple lightPosition, Tuple shadingPoint) {
        assert lightPosition.isPoint();
        assert shadingPoint.isPoint();

        var toLight = lightPosition.subtract(shadingPoint);
        var distance = toLight.magnitude();
        if (distance < EPSILON) return false;

        var dir = toLight.normalize();
        var direction = Tuple.vector(dir.x(), dir.y(), dir.z());

        var r = Ray.create(shadingPoint, direction);
        var xs = intersect(r);
        var hit = xs.hit();
        return hit
                .map(i -> i.t() < distance)
                .orElse(false);
    }

    @Override
    public String toString() {
        return "World{" +
                "objects=" + Arrays.toString(objects) +
                ", lights=" + Arrays.toString(lights) +
                '}';
    }
}
