package io.zocalo;

import io.zocalo.scene.Shape;

public record Computations(float t, Shape object, Tuple point, Tuple eyev, Tuple normalv, boolean inside) {
}
