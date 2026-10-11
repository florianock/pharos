package io.zocalo.scene;

import io.zocalo.Utils;

import static io.zocalo.Utils.unpack255;

public record Color(double red, double green, double blue) {

    public Color add(Color color) {
        return new Color(red + color.red(), green + color.green(), blue + color.blue());
    }

    public Color subtract(Color color) {
        return new Color(red - color.red(), green - color.green(), blue - color.blue());
    }

    public Color multiply(double scalar) {
        return new Color(red * scalar, green * scalar, blue * scalar);
    }

    public Color multiply(Color color) {
        // Hadamard Product for colors
        return new Color(red() * color.red(), green() * color.green(), blue() * color.blue());
    }

    public static Color of(int i) {
        var argb = new int[4];
        unpack255(i, argb, 0);
        return new Color(argb[1] / 255f, argb[2] / 255f, argb[3] / 255f);
    }

    public static Color BLACK = new Color(0, 0, 0);
    public static Color RED = new Color(1, 0, 0);
    public static Color GREEN = new Color(0, 1, 0);
    public static Color AGREEN = new Color(  42/255d, 102/255d,  41/255d ); // green: grass
    public static Color BLUE = new Color(0, 0, 1);
    public static Color DARKBLUE = new Color(   2/255d,  43/255d,  68/255d ); // dark blue: deep water
    public static Color DEEPBLUE = new Color(   9/255d,  62/255d,  92/255d ); // deep blue: water
    public static Color ABLUE = new Color(  17/255d,  82/255d, 112/255d ); // blue: shallow water
    public static Color LIGHTBLUE = new Color(  69/255d, 108/255d, 118/255d ); // light blue: shore
    public static Color WHITE = new Color(1, 1, 1);
    public static Color LIGHTGREEN = new Color( 115/255d, 128/255d,  77/255d ); // light green: veld
    public static Color BROWN = new Color( 153/255d, 143/255d,  92/255d ); // brown: tundra
    public static Color AGREY = new Color( 179/255d, 179/255d, 179/255d ); // grey: rocks
    public static Color GREY = new Color( 0.5, 0.5, 0.5 ); // grey: rocks
    public static final Color YELLOW = new Color(1, 1, 0);

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
