package io.zocalo.scene;

import io.zocalo.Matrix4x4;
import io.zocalo.Ray;
import io.zocalo.Tuple;

public class Camera {

    private final int hsize, vsize;
    private final float fov;
    private final Matrix4x4 transform;
    private float halfWidth;
    private float halfHeight;
    private float pixelSize;

    public Camera(int hsize, int vsize, float fov, Matrix4x4 transform) {
        this.hsize = hsize;
        this.vsize = vsize;
        this.fov = fov;
        this.transform = transform;
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

    // TODO make faster
    public Ray rayForPixel(int px, int py) {
        // the offset from the edge of the canvas to the pixel's center
        var xOffset = (px + 0.5f) * pixelSize;
        var yOffset = (py + 0.5f) * pixelSize;

        // the untransformed coordinates of hte pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*
        var worldX = halfWidth - xOffset;
        var worldY = halfHeight - yOffset;

        // using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        var pixel = transform.invert().multiply(Tuple.point(worldX, worldY, -1));
        var origin = transform.invert().multiply(Tuple.point(0, 0, 0));
        var direction = pixel.subtract(origin).normalize();

        return Ray.create(origin, direction);
    }
}
