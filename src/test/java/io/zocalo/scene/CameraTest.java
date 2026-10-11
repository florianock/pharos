package io.zocalo.scene;

import io.zocalo.kernel.Canvas;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.Utils;
import io.zocalo.kernel.Camera;
import org.junit.jupiter.api.Test;

import static io.zocalo.TestUtils.assertEqualEnough;
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
        assertEqualEnough(0.01, c.pixelSize());
    }

    @Test
    void thePixelSizeForAVerticalCamera() {
        var c = new Camera(125, 200, HALF_PI);
        assertEqualEnough(0.01, c.pixelSize());
    }

    @Test
    void constructingARayThroughTheCenterOfTheCanvas() {
        var c = new Camera(201, 101,  HALF_PI);
        var r = c.rayForPixel(100, 50);
        assertEquals(Tuple.point(0, 0,  0), r.origin());
        assertEquals(Tuple.vector(0, 0, -1), r.direction());
    }

    @Test
    void constructingARayThroughACornerOfTheCanvas() {
        var c = new Camera(201, 101, HALF_PI);
        var r = c.rayForPixel(0, 0);
        assertEquals(Tuple.point(0, 0, 0), r.origin());
        assertEquals(Tuple.vector(0.66519, 0.33259, -0.66851), r.direction());
    }

    @Test
    void constructingARayWhenTheCameraIsTransformed() {
        var t = Matrix4x4.rotationOnY(Utils.QTR_PI).multiply(Matrix4x4.translation(0, -2, 5));
        var c = new Camera(201, 101,  Utils.HALF_PI, t);
        var r = c.rayForPixel(100, 50);
        assertEquals(Tuple.point(0, 2, -5), r.origin());
        assertEquals(Tuple.vector(Math.sqrt(2)/2.0, 0, -1f * Math.sqrt(2)/2.0), r.direction());
    }

    @Test
    void renderingAWorldWithACamera() {
        var w = World.defaultWorld();
        var from = Tuple.point(0, 0, -5);
        var to = Tuple.point(0, 0, 0);
        var up = Tuple.vector(0, 1, 0);
        var c = new Camera(11, 11, HALF_PI, from.viewTransform(to, up));
        Canvas image = c.render(w);
        var expected = Utils.pack255(new Color(0.38066, 0.47583, 0.2855));
        assertEquals(expected, image.pixelAt(5, 5));
    }
}