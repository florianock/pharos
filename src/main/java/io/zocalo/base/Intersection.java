package io.zocalo.base;

import io.zocalo.scene.shapes.Shape;

import java.util.ArrayList;

import static io.zocalo.Utils.SHADOW_OFFSET;

public record Intersection(double t, Shape object) {

    public Computations prepareComputations(Ray r, Intersections xs) {
        var point = r.position(t);
        var eyev = r.direction().negate();
        var normalv = object.normalAt(point);
        var inside = false;

        if (normalv.dot(eyev) < 0) {
            inside = true;
            normalv = normalv.negate();
        }

        var reflectv = r.direction().reflect(normalv);
        Tuple offsetNormal = normalv.multiply(SHADOW_OFFSET);
        var overPoint = point.add(offsetNormal);
        var underPoint = point.subtract(offsetNormal);

        var refractiveIndices = getRefractiveIndices(xs);

        return new Computations(t, object, point, eyev, normalv, reflectv, inside, overPoint, underPoint,
                refractiveIndices.n1(), refractiveIndices.n2());
    }

    private RefractiveIndicesResult getRefractiveIndices(Intersections xs) {
        double n1 = 1, n2 = 1;
        var containers = new ArrayList<Shape>(1 + xs.count() / 2);
        for (var i : xs.intersections()) {
            if (i.equals(this) && !containers.isEmpty()) {
                n1 = containers.getLast().getRefractiveIndex();
            }
            if (containers.contains(i.object)) {
                containers.remove(i.object);
            } else {
                containers.add(i.object);
            }
            if (i.equals(this)) {
                if (!containers.isEmpty())
                    n2 = containers.getLast().getRefractiveIndex();
                break;
            }
        }
        return new RefractiveIndicesResult(n1, n2);
    }

    private record RefractiveIndicesResult(double n1, double n2) {
    }
}