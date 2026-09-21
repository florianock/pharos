package io.zocalo.scene;

import io.zocalo.Tuple;

public record Light(Tuple position, Color intensity) {

    public static Light pointLight(Tuple position, Color intensity) {
        return new Light(position, intensity);
    }
}