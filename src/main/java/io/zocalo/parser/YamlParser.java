package io.zocalo.parser;

import io.zocalo.kernel.Camera;
import io.zocalo.scene.Light;
import io.zocalo.scene.World;
import io.zocalo.scene.shapes.Shape;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camera, lights, action!
 * Reads Yaml files based on Jamis Buck's scene definitions used in his book The Ray Tracer Challenge.
 */
public final class YamlParser {

    public static final String DEFAULT_FILE = "demos/cover.yml";

    void main(String... args) throws IOException {
        var objects = parse(Paths.get(DEFAULT_FILE));
        var cameras = objects.stream().filter(o -> o instanceof Camera).map(c -> (Camera) c).toList();
        assert cameras.size() == 1;
        var camera = cameras.getFirst();
        var lights = objects.stream().filter(o -> o instanceof Light).map(l -> (Light) l).toList();
        var shapes = objects.stream().filter(o -> !(o instanceof Camera || o instanceof Light)).map(s -> (Shape) s).toList();
        var c = camera.renderParallel(new World(shapes, lights));
//        c.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/coverHQ.png"));
    }

    public Collection<Object> parse(String str) {
        var options = new LoaderOptions();
        var yaml = new Yaml(options);
        List<Map<String, Object>> entries = yaml.load(str);
        return Optional.ofNullable(entries).map(this::getObjects).orElse(List.of());
    }

    public Collection<Object> parse(Path file) throws IOException {
        var options = new LoaderOptions();
        var yaml = new Yaml(options);
        try (var fileStream = Files.newInputStream(file)) {
            List<Map<String, Object>> entries = yaml.load(fileStream);
            return Optional.ofNullable(entries).map(this::getObjects).orElse(List.of());
        }
    }

    private List<Object> getObjects(Collection<Map<String, Object>> entries) {
        Map<String, Map<String, Object>> defs = new HashMap<>();

        // ---- Pass 1: hoist defines ----
        List<Map<String, Object>> useSites = new ArrayList<>();
        for (var entry : entries) {
            if (entry.containsKey("define")) {
                defs.put(strOf(entry.get("define")), valueBlock(entry, defs));
            } else {
                useSites.add(entry);
            }
        }

        // ---- Pass 2: resolve, then instantiate ----
        var builder = new SceneBuilder(defs);
        return useSites.stream()
                .map(site -> builder.instantiate(expand(site, defs, new HashSet<>())))
                .toList();
    }

    /**
     * Recursively expands `add: <define-name>` references, merging overrides.
     */
    private Map<String, Object> expand(Map<String, Object> node, Map<String, Map<String, Object>> defs, Set<String> active) {
        var name = strOf(node.get("add"));
        if (!defs.containsKey(name)) return node;          // primitive type like "cube"
        if (!active.add(name))
            throw new IllegalStateException("circular define: " + name);

        @SuppressWarnings("unchecked")
        var base = expand((Map<String, Object>) deepCopy(defs.get(name)), defs, active);
        return merge(base, node);
    }

    /**
     * Use-site keys override; transforms concatenate.
     */
    private Map<String, Object> merge(Map<String, Object> base, Map<String, Object> overrides) {
        var result = new LinkedHashMap<>(base);
        for (var e : overrides.entrySet()) {
            if (e.getKey().equals("transform") && result.containsKey("transform"))
                result.put("transform", concat(result.get("transform"), e.getValue()));
            else
                result.put(e.getKey(), e.getValue());
        }
        return result;
    }

    private Object deepCopy(Object o) {
        if (o instanceof Map<?, ?> m) {
            var copy = new LinkedHashMap<>();
            m.forEach((k, v) -> copy.put(k, deepCopy(v)));
            return copy;
        }
        if (o instanceof List<?> l)
            return l.stream().map(this::deepCopy).collect(Collectors.toCollection(ArrayList::new));
        return o;  // scalars are immutable, fine
    }

    /**
     * Concatenates the base and override transform lists,
     * producing [base..., override...] — overrides apply last.
     */
    private static List<Object> concat(Object base, Object override) {
        List<Object> result = new ArrayList<>(asList(base));
        result.addAll(asList(override));
        return result;
    }

    /**
     * Returns the define's payload: the nested map under the `value` key,
     * i.e. the actual object definition ("add: cube", "shadow: false", ...).
     */
    private static Map<String, Object> valueBlock(Map<String, Object> entry, Map<String, Map<String, Object>> defs) {
        Map<String, Object> base = entry.containsKey("extend") ? defs.get(strOf(entry.get("extend"))) : Map.of();
        if (base == null) throw new IllegalArgumentException("Missing definition of: " + strOf(entry.get("extend")));
        Object v = require(entry, "value");
        return switch (v) {
            case Map<?, ?> m -> {
                @SuppressWarnings("unchecked")
                var block = (Map<String, Object>) m;
                var result = new LinkedHashMap<>(base);
                result.putAll(block);
                yield result;
            }
            case List<?> l -> {
                var block = asList(l);
                yield Map.of("transform", expandList(block, defs));
            }
            default -> throw new IllegalArgumentException(
                    "define '" + entry.get("define") + "': 'value' must be a mapping or a transform list, got: "
                            + v.getClass().getSimpleName());
        };
    }

    private static List<Object> expandList(List<Object> block, Map<String, Map<String, Object>> defs) {
        if (block.stream().noneMatch(o -> o instanceof String)) return block;

        var result = new ArrayList<>();
        for (var i : block) {
            switch (i) {
                case String s -> result.addAll(asList(defs.get(s).get("transform")));
                case List<?> l -> result.add(l);
                case null, default -> throw new IllegalArgumentException("Can't handle type of: " + i + " in " + block);
            }
        }
        return result;
    }

    static String strOf(Object v) {
        if (v instanceof String s) return s;
        throw new IllegalArgumentException("expected string, got: " + v);
    }
    static Object require(Map<String, Object> m, String key) {
        if (!m.containsKey(key)) throw new IllegalArgumentException("unknown key: " + key);
        return m.get(key);
    }
    static List<Object> asList(Object v) {
        if (!(v instanceof List<?> l)) throw new IllegalArgumentException("expected transform list, got: " + v);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) l;
        return list;
    }
}