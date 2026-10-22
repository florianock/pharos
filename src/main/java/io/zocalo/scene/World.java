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
        return new Light[]{Light.pointLight(Tuple.point(-10, 10, -10), new Color(1, 1, 1))};
    }

    public Color shadeHit(Computations comps, int remaining) {
        return shadeHit(comps, remaining, true, true, true);
    }

    public Color shadeHit(Computations comps, int remaining, boolean shadows, boolean reflections, boolean refractions) {
        var surface = Arrays.stream(lights)
                .map(light -> comps.shadeHit(light, shadows ? light.intensityAt(comps.overPoint(), this) : 1.0))
                .reduce(Color.BLACK, Color::add);

        var reflected = reflections ? reflectedColor(comps, remaining) : Color.BLACK;
        var refracted = refractions ? refractedColor(comps, remaining) : Color.BLACK;

        var material = comps.object().getMaterial();
        if (reflections && refractions && material.reflective() > 0 && material.transparency() > 0) {
            var reflectance = comps.schlick();
            return surface.add(reflected.multiply(reflectance)).add(refracted.multiply(1 - reflectance));
        }

        return surface.add(reflected).add(refracted);
    }

    // TODO put stuff like RenderMode and shadows in separate Configuration object and remove
    //      these overloads like for shadeHit and colorAt
    public Color colorAt(Ray r, int remaining) {
        return colorAt(r, remaining, RenderMode.SHADE, true, true, true);
    }

    public Color colorAt(Ray r, int remaining, RenderMode mode, boolean shadows, boolean reflections, boolean refractions) {
        var xs = intersect(r);
        return xs.hit()
                .map(i -> i.prepareComputations(r, xs))
                .map(c -> switch (mode) {
                    case DEPTH -> renderDepth(r, c);
                    case NORMALS -> renderNormals(c);
                    case SHADE -> shadeHit(c, remaining, shadows, reflections, refractions);
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
        if (Math.abs(distance) < EPSILON) return false;

        var dir = toLight.normalize();
        var direction = Tuple.vector(dir.x(), dir.y(), dir.z());

        var r = new Ray(shadingPoint, direction);
        var xs = intersect(r);
        var hit = xs.hit();
        return hit
                .map(i -> i.object().hasShadow() && i.t() < distance)
                .orElse(false);
    }

    public Color reflectedColor(Computations comps, int remaining) {
        if (comps.object().getReflective() == 0 || remaining < 1) return Color.BLACK;
        var reflectRay = new Ray(comps.overPoint(), comps.reflectv());
        return colorAt(reflectRay, remaining - 1).multiply(comps.object().getReflective());
    }

    public Color refractedColor(Computations comps, int remaining) {
        if (comps.object().getTransparency() == 0 || remaining < 1) return Color.BLACK;

        // Find the ratio of first index of refraction to the second.
        // (Yup, this is inverted from the definition of Snell's Law.)
        var nRatio = comps.n1() / comps.n2();
        // cos(theta_i) is the same as the dot product of the two vectors.
        var cosi = comps.eyev().dot(comps.normalv());
        // Find sin(theta_t)^2 via trigonometric identity
        var sin2t = nRatio * nRatio * (1 - cosi * cosi);

        if (sin2t > 1) return Color.BLACK;

        // Find cos(theta_t) via trigonometric identity
        var cost = Math.sqrt(1.0 - sin2t);
        // Compute the direction of the refracted ray
        var direction = comps.normalv().multiply(nRatio * cosi - cost).subtract(comps.eyev().multiply(nRatio));
        // Create the refracted ray
        var refractRay = new Ray(comps.underPoint(), direction);

        // Find the color of the refracted ray, making sure to multiply
        // by the transparency value to account for any opacity
        return colorAt(refractRay, remaining - 1).multiply(comps.object().getTransparency());
    }

    @Override
    public String toString() {
        return "World{" +
                "objects=" + Arrays.toString(objects) +
                ", lights=" + Arrays.toString(lights) +
                '}';
    }
}
