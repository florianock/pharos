package io.zocalo.kernel;

import io.zocalo.scene.World;

public class Camera {

    private static final float[] ORIGIN = new float[] {0, 0, 0, 1};

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
        float xOffset = (px + 0.5f) * pixelSize;
        float yOffset = (py + 0.5f) * pixelSize;

        // the untransformed coordinates of hte pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*
        float worldX = halfWidth - xOffset;
        float worldY = halfHeight - yOffset;

        arr[0] = worldX;
        arr[1] = worldY;
        arr[2] = -1;
        arr[3] = 1;

        // using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        float[] pixel = inverseTransform.multiply(arr);
        float[] origin = inverseTransform.multiply(ORIGIN);
        float[] direction = Tuple.normalize(Tuple.subtract(pixel, origin));

        return Ray.create(origin, direction);
    }

    public Canvas render(World w) {
        var image = new Canvas(hsize, vsize);
        var arr = new float[4];
        for (var y = 0; y < vsize; y++) {
            for (var x = 0; x < hsize; x++) {
                var ray = rayForPixel(x, y, arr);
                var color = w.colorAt(ray);
                image.writePixel(x, y, color);
            }
        }
        return image;
    }
}
