package io.zocalo.scene;

public enum MaterialConstants {

    VACUUM(1),
    AIR(1.00029),
    ICE(1.31),
    WATER(1.333),
    OIL(1.47),
    GLASS(1.52),
    AMBER(1.55),
    DIAMOND(2.417);

    private final double refractiveIndex;

    MaterialConstants(double refractiveIndex) {
        this.refractiveIndex = refractiveIndex;
    }

    public double refractiveIndex() {
        return refractiveIndex;
    }
}
