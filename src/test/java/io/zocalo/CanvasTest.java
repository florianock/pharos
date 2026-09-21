package io.zocalo;

import io.zocalo.scene.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanvasTest {

    @Test
    void constructCanvas() {
        var c = new Canvas(10, 20);

        assertEquals(10, c.width());
        assertEquals(20, c.height());

        var zero = Color.create(0, 0, 0);
        for (var x = 0; x < 10; x++) {
            for (var y = 0; y < 20; y++) {
                assertEquals(zero, Color.of(c.pixelAt(x, y)));
            }
        }
    }

    @Test
    void writePixel() {
        var c = new Canvas(10, 20);

        c.writePixel(2, 3, Color.RED);

        assertEquals(Color.RED, Color.of(c.pixelAt(2, 3)));
    }

    @Test
    void canvasToPPM_Header() {
        var c = new Canvas(5, 3);

        var ppm = c.toPPM();

        var lines = ppm.split("\n");
        assertEquals("P3", lines[0]);
        assertEquals("5 3", lines[1]);
        assertEquals("255", lines[2]);
    }

    @Test
    void canvasToPPM_PixelData() {
        var c = new Canvas(5, 3);
        var c1 = Color.create(1.5f, 0, 0);
        var c2 = Color.create(0, 0.5f, 0);
        var c3 = Color.create(-0.5f, 0, 1);

        c.writePixel(0, 0, c1);
        c.writePixel(2, 1, c2);
        c.writePixel(4, 2, c3);

        var ppm = c.toPPM();

        var lines = ppm.split("\n");
        assertEquals("255 0 0 0 0 0 0 0 0 0 0 0 0 0 0", lines[3]);
        assertEquals("0 0 0 0 0 0 0 128 0 0 0 0 0 0 0", lines[4]);
        assertEquals("0 0 0 0 0 0 0 0 0 0 0 0 0 0 255", lines[5]);
    }

    @Test
    void canvasToPPM_splitsLongLines() {
        var c = new Canvas(10, 2);
        c.initialize(Color.create(1, 0.8f, 0.6f));

        var ppm = c.toPPM();

        var lines = ppm.split("\n");
        assertEquals("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204", lines[3]);
        assertEquals("153 255 204 153 255 204 153 255 204 153 255 204 153", lines[4]);
        assertEquals("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204", lines[5]);
        assertEquals("153 255 204 153 255 204 153 255 204 153 255 204 153", lines[6]);
    }

    @Test
    void canvasToPPM_endsWithNewLine() {
        var c = new Canvas(5, 3);

        var ppm = c.toPPM();

        assertEquals('\n', ppm.charAt(ppm.length() - 1));
    }
}