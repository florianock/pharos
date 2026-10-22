package io.zocalo.parser;

import io.zocalo.base.Camera;
import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SceneBuilder {
    private final Map<String, Map<String, Object>> defs;
    private final Map<String, Function<Map<String, Object>, Object>> factories = Map.of(
            "camera", this::buildCamera,
            "light", this::buildLight,
            "sphere", this::buildSphere,
            "plane", this::buildPlane,
            "cube", this::buildCube,
            "cylinder", this::buildCylinder,
            "cone", this::buildCone
    );

    public SceneBuilder(Map<String, Map<String, Object>> defs) {
        this.defs = defs;
    }

    /**
     * Dispatches a resolved entry to its builder based on the `add` key.
     */
    public Object instantiate(Map<String, Object> entry) {
        String type = strOf(require(entry, "add"));

        Function<Map<String, Object>, Object> builder = factories.get(type);
        if (builder == null)
            throw new IllegalArgumentException("unknown object type '" + type + "' in entry: " + entry);

        return builder.apply(entry);
    }

    static Object require(Map<String, Object> m, String key) {
        if (!m.containsKey(key)) throw new IllegalArgumentException("unknown key: " + key);
        return m.get(key);
    }

    private Camera buildCamera(Map<String, Object> m) {
        return new Camera(
                intOf(m, "width"),
                intOf(m, "height"),
                doubleOf(m, "field-of-view"),
                Tuple.viewTransform(
                        pointOf(m, "from"),
                        pointOf(m, "to"),
                        vectorOf(m, "up")
                )
        );
    }

    private Light buildLight(Map<String, Object> m) {
        if (m.containsKey("at")) {
            return Light.pointLight(
                    pointOf(m, "at"),
                    colorOf(m, "intensity")
            );
        } else if (m.containsKey("corner")) {
            return Light.areaLight(
                    pointOf(m, "corner"),
                    vectorOf(m, "uvec"),
                    intOf(m, "usteps"),
                    vectorOf(m, "vvec"),
                    intOf(m, "vsteps"),
                    booleanOf(m, "jitter"),
                    colorOf(m, "intensity")
            );
        }
        throw new IllegalArgumentException("unknown light type: " + m);
    }

    private Sphere buildSphere(Map<String, Object> m) {
        var defaultSphere = new Sphere();
        var shadow = !m.containsKey("shadow") || booleanOf(m, "shadow");
        var material = m.containsKey("material") ? materialOf(m) : defaultSphere.getMaterial();
        var transform = m.containsKey("transform") ? transformOf(m, defs) : Matrix4x4.identity();
        return new Sphere(transform, material, shadow);
    }

    private Cube buildCube(Map<String, Object> m) {
        var defaultCube = new Cube();
        var shadow = !m.containsKey("shadow") || booleanOf(m, "shadow");
        var material = m.containsKey("material") ? materialOf(m) : defaultCube.getMaterial();
        var transform = m.containsKey("transform") ? transformOf(m, defs) : Matrix4x4.identity();
        return new Cube(transform, material, shadow);
    }

    private Plane buildPlane(Map<String, Object> m) {
        var defaultPlane = new Plane();
        var shadow = !m.containsKey("shadow") || booleanOf(m, "shadow");
        var material = m.containsKey("material") ? materialOf(m) : defaultPlane.getMaterial();
        var transform = m.containsKey("transform") ? transformOf(m, defs) : Matrix4x4.identity();
        return new Plane(transform, material, shadow);
    }

    private Cylinder buildCylinder(Map<String, Object> m) {
        var defaultCylinder = new Cylinder();
        var shadow = !m.containsKey("shadow") || booleanOf(m, "shadow");
        var material = m.containsKey("material") ? materialOf(m) : defaultCylinder.getMaterial();
        var transform = m.containsKey("transform") ? transformOf(m, defs) : Matrix4x4.identity();
        var minimum = m.containsKey("minimum") ? doubleOf(m, "minimum") : defaultCylinder.minimum();
        var maximum = m.containsKey("maximum") ? doubleOf(m, "maximum") : defaultCylinder.maximum();
        var closed = m.containsKey("closed") ? booleanOf(m, "closed") : defaultCylinder.closed();
        return new Cylinder(transform, material, minimum, maximum, closed, shadow);
    }

    private Cone buildCone(Map<String, Object> m) {
        var defaultCone = new Cone();
        var shadow = !m.containsKey("shadow") || booleanOf(m, "shadow");
        var material = m.containsKey("material") ? materialOf(m) : defaultCone.getMaterial();
        var transform = m.containsKey("transform") ? transformOf(m, defs) : Matrix4x4.identity();
        var minimum = m.containsKey("minimum") ? doubleOf(m, "minimum") : defaultCone.minimum();
        var maximum = m.containsKey("maximum") ? doubleOf(m, "maximum") : defaultCone.maximum();
        var closed = m.containsKey("closed") ? booleanOf(m, "closed") : defaultCone.closed();
        return new Cone(transform, material, minimum, maximum, closed, shadow);
    }

    private static int intOf(Map<String, Object> m, String key) {
        Object v = require(m, key);
        if (v instanceof Number n) return n.intValue();
        throw parseError(key, v, "expected int");
    }

    private static double doubleOf(Map<String, Object> m, String key) {
        Object v = require(m, key);
        if (v instanceof Number d) return asDouble(d);
        throw parseError(key, v, "expected double");
    }

    private static boolean booleanOf(Map<String, Object> m, String key) {
        Object v = require(m, key);
        if (v instanceof Boolean b) return b;
        throw parseError(key, v, "expected boolean");
    }

    private static Tuple pointOf(Map<String, Object> m, String key) {
        List<Double> c = tripleOf(m, key);
        return Tuple.point(c.get(0), c.get(1), c.get(2));
    }

    private static Tuple vectorOf(Map<String, Object> m, String key) {
        List<Double> c = tripleOf(m, key);
        return Tuple.vector(c.get(0), c.get(1), c.get(2));
    }

    private static Color colorOf(Map<String, Object> m, String key) {
        List<Double> c = tripleOf(m, key);
        return new Color(c.get(0), c.get(1), c.get(2));
    }

    private static List<Double> tripleOf(Map<String, Object> m, String key) {
        Object v = require(m, key);
        if (!(v instanceof List<?> l) || l.size() != 3)
            throw parseError(key, v, "expected [x, y, z]");
        return l.stream().map(SceneBuilder::asDouble).toList();
    }

    static String strOf(Object v) {
        if (v instanceof String s) return s;
        throw new IllegalArgumentException("expected string, got: " + v);
    }

    /**
     * Resolves a material: either a define name or an inline mapping.
     */
    private Material materialOf(Map<String, Object> m) {
        var key = "material";
        Object v = require(m, key);

        if (v instanceof String name)
            return materialFrom(resolveDefine(name, key));

        if (v instanceof Map<?, ?> mm)
            return materialFrom(castMap(mm));

        throw parseError(key, v, "expected a material mapping or a define name");
    }

    private Material materialFrom(Map<String, Object> block) {
        Material mat = new Material();   // RTC defaults: white, ambient .1, diffuse .9, specular .9, shininess 200

        return new Material.Builder()
                .color(block.containsKey("color") ? colorOf(block, "color") : mat.color())
                .ambient(block.containsKey("ambient") ? doubleOf(block, "ambient") : mat.ambient())
                .diffuse(block.containsKey("diffuse") ? doubleOf(block, "diffuse") : mat.diffuse())
                .specular(block.containsKey("specular") ? doubleOf(block, "specular") : mat.specular())
                .shininess(block.containsKey("shininess") ? doubleOf(block, "shininess") : mat.shininess())
                .reflective(block.containsKey("reflective") ? doubleOf(block, "reflective") : mat.reflective())
                .transparency(block.containsKey("transparency") ? doubleOf(block, "transparency") : mat.transparency())
                .refractiveIndex(block.containsKey("refractive-index") ? doubleOf(block, "refractive-index") : mat.refractiveIndex())
                .pattern(block.containsKey("pattern") ? patternOf(block, "pattern") : mat.pattern())
                .build();
    }

    /**
     * Resolves a pattern: define name or inline mapping, then builds it.
     */
    private Pattern patternOf(Map<String, Object> m, String key) {
        Object v = deref(require(m, key), key);
        if (!(v instanceof Map<?, ?> pm))
            throw parseError(key, v, "expected a pattern mapping or a define name");
        Map<String, Object> p = castMap(pm);

        String type = strOf(require(p, "type"));

        var transform = p.containsKey("transform") ? transformOf(p, defs) : Matrix4x4.identity();

        // FIXME Add support for perturbed, nested and blended patterns
        return switch (type) {
            case "stripes" -> new Stripe(colorAt(p, "colors", 0), colorAt(p, "colors", 1), transform);
            case "gradient" -> new Gradient(colorAt(p, "colors", 0), colorAt(p, "colors", 1), transform);
            case "rings" -> new Ring(colorAt(p, "colors", 0), colorAt(p, "colors", 1), transform);
            case "checkers" -> new Checkers(colorAt(p, "colors", 0), colorAt(p, "colors", 1), transform);
            case "radial-gradient" -> new RadialGradient(colorAt(p, "colors", 0), colorAt(p, "colors", 1), transform);
            default -> throw new IllegalArgumentException("unknown pattern type '" + type + "'");
        };
    }

    /**
     * Fetches the nth element of a two-colour list, e.g. colors: [[..], [..]].
     */
    private Solid colorAt(Map<String, Object> p, String key, int index) {
        Object v = require(p, key);
        if (!(v instanceof List<?> l) || l.size() <= index)
            throw parseError(key, v, "expected a list with at least " + (index + 1) + " entries");
        var color = colorOfValue(l.get(index), key);
        return new Solid(color);
    }

    /**
     * Colour from a raw value: define reference, or [r, g, b].
     */
    private Color colorOfValue(Object v, String key) {
        Object resolved = deref(v, key);
        if (resolved instanceof List<?> l && l.size() == 3)
            return new Color(asDouble(l.get(0)), asDouble(l.get(1)), asDouble(l.get(2)));
        throw parseError(key, resolved, "expected [r, g, b]");
    }

    /**
     * Unwraps a value that may be a define reference, without recursing blindly.
     */
    private Object deref(Object v, String key) {
        if (v instanceof String name && defs.containsKey(name))
            return resolveDefine(name, key);
        return v;
    }

    private Map<String, Object> resolveDefine(String name, String key) {
        Map<String, Object> block = defs.get(name);
        if (block == null)
            throw new IllegalArgumentException(
                    "key '" + key + "': no define named '" + name + "'");
        return block;
    }

    /**
     * Converts an untyped YAML value to double, accepting scalars and integers.
     * Throws with context if the value can't be coerced.
     */
    private static Double asDouble(Object v) {
        switch (v) {
            case null -> throw new IllegalArgumentException("expected number, got null");
            case Number n -> {
                return n.doubleValue();
            }
            case String s -> {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("cannot parse '" + s + "' as number", e);
                }
            }
            default -> throw new IllegalArgumentException(
                    "expected number, got " + v.getClass().getSimpleName() + ": " + v
            );
        }
    }

    /**
     * Unwraps a wildcard map from a YAML value into Map<String, Object>.
     * Erasure prevents expressing this in the instanceof check, hence the cast.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> m) {
        return (Map<String, Object>) m;
    }

    /**
     * Normalizes a transform value into a list of operations.
     * Tolerates both [[op...], [op...]] and a single [op, args...] form.
     */
    static List<Object> asList(Object v) {
        if (!(v instanceof List<?> l)) throw new IllegalArgumentException("expected transform list, got: " + v);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) l;
        return list;
    }

    /**
     * Fold a transform list into a single matrix, applied in listed order.
     */
    private static Matrix4x4 transformOf(Map<String, Object> m, Map<String, Map<String, Object>> defs) {
        var key = "transform";
        Object v = require(m, key);
        if (!(v instanceof List<?> ops))
            throw parseError(key, v, "expected a list of transform ops");

        var result = new ArrayList<>();
        for (var op : ops) {
            if (op instanceof String s) {
                result.addAll(asList(defs.get(s).get(key)));
            } else if (op instanceof List<?> l) {
                result.add(asList(l));
            }
        }

        ops = result;

        Matrix4x4 acc = Matrix4x4.identity();
        for (Object op : ops) {
            List<?> parts = asList(op);
            if (parts.isEmpty()) continue;
            acc = opMatrix(parts).multiply(acc);   // pre-multiply: listed order = application order
        }
        return acc;
    }

    private static Matrix4x4 opMatrix(List<?> parts) {
        String op = strOf(parts.getFirst());
        List<Double> a = parts.stream().skip(1).map(SceneBuilder::asDouble).toList();

        return switch (op) {
            case "translate" -> Matrix4x4.translation(check(a, 3));
            case "scale" -> Matrix4x4.scaling(check(a, 3));
            case "rotate-x" -> Matrix4x4.rotationOnX(check(a, 1).getFirst());
            case "rotate-y" -> Matrix4x4.rotationOnY(check(a, 1).getFirst());
            case "rotate-z" -> Matrix4x4.rotationOnZ(check(a, 1).getFirst());
            case "shear" -> Matrix4x4.shearing(check(a, 6));
            default -> throw new IllegalArgumentException(
                    "unknown transform op '" + op + "' (expected translate, scale, rotate-x/y/z, shear)");
        };
    }

    /**
     * Validates that a transform op got exactly n arguments,
     * returning the validated list so call sites can chain.
     */
    private static List<Double> check(List<Double> a, int n) {
        if (a.size() != n)
            throw new IllegalArgumentException(
                    "expected " + n + " argument(s) for transform op, got " + a.size()
                            + ": " + a);
        return a;   // returning the argument enables chaining — see option 2
    }

    /**
     * Builds a descriptive exception for a malformed value at a given key.
     * Returns rather than throws, so call sites read `throw parseError(...)`.
     */
    private static IllegalArgumentException parseError(String key, Object value, String expectation) {
        return new IllegalArgumentException(
                "key '" + key + "': " + expectation
                        + ", but got " + describe(value));
    }

    /**
     * Human-readable rendering of an unexpected value, including its YAML type.
     */
    private static String describe(Object v) {
        return switch (v) {
            case null -> "null";
            case String s -> "\"" + s + "\" (string)";
            case Number n -> n + " (" + n.getClass().getSimpleName() + ")";
            case Boolean b -> b + " (boolean)";
            case List<?> l -> l + " (list of " + l.size() + ")";
            case Map<?, ?> m -> m.keySet() + " (mapping)";
            default -> v + " (" + v.getClass().getSimpleName() + ")";
        };
    }
}
