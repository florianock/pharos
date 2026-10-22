package io.zocalo.scene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Reads camera, world and shapes
 */
public class SceneBuilder {

    public static String defaultFile = "shadow-glamour-shot.yml";

    void main(String... args) throws IOException {
        var file = args.length == 0 ? defaultFile : args[0];
        var txt = Files.readString(Paths.get("demos", file));
        var sections = Arrays.stream(txt.split("- add: "))
                .map(s ->
                        Arrays.stream(s.split("(\\r\\n|\\r|\\n)"))
                                .map(String::trim)
                                .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                                .toList())
                .filter(l -> !l.isEmpty())
                .toList();
        sections.forEach(IO::println);
    }
}
