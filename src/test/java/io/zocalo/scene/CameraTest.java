package io.zocalo.scene;

import io.zocalo.Canvas;
import io.zocalo.Matrix4x4;
import io.zocalo.Tuple;
import io.zocalo.Utils;
import org.junit.jupiter.api.Test;

import static io.zocalo.Utils.EPSILON;
import static io.zocalo.Utils.HALF_PI;
import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    @Test
    void constructingACamera() {
        var hsize = 160;
        var vsize = 120;
        var fov = HALF_PI;
        var c = new Camera(hsize, vsize, fov);
        assertEquals(160, c.hsize());
        assertEquals(120, c.vsize());
        assertEquals(fov, c.fov());
    }

    @Test
    void thePixelSizeForAHorizontalCamera() {
        var c = new Camera(200, 125, HALF_PI);
        assertEquals(0.01f, c.pixelSize());
    }

    @Test
    void thePixelSizeForAVerticalCamera() {
        var c = new Camera(125, 200, HALF_PI);
        assertEquals(0.01f, c.pixelSize());
    }

    @Test
    void constructingARayThroughTheCenterOfTheCanvas() {
        var c = new Camera(201, 101,  HALF_PI);
        var r = c.rayForPixel(100, 50);
        assertArrayEquals(new float[] {0, 0,  0, 1}, r.origin(), EPSILON);
        assertArrayEquals(new float[] {0, 0, -1, 0}, r.direction(), EPSILON);
    }

    @Test
    void constructingARayThroughACornerOfTheCanvas() {
        var c = new Camera(201, 101, HALF_PI);
        var r = c.rayForPixel(0, 0);
        assertArrayEquals(new float[] {0, 0, 0, 1}, r.origin(), EPSILON);
        assertArrayEquals(new float[] {0.66519f, 0.33259f, -0.66851f, 0}, r.direction(), EPSILON);
    }

    @Test
    void constructingARayWhenTheCameraIsTransformed() {
        var t = Matrix4x4.rotationOnY(Utils.PI / 4).multiply(Matrix4x4.translation(0, -2, 5));
        var c = new Camera(201, 101,  Utils.HALF_PI, t);
        var r = c.rayForPixel(100, 50);
        assertArrayEquals(new float[] {0, 2, -5, 1}, r.origin(), EPSILON);
        assertArrayEquals(new float[] {(float)Math.sqrt(2)/2f, 0, -1f * (float)Math.sqrt(2)/2f, 0}, r.direction(), EPSILON);
    }

    @Test
    void renderingAWorldWithACamera() {
        var w = World.defaultWorld();
        var from = Tuple.point(0, 0, -5);
        var to = Tuple.point(0, 0, 0);
        var up = Tuple.vector(0, 1, 0);
        var c = new Camera(11, 11, HALF_PI, from.viewTransform(to, up));
        Canvas image = w.render(c, w);
        var expected = Utils.pack255(Color.create(0.38066f, 0.47583f, 0.2855f));
        assertEquals(expected, image.pixelAt(5, 5));
    }
}