import io.zocalo.kernel.Canvas;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.Sphere;

void main() throws IOException {
    var canvasPixels = 1080;
    var canvas = new Canvas(canvasPixels, canvasPixels);
    var start = System.nanoTime();
    var rayOrigin = Tuple.point(0, 0, -5);
    var lightPosition = Tuple.point(-10, 10, -10);
    var wallZ = 10f;
    var wallSize = 7f;

    final float pixelSize = wallSize / canvasPixels;
    final float half = wallSize / 2f;
    var m = new Material.Builder().color(Color.create(1f, 0.2f, 1f)).build();
    final Sphere shape = new Sphere.Builder().material(m).build();
    var lightColor = Color.create(1, 1, 1);
    var light = Light.pointLight(lightPosition, lightColor);

    var tileSize = 32;
    var numTilesX = (canvasPixels + tileSize - 1) / tileSize;
    var numTilesY = (canvasPixels + tileSize - 1) / tileSize;

    canvas.setupGui();
    var rayOriginValues = rayOrigin.values();
    try (var executor = Executors.newWorkStealingPool()) {
        for (var ty = 0; ty < numTilesY; ty++) {
            for (var tx = 0; tx < numTilesX; tx++) {
                final int xStart = tx * tileSize;
                final int yStart = ty * tileSize;
                final int xEnd = Math.min(canvasPixels, xStart + tileSize);
                final int yEnd = Math.min(canvasPixels, yStart + tileSize);
                executor.submit(() -> {
                    var position = new float[4];
                    for (var y = yStart; y < yEnd; y++){
                        var worldY = half - pixelSize * y;

                        for (var x = xStart; x < xEnd; x++) {
                            var worldX = -half + pixelSize * x;

                            position[0] = worldX; position[1] = worldY; position[2] = wallZ; position[3] = 1;
                            var r = Ray.create(rayOriginValues, Tuple.normalize(Tuple.subtract(position, rayOriginValues)));
                            var xs = shape.intersect(r);

                            var h = xs.hit();
                            if (h.isPresent()) {
                                var hit = h.get();
                                var point = r.position(hit.t());
                                var normal = hit.shape().normalAt(point);
                                var eye = new Tuple(Tuple.negate(r.direction()));
                                var color = hit.shape().material().lighting(light, point, eye, normal, false);
                                canvas.writePixel(x, y, color);
                            }
                        }
                        canvas.repaint();
                    }
                });
            }
        }
    }

    var end = System.nanoTime();
    System.out.println("Duration: " + (end - start) + " nanoseconds");
    System.out.println("          " + ((end - start) / 1_000_000) + " milliseconds");
    System.out.println("          " + ((end - start) / 1_000_000_000) + " seconds");

    var path = Paths.get(System.getProperty("user.home"),"Pictures/pharos/sphereCh6.png");
    if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
    }

    canvas.toPNG(path);
    canvas.toPPM(Paths.get(path.getParent().toString(), "sphereCh6.ppm"));
}
