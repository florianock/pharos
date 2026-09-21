package io.zocalo;

import java.util.*;

public class IntersectionList {

    private final ArrayList<Intersection> intersections;

    public IntersectionList(List<Intersection> is) {
        this.intersections = new ArrayList<>(is);
    }

    public IntersectionList(Intersection... is) {
        this(new ArrayList<>(Arrays.asList(is)));
    }

    public IntersectionList add(Intersection i) {
        intersections.add(i);
        return this;
    }

    public IntersectionList addAll(Intersection... is) {
        intersections.addAll(List.of(is));
        return this;
    }

    public int count() {
        return intersections.size();
    }

    public Intersection get(int index) {
        if (index < 0 || intersections.size() <= index) throw new IndexOutOfBoundsException();

        intersections.sort(Comparator.comparingDouble(Intersection::t));
        return intersections.get(index);
    }

    public Optional<Intersection> getFirst() {
        if (intersections.isEmpty()) return Optional.empty();

        intersections.sort(Comparator.comparingDouble(Intersection::t));
        return Optional.of(intersections.getFirst());
    }

    public Optional<Intersection> hit() {
        intersections.sort(Comparator.comparingDouble(Intersection::t));
        for (var i : intersections) {
            if (i.t() > 0) return Optional.of(i);
        }
        return Optional.empty();
    }
}