package io.zocalo.scene;

import io.zocalo.Utils;

import static io.zocalo.Utils.unpack255;

public record Color(double red, double green, double blue) {

    public Color add(Color color) {
        return Color.create(red + color.red(), green + color.green(), blue + color.blue());
    }

    public Color subtract(Color color) {
        return Color.create(red - color.red(), green - color.green(), blue - color.blue());
    }

    public Color multiply(double scalar) {
        return Color.create(red * scalar, green * scalar, blue * scalar);
    }

    public Color multiply(Color color) {
        // Hadamard Product for colors
        return Color.create(red() * color.red(), green() * color.green(), blue() * color.blue());
    }

    public static Color create(double r, double g, double b) {
        return new Color(r, g, b);
    }

    public static Color of(int i) {
        var argb = new int[4];
        unpack255(i, argb, 0);
        return new Color(argb[1] / 255f, argb[2] / 255f, argb[3] / 255f);
    }

    public static Color BLACK = Color.create(0, 0, 0);
    public static Color RED = Color.create(1, 0, 0);
    public static Color GREEN = Color.create(0, 1, 0);
    public static Color BLUE = Color.create(0, 0, 1);
    public static Color WHITE = Color.create(1, 1, 1);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Color(double red1, double green1, double blue1))) return false;

        return Utils.equalEnough(red, red1) && Utils.equalEnough(blue, blue1) &&
                Utils.equalEnough(green, green1);
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(red);
        result = 31 * result + Double.hashCode(green);
        result = 31 * result + Double.hashCode(blue);
        return result;
    }
}
