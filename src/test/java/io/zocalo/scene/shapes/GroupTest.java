package io.zocalo.scene.shapes;

import io.zocalo.base.Ray;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.TestShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void creatingANewGroup() {
        var g = new Group();
        assertEquals(Transform.identity(), g.getTransform());
        assertTrue(g.isEmpty());
        assertEquals(0, g.count());
    }

    @Test
    void aShapeHasAParentAttribute() {
        var s = new TestShape();
        assertNull(s.getParent());
    }

    @Test
    void addingAChildToAGroup() {
        var s = new TestShape();
        var g = new Group.Builder().add(s).build();
        assertFalse(g.isEmpty());
        assertEquals(1, g.count());
        assertTrue(g.includes(s));
        assertEquals(g, s.getParent());
    }

    @Test
    void intersectingARayWithAnEmptyGroup() {
        var g = new Group();
        var r = new Ray(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
        var xs = g.localIntersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void intersectingARayWithANonEmptyGroup() {
        var s1 = new Sphere();
        var s2 = new Sphere(Transform.translation(0, 0, -3));
        var s3 = new Sphere(Transform.translation(5, 0, 0));
        var g = new Group.Builder().add(s1).add(s2).add(s3).build();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var xs = g.localIntersect(r);
        assertEquals(4, xs.count());
        assertEquals(xs.get(0).object(), s2);
        assertEquals(xs.get(1).object(), s2);
        assertEquals(xs.get(2).object(), s1);
        assertEquals(xs.get(3).object(), s1);
    }

    @Test
    void intersectingATransformedGroup() {
        var s1 = new Sphere(Transform.translation(5, 0, 0));
        var g = new Group.Builder().transform(Transform.scaling(2, 2, 2)).add(s1).build();
        var r = new Ray(Tuple.point(10, 0, -10), Tuple.vector(0, 0, 1));
        var xs = g.localIntersect(r);
        assertEquals(2, xs.count());
    }
}