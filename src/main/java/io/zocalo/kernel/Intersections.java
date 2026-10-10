package io.zocalo.kernel;

import java.util.*;

// TODO make this behave like a collection or something
public class Intersections {

    private final ArrayList<Intersection> hits;

    public Intersections(List<Intersection> is) {
        this.hits = new ArrayList<>(is);
    }

    public Intersections(Intersection... is) {
        this(new ArrayList<>(Arrays.asList(is)));
    }

    public Intersections add(Intersection i) {
        hits.add(i);
        return this;
    }

    public Intersections addAll(Intersection... is) {
        hits.addAll(List.of(is));
        return this;
    }

    public int count() {
        return hits.size();
    }

    public ArrayList<Intersection> intersections() {
        return hits;
    }

    public Intersection get(int index) {
        if (index < 0 || hits.size() <= index) throw new IndexOutOfBoundsException();

        hits.sort(Comparator.comparingDouble(Intersection::t));
        return hits.get(index);
    }

    public Optional<Intersection> hit() {
        hits.sort(Comparator.comparingDouble(Intersection::t));
        for (var hit : hits) {
            if (hit.t() > 0) return Optional.of(hit);
        }
        return Optional.empty();
    }
}