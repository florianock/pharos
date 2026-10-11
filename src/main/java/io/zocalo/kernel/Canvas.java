package io.zocalo.kernel;

import io.zocalo.scene.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

import static io.zocalo.Utils.*;

public class Canvas {

    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int[] pixels;
    private JLabel label;

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
        assert 0 <= x && x < width && 0 <= y && y < height;
        return pixels[y * width + x];
    }

    public void writePixel(int x, int y, Color color) {
        assert 0 <= x && x < width && 0 <= y && y < height;
        writePixel(x, y, pack255(color));
    }

    public void writePixel(int x, int y, int value) {
        assert 0 <= x && x < width && 0 <= y && y < height;
        pixels[y * width + x] = value;
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

    /**
     * Setup a JPanel in a window to see the image progress.
     */
    public void setupGui() {
        JFrame frame = new JFrame("Ray Tracer Preview");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Repaint the JPanel with the image
     */
    public void repaint() {
        label.repaint();
    }
}
