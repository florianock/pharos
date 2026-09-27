package io.zocalo.kernel;

import io.zocalo.scene.World;

import java.util.concurrent.Executors;

public class Camera {

    private static final float[] ORIGIN = new float[] { 0, 0, 0, 1 };

    private static final boolean showPreview = true;
    private final int hsize, vsize;
    private final float fov;
    private final Matrix4x4 transform;
    private final Matrix4x4 inverseTransform;
    private final float halfWidth;
    private final float halfHeight;
    private final float pixelSize;

    public Camera(int hsize, int vsize, float fov, Matrix4x4 transform) {
        this.hsize = hsize;
        this.vsize = vsize;
        this.fov = fov;
        this.transform = transform;
        this.inverseTransform = transform.invert();
        var halfView = (float)Math.tan(fov / 2);
        var aspect = (float)hsize / (float)vsize;
        if (aspect >= 1) {
            halfWidth = halfView;
            halfHeight = halfView / aspect;
        } else {
            halfWidth = halfView * aspect;
            halfHeight = halfView;
        }
        pixelSize = (halfWidth * 2) / hsize;
    }

    public Camera(int hsize, int vsize, float fov) {
        this(hsize, vsize, fov, Matrix4x4.identity());
    }

    public Matrix4x4 transform() {
        return transform;
    }

    public float fov() {
        return fov;
    }

    public int vsize() {
        return vsize;
    }

    public int hsize() {
        return hsize;
    }

    public float pixelSize() {
        return pixelSize;
    }

    public Ray rayForPixel(int px, int py, float[] arr) {
        // the offset from the edge of the canvas to the pixel's center
        var xOffset = (px + 0.5f) * pixelSize;
        var yOffset = (py + 0.5f) * pixelSize;

        // the untransformed coordinates of hte pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*
        var worldX = halfWidth - xOffset;
        var worldY = halfHeight - yOffset;

        arr[0] = worldX;
        arr[1] = worldY;
        arr[2] = -1;
        arr[3] = 1;

        // using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        var pixel = inverseTransform.multiply(arr);
        var origin = inverseTransform.multiply(ORIGIN);
        var direction = Tuple.normalize(Tuple.subtract(pixel, origin));

        // TODO get rid of this create. Re-use a Ray or something similar
        return Ray.create(origin, direction);
    }

    public Canvas render(World w) {
        var image = new Canvas(hsize, vsize);
        if (showPreview) image.setupGui();
        var start = System.nanoTime();
        var arr = new float[4];
        for (var y = 0; y < vsize; y++) {
            for (var x = 0; x < hsize; x++) {
                var ray = rayForPixel(x, y, arr);
                var color = w.colorAt(ray, RenderMode.SHADE);
                image.writePixel(x, y, color);
            }
            if (showPreview) image.repaint();
        }
        var end = System.nanoTime();
        System.out.println("Duration: " + (end - start) + " nanoseconds");
        System.out.println("          " + ((end - start) / 1_000_000) + " milliseconds");
        System.out.println("          " + ((end - start) / 1_000_000_000) + " seconds");

        return image;
    }

    public Canvas renderParallel(World w) {
        var image = new Canvas(hsize, vsize);

        var tileSize = 32;
        var numTilesX = (hsize + tileSize - 1) / tileSize;
        var numTilesY = (vsize + tileSize - 1) / tileSize;

        if (showPreview) image.setupGui();
        var start = System.nanoTime();

        try (var executor = Executors.newWorkStealingPool()) {
            for (var ty = 0; ty < numTilesY; ty++) {
                for (var tx = 0; tx < numTilesX; tx++) {
                    var xStart = tx * tileSize;
                    var yStart = ty * tileSize;
                    var xEnd = Math.min(hsize, xStart + tileSize);
                    var yEnd = Math.min(vsize, yStart + tileSize);
                    executor.submit(() -> {
                        var arr = new float[4];
                        for (var y = yStart; y < yEnd; y++) {
                            for (var x = xStart; x < xEnd; x++) {
                                var ray = rayForPixel(x, y, arr);
                                var color = w.colorAt(ray, RenderMode.SHADE);
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
