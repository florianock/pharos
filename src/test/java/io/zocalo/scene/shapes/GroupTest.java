package io.zocalo.scene.shapes;

import io.zocalo.base.Ray;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Material;
import io.zocalo.scene.TestShape;
import org.junit.jupiter.api.Test;

import static io.zocalo.Utils.HALF_PI;
import static io.zocalo.Utils.ORIGIN;
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
        var xs = g.intersect(r);
        assertEquals(0, xs.count());
    }

    @Test
    void intersectingARayWithANonEmptyGroup() {
        var s1 = new Sphere();
        var s2 = new Sphere(Transform.translation(0, 0, -3));
        var s3 = new Sphere(Transform.translation(5, 0, 0));
        var g = new Group.Builder().add(s1).add(s2).add(s3).build();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var xs = g.intersect(r);
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
        var xs = g.intersect(r);
        assertEquals(2, xs.count());
    }

    @Test
    void convertingAPointFromWorldToObjectSpace() {
        var s = new Sphere(Transform.translation(5, 0, 0));
        var g2 = new Group.Builder().transform(Transform.scaling(2, 2, 2)).add(s).build();
        var _ = new Group.Builder().transform(Transform.rotationOnY(HALF_PI)).add(g2).build();
        var p = s.worldToObject(Tuple.point(-2, 0, -10));
        assertEquals(Tuple.point(0, 0, -1), p);
    }

    @Test
    void convertingANormalFromObjectToWorldSpace() {
        var s = new Sphere(Transform.translation(5, 0, 0));
        var g2 = new Group.Builder().transform(Transform.scaling(1, 2, 3)).add(s).build();
        var _ = new Group.Builder().transform(Transform.rotationOnY(HALF_PI)).add(g2).build();
        var n = s.normalToWorld(Tuple.vector(Math.sqrt(3)/3, Math.sqrt(3)/3, Math.sqrt(3)/3));
        assertEquals(Tuple.vector(0.2857, 0.4286, -0.8571), n);
    }

    @Test
    void findingTheNormalOnAChildObject() {
        var s = new Sphere(Transform.translation(5, 0, 0));
        var g2 = new Group.Builder().transform(Transform.scaling(1, 2, 3)).add(s).build();
        var _ = new Group.Builder().transform(Transform.rotationOnY(HALF_PI)).add(g2).build();
        var n = s.normalAt(Tuple.point(1.7321, 1.1547, -5.5774));
        assertEquals(Tuple.vector(0.2857, 0.4286, -0.8571), n);
    }

    @Test
    void findingTheNormalOfAGroupThrowsException() {
        var g = new Group();
        assertThrows(RuntimeException.class, () -> g.localNormalAt(ORIGIN));
    }

    @Test
    void aGroupHasABoundingBoxThatContainsItsChildren() {
        var s = new Sphere(Transform.scaling(2, 2, 2).translate(2, 5, -3));
        var c = new Cylinder(Transform.scaling(0.5, 1, 0.5).translate(-4, -1, 4),
                Material.create(), -2, 2, false);
        var shape = new Group.Builder().add(s).add(c).build();
        var box = shape.bounds();
        assertEquals(Tuple.point(-4.5, -3, -5), box.min());
        assertEquals(Tuple.point(4, 7, 4.5), box.max());
    }

    @Test
    void intersectingRayPlusGroupDoesntTestChildrenIfBoxIsMissed() {
        var child = new TestShape();
        var shape = new Group.Builder().add(child).build();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 1, 0));
        var _ = shape.intersect(r);
        assertNull(child.getSavedRay());
    }

    @Test
    void intersectingRayPlusGroupTestsChildrenIfBoxIsHit() {
        var child = new TestShape();
        var shape = new Group.Builder().add(child).build();
        var r = new Ray(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
        var _ = shape.intersect(r);
        assertNotNull(child.getSavedRay());
    }

//    @Test
//    void testBoundingBoxWithoutTranslations() {
//        var s = new Sphere();
//        var g = new Group.Builder().add(s).build();
//
//        var r1 = new Ray(Tuple.point(0, 0, -2), Tuple.vector(0, 0, 1));
//        assertTrue(g.intersectBounds(r1));
//        assertEquals(2, g.intersect(r1).count());
//
//        var r2 = new Ray(Tuple.point(0.9, 0.9, -2), Tuple.vector(0, 0, 1));
//        assertTrue(g.intersectBounds(r2));
//        assertEquals(0, g.intersect(r2).count());
//
//        var r3 = new Ray(Tuple.point(2, 2, -2), Tuple.vector(0, 0, 1));
//        assertFalse(g.intersectBounds(r3));
//        assertEquals(0, g.intersect(r3).count());
//
//    }
}