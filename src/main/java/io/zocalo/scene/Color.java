package io.zocalo.scene;

import io.zocalo.Utils;

import static io.zocalo.Utils.unpack255;

public record Color(float red, float green, float blue) {

    public Color add(Color color) {
        return Color.create(red + color.red(), green + color.green(), blue + color.blue());
    }

    public Color subtract(Color color) {
        return Color.create(red - color.red(), green - color.green(), blue - color.blue());
    }

    public Color multiply(float scalar) {
        return Color.create(red * scalar, green * scalar, blue * scalar);
    }

    public Color multiply(Color color) {
        // Hadamard Product for colors
        return Color.create(red() * color.red(), green() * color.green(), blue() * color.blue());
    }

    public static Color create(float r, float g, float b) {
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
        if (!(o instanceof Color color)) return false;

        return Utils.equalEnough(red, color.red) && Utils.equalEnough(blue, color.blue) &&
                Utils.equalEnough(green, color.green);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(red);
        result = 31 * result + Float.hashCode(green);
        result = 31 * result + Float.hashCode(blue);
        return result;
    }
}
