import io.zocalo.kernel.Canvas;
import io.zocalo.kernel.Ray;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.shapes.Sphere;

void main() throws IOException {
    var canvasPixels = 1080;
    var canvas = new Canvas(canvasPixels, canvasPixels);
    var start = System.nanoTime();
    var rayOrigin = Tuple.point(0, 0, -5);
    var lightPosition = Tuple.point(-10, 10, -10);
    var wallZ = 10d;
    var wallSize = 7d;

    final double pixelSize = wallSize / canvasPixels;
    final double half = wallSize / 2d;
    var m = new Material.Builder().color(new Color(1, 0.2, 1)).build();
    final Sphere shape = new Sphere(m);
    var lightColor = new Color(1, 1, 1);
    var light = Light.pointLight(lightPosition, lightColor);

    var tileSize = 32;
    var numTilesX = (canvasPixels + tileSize - 1) / tileSize;
    var numTilesY = (canvasPixels + tileSize - 1) / tileSize;

    canvas.setupGui();
    try (var executor = Executors.newWorkStealingPool()) {
        for (var ty = 0; ty < numTilesY; ty++) {
            for (var tx = 0; tx < numTilesX; tx++) {
                final int xStart = tx * tileSize;
                final int yStart = ty * tileSize;
                final int xEnd = Math.min(canvasPixels, xStart + tileSize);
                final int yEnd = Math.min(canvasPixels, yStart + tileSize);
                executor.submit(() -> {
                    for (var y = yStart; y < yEnd; y++){
                        var worldY = half - pixelSize * y;

                        for (var x = xStart; x < xEnd; x++) {
                            var worldX = -half + pixelSize * x;

                            var position = Tuple.point(worldX, worldY, wallZ);
                            var r = new Ray(rayOrigin, position.subtract(rayOrigin).normalize());
                            var xs = shape.intersect(r);

                            var h = xs.hit();
                            if (h.isPresent()) {
                                var hit = h.get();
                                var point = r.position(hit.t());
                                var normal = hit.object().normalAt(point);
                                var eye = r.direction().negate();
                                var color = hit.object().getMaterial().lighting(new Sphere(), light, point, eye, normal, 1.0);
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

    var path = Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png");
    if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
    }

    canvas.toPNG(path);
    canvas.toPPM(Paths.get(path.getParent().toString(), "test.ppm"));
}
