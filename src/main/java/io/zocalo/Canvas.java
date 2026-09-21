package io.zocalo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;
import java.util.concurrent.Executors;

import static io.zocalo.Utils.*;

public class Canvas {

    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int[] pixels;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void initialize(Color color) {
        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                writePixel(x, y, color);
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int pixelAt(int x, int y) {
        if (x < 0 || width < x) throw new IndexOutOfBoundsException(x);
        if (y < 0 || height < y) throw new IndexOutOfBoundsException(y);

        return pixels[x * height + y];
    }

    public void writePixel(int x, int y, Color color) {
        if (x < 0 || width < x) throw new IndexOutOfBoundsException(x);
        if (y < 0 || height < y) throw new IndexOutOfBoundsException(y);

        writePixel(x, y, pack255(color));
    }

    public void writePixel(int x, int y, int value) {
        if (x < 0 || width < x) throw new IndexOutOfBoundsException(x);
        if (y < 0 || height < y) throw new IndexOutOfBoundsException(y);

        pixels[x * height + y] = value;
    }

    public void renderParallel(final Tuple rayOrigin, final Tuple lightPosition, final float wallZ, final float wallSize) {
        var canvasPixels = Math.min(this.width, this.height);

        final float pixelSize = wallSize / canvasPixels;
        final float half = wallSize / 2f;
        var m = new Material.Builder().color(Color.create(1f, 0.2f, 1f)).build();
        final Sphere shape = new Sphere.Builder().material(m).build();
        var lightColor = Color.create(1, 1, 1);
        var light = Light.pointLight(lightPosition, lightColor);

        var tileSize = 32;
        var numTilesX = (width + tileSize - 1) / tileSize;
        var numTilesY = (height + tileSize - 1) / tileSize;

        var rayOriginValues = rayOrigin.values();
        try (var executor = Executors.newWorkStealingPool()) {
            for (var ty = 0; ty < numTilesY; ty++) {
                for (var tx = 0; tx < numTilesX; tx++) {
                    final int xStart = tx * tileSize;
                    final int yStart = ty * tileSize;
                    final int xEnd = Math.min(width, xStart + tileSize);
                    final int yEnd = Math.min(height, yStart + tileSize);
                    executor.submit(() -> {
                        var position = new float[4];
                        for (var y = yStart; y < yEnd; y++){
                            var worldY = half - pixelSize * y;

                            for (var x = xStart; x < xEnd; x++){
                                var worldX = -half + pixelSize * x;

                                position[0] = worldX; position[1] = worldY; position[2] = wallZ; position[3] = 1;
                                var r = Ray.create(rayOriginValues, Tuple.normalize(Tuple.subtract(position, rayOriginValues)));
                                var xs = shape.intersect(r);

                                var h = xs.hit();
                                if (h.isPresent()) {
                                    var hit = h.get();
                                    var point = r.position(hit.t());
                                    var normal = hit.object().normalAt(point);
                                    var eye = new Tuple(Tuple.negate(r.direction()));
                                    var color = hit.object().material().lighting(light, point, eye, normal);
                                    writePixel(x, y, color);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void render(Tuple rayOrigin, Tuple lightPosition, float wallZ, float wallSize) {
        var canvasPixels = Math.min(this.width, this.height);

        var pixelSize = wallSize / canvasPixels;
        var half = wallSize / 2f;

//    var t = Matrix4x4.shearing(1, 0, 0, 0, 0, 0).multiply(Matrix4x4.scaling(0.5, 1, 1));
        var m = new Material.Builder().color(Color.create(1f, 0.2f, 1f)).build();
        var shape = new Sphere.Builder().material(m).build();
        //        var shape = Shape.sphere(Matrix4x4.rotationOnZ(Utils.PI / 4).multiply(Matrix4x4.scaling(0.5, 1, 1)));

        var lightColor = Color.create(1, 1, 1);
        var light = Light.pointLight(lightPosition, lightColor);

        var rayOriginValues = rayOrigin.values();
        for (var y = 0; y < canvasPixels; y++) {
            var worldY = half - pixelSize * y;

            for (var x = 0; x < canvasPixels; x++) {
                var worldX = -half + pixelSize * x;

//            var position = Tuple.point(worldX, worldY, wallZ);
                var position = new float[] {worldX, worldY, wallZ, 1};
                var r = Ray.create(rayOriginValues, Tuple.normalize(Tuple.subtract(position, rayOriginValues)));
                var xs = shape.intersect(r);

                var h = xs.hit();
                if (h.isPresent()) {
                    var hit = h.get();
                    var point = r.position(hit.t());
                    var normal = hit.object().normalAt(point);
                    var eye = new Tuple(Tuple.negate(r.direction()));
                    var color = hit.object().material().lighting(light, point, eye, normal);
                    writePixel(x, y, color);
                }
            }
        }
    }

    public String toPPM() {
        var ppm = new StringJoiner("\n", "P3\n", "\n");
        ppm.add("%s %s".formatted(width, height));
        ppm.add(String.valueOf(255));
        var argb = new int[4];
        for (var r = 0; r < height; r++) {
            var row = new StringBuilder(width * 3 + width);
            for (var c = 0; c < width; c++) {
                unpack255(pixelAt(c, r), argb, 0);
                row.append("%s %s %s ".formatted(argb[1], argb[2], argb[3]));
            }
            if (row.length() > 70) {
                var pointer = 70;
                while (true) {
                    if (row.charAt(pointer) == ' ') {
                        row.replace(pointer, pointer+1, "\n");
                        break;
                    } else pointer--;
                }
            }
            ppm.add(row.substring(0, row.length()-1));
        }
        return ppm.toString();
    }

    public void toPPM(Path path) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, toPPM().getBytes());
    }

    public void toPNG(Path path) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        ImageIO.write(image, "png", Files.newOutputStream(path));
    }
}
