package io.zocalo.base;

import java.util.List;

public final class Transform {

    private Transform() {
        /* Utility class, should not be instantiated */
    }

    public static Matrix4x4 identity() {
        return new Matrix4x4(new double[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 translation(List<Double> a) {
        assert a.size() == 3;
        return translation(a.get(0), a.get(1), a.get(2));
    }
    public static Matrix4x4 translation(double x, double y, double z) {
        return new Matrix4x4(new double[] {
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 scaling(List<Double> a) {
        assert a.size() == 3;
        return scaling(a.get(0), a.get(1), a.get(2));
    }
    public static Matrix4x4 scaling(double x, double y, double z) {
        return new Matrix4x4(new double[] {
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnX(double r) {
        return new Matrix4x4(new double[] {
                1, 0, 0, 0,
                0, Math.cos(r), -Math.sin(r), 0,
                0, Math.sin(r), Math.cos(r), 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnY(double r) {
        return new Matrix4x4(new double[] {
                Math.cos(r), 0, Math.sin(r), 0,
                0, 1, 0, 0,
                -Math.sin(r), 0, Math.cos(r), 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 rotationOnZ(double r) {
        return new Matrix4x4(new double[] {
                Math.cos(r), -Math.sin(r), 0, 0,
                Math.sin(r), Math.cos(r), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 shearing(List<Double> a) {
        assert a.size() == 6;
        return shearing(a.get(0), a.get(1), a.get(2), a.get(3), a.get(4), a.get(5));
    }
    public static Matrix4x4 shearing(double xInProportionToY, double xInProportionToZ, double yInProportionToX,
                                     double yInProportionToZ, double zInProportionToX, double zInProportionToY) {
        return new Matrix4x4(new double[] {
                1, xInProportionToY, xInProportionToZ, 0,
                yInProportionToX, 1, yInProportionToZ, 0,
                zInProportionToX, zInProportionToY, 1, 0,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 viewTransform(Tuple from, Tuple to, Tuple up) {
        assert from.isPoint() && to.isPoint() && up.isVector();
        var forward = to.subtract(from).normalize();
        var upn = up.normalize();
        var left = forward.cross(upn);
        var trueUp = left.cross(forward);
        var orientation = new Matrix4x4(new double[]{
                left.x(),     left.y(),     left.z(), 0,
                trueUp.x(),   trueUp.y(),   trueUp.z(), 0,
                -forward.x(), -forward.y(), -forward.z(), 0,
                0,            0,            0, 1
        });
        return orientation.multiply(Transform.translation(-from.x(), -from.y(), -from.z()));
    }
}
