package io.zocalo.kernel;

import io.zocalo.Utils;
import io.zocalo.scene.World;

import java.util.concurrent.Executors;

public class Camera {

    private static final boolean SHOW_PREVIEW = true;
    private static final boolean SHADOWS = true;
    private static final boolean REFLECTIONS = true;
    private static final boolean REFRACTIONS = true;
    private static final RenderMode RENDER_MODE = RenderMode.SHADE;
    private static final int TILE_SIZE = 32;
    private static final int RECURSIVE_REFLECTION_LIMIT = 10;

    private final int hsize, vsize;
    private final double fov;
    private final Matrix4x4 viewTransform;
    private final Matrix4x4 inverseViewTransform;
    private final double halfWidth;
    private final double halfHeight;
    private final double pixelSize;

    public Camera(int hsize, int vsize, double fov, Matrix4x4 viewTransform) {
        this.hsize = hsize;
        this.vsize = vsize;
        this.fov = fov;
        this.viewTransform = viewTransform;
        this.inverseViewTransform = viewTransform.invert();
        var halfView = Math.tan(fov / 2);
        var aspect = (double) hsize / vsize;
        if (aspect >= 1) {
            halfWidth = halfView;
            halfHeight = halfView / aspect;
        } else {
            halfWidth = halfView * aspect;
            halfHeight = halfView;
        }
        pixelSize = (halfWidth * 2) / hsize;
    }

    public Camera(int hsize, int vsize, double fov) {
        this(hsize, vsize, fov, Matrix4x4.identity());
    }

    public Matrix4x4 transform() {
        return viewTransform;
    }

    public double fov() {
        return fov;
    }

    public int vsize() {
        return vsize;
    }

    public int hsize() {
        return hsize;
    }

    public double pixelSize() {
        return pixelSize;
    }

    public Ray rayForPixel(int px, int py) {
        // the offset from the edge of the canvas to the pixel's center
        var xOffset = (px + 0.5) * pixelSize;
        var yOffset = (py + 0.5) * pixelSize;

        // the untransformed coordinates of hte pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*
        var worldX = halfWidth - xOffset;
        var worldY = halfHeight - yOffset;

        Tuple arr = Tuple.point(worldX, worldY, -1);

        // using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        var pixel = inverseViewTransform.multiply(arr);
        var origin = inverseViewTransform.multiply(Utils.ORIGIN);
        var direction = pixel.subtract(origin).normalize();

        return new Ray(origin, direction);
    }

    public Canvas render(World w) {
        var image = new Canvas(hsize, vsize);
        if (SHOW_PREVIEW) image.setupGui();
        var start = System.nanoTime();
        for (var y = 0; y < vsize; y++) {
            for (var x = 0; x < hsize; x++) {
                var ray = rayForPixel(x, y);
                var color = w.colorAt(ray, 5, RENDER_MODE, SHADOWS, REFLECTIONS, REFRACTIONS);
                image.writePixel(x, y, color);
            }
            if (SHOW_PREVIEW) image.repaint();
        }
        var end = System.nanoTime();
        System.out.println("Duration: " + (end - start) + " nanoseconds");
        System.out.println("          " + ((end - start) / 1_000_000) + " milliseconds");
        System.out.println("          " + ((end - start) / 1_000_000_000) + " seconds");

        return image;
    }

    public Canvas renderParallel(World w) {
        return renderParallel(w, SHOW_PREVIEW);
    }

    public Canvas renderParallel(World w, boolean showPreview) {
        var image = new Canvas(hsize, vsize);

        var numTilesX = (hsize + TILE_SIZE - 1) / TILE_SIZE;
        var numTilesY = (vsize + TILE_SIZE - 1) / TILE_SIZE;

        if (showPreview) image.setupGui();
        var start = System.nanoTime();

        try (var executor = Executors.newWorkStealingPool()) {
            for (var ty = 0; ty < numTilesY; ty++) {
                for (var tx = 0; tx < numTilesX; tx++) {
                    var xStart = tx * TILE_SIZE;
                    var yStart = ty * TILE_SIZE;
                    var xEnd = Math.min(hsize, xStart + TILE_SIZE);
                    var yEnd = Math.min(vsize, yStart + TILE_SIZE);
                    executor.submit(() -> {
                        for (var y = yStart; y < yEnd; y++) {
                            for (var x = xStart; x < xEnd; x++) {
                                var ray = rayForPixel(x, y);
                                var color = w.colorAt(ray, RECURSIVE_REFLECTION_LIMIT, RENDER_MODE, SHADOWS, REFLECTIONS, REFRACTIONS);
                                image.writePixel(x, y, color);
                            }
                        }
                        if (showPreview) image.repaint();
                    });
                }
            }
        }

        var end = System.nanoTime();
        IO.println("Duration: " + (end - start) + " nanoseconds");
        IO.println("          " + ((end - start) / 1_000_000) + " milliseconds");
        IO.println("          " + ((end - start) / 1_000_000_000) + " seconds");

        return image;
    }
}
