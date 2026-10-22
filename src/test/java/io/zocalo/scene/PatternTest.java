package io.zocalo.scene;

import io.zocalo.base.Camera;
import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.IOException;
import java.nio.file.Paths;

import static io.zocalo.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class PatternTest {

    private static final boolean RUN_VISUALS = false;

    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private static final Color GREY = Color.GREY;

    @Test
    void creatingAStripePattern() {
        var pattern = new Stripe();
        assertEquals(new Solid(WHITE), pattern.a);
        assertEquals(new Solid(BLACK), pattern.b);
    }

    @Test
    void aStripePatternIsConstantInY() {
        var pattern = new Stripe();

        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 1, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 2, 0)));
    }

    @Test
    void aStripePatternIsConstantInZ() {
        var pattern = new Stripe();

        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 1)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 2)));
    }

    @Test
    void aStripePatternAlternatesInX() {
        var pattern = new Stripe();

        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.9, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(1, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(-0.1, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(-1, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(-1.1, 0, 0)));
    }

    @Test
    void stripesWithAnObjectTransformation() {
        var obj = new Sphere(Matrix4x4.scaling(2, 2, 2));
        var pattern = new Stripe();

        var c = pattern.patternAtObject(obj, Tuple.point(1.5, 0, 0));

        assertEquals(WHITE, c);
    }

    @Test
    void theDefaultPatternTransformation() {
        var pattern = new TestPattern();
        assertEquals(Matrix4x4.identity(), pattern.transform);
    }

    @Test
    void assigningATransformation() {
        var pattern = new TestPattern(Matrix4x4.translation(1, 2, 3));
        assertEquals(Matrix4x4.translation(1, 2, 3), pattern.transform);
    }

    @Test
    void aPatternWithAnObjectTransformation() {
        var shape = new Sphere(Matrix4x4.scaling(2, 2, 2));
        var pattern = new TestPattern();
        var c = pattern.patternAtObject(shape, Tuple.point(2, 3, 4));
        assertEquals(new Color(1, 1.5, 2), c);
    }

    @Test
    void aPatternWithAPatternTransformation() {
        var shape = new Sphere();
        var pattern = new TestPattern(Matrix4x4.scaling(2, 2, 2));
        var c = pattern.patternAtObject(shape, Tuple.point(2, 3, 4));
        assertEquals(new Color(1, 1.5, 2), c);
    }

    @Test
    void aPatternWithBothAnObjectAndAPatternTransformation() {
        var shape = new Sphere(Matrix4x4.scaling(2, 2, 2));
        var pattern = new TestPattern(Matrix4x4.translation(0.5, 1, 1.5));
        var c = pattern.patternAtObject(shape, Tuple.point(2.5, 3, 3.5));
        assertEquals(new Color(0.75, 0.5, 0.25), c);
    }

    @Test
    void aGradientLinearlyInterpolatesBetweenColors() {
        var pattern = new Gradient();
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(new Color(0.75, 0.75, 0.75), pattern.patternAt(Tuple.point(0.25, 0, 0)));
        assertEquals(new Color(0.5, 0.5, 0.5), pattern.patternAt(Tuple.point(0.5, 0, 0)));
        assertEquals(new Color(0.25, 0.25, 0.25), pattern.patternAt(Tuple.point(0.75, 0, 0)));
    }

    @Test
    void aRingPatternShouldExtendInBothXAndZ() {
        var pattern = new Ring();
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(1, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(0, 0, 1)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(0.708, 0, 0.708)));
    }

    @Test
    void checkersShouldRepeatInX() {
        var pattern = new Checkers();
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.99, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(1.01, 0, 0)));
    }

    @Test
    void checkersShouldRepeatInY() {
        var pattern = new Checkers();
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0.99, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(0, 1.01, 0)));
    }

    @Test
    void checkersShouldRepeatInZ() {
        var pattern = new Checkers();
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0, 0, 0.99)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(0, 0, 1.01)));
    }

    @Test
    void perturbShouldJitterPoint() {
        var pattern = new Perturbed(new Stripe());
        assertEquals(BLACK, pattern.patternAt(Tuple.point(3.1, 6.2, 2.3)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.99, 0, 0)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(1.01, 0, 0)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(13.1, 26.2, 32.3)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.1, 7.2, 5.3)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(12.1, 26.2, 2.3)));
    }

    @Test
    void blendedShouldAverageTwoPatterns() {
        var pattern = new Blended(new Stripe(), new Stripe(Matrix4x4.rotationOnY(HALF_PI)));
        assertEquals(GREY, pattern.patternAt(Tuple.point(-0.99, 0, -0.99)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.99, 0, -0.99)));
        assertEquals(BLACK, pattern.patternAt(Tuple.point(1.01, 0, 0.1)));
        assertEquals(GREY, pattern.patternAt(Tuple.point(1.01, 0, -0.1)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(-1.5, 0, -0.5)));
        assertEquals(WHITE, pattern.patternAt(Tuple.point(0.99, 0, -2.1)));
    }

    /*
        BONUS tests that visualize the patterns.
        Source: https://github.com/georgelecakes/TheRayTracerChallenge/blob/master/UnitTesting/Chapter10Test.cs
    */

    @Test
    @EnabledIf("runVisuals")
    void BasicScene() throws IOException, InterruptedException {
        var light1 = Light.pointLight(Tuple.point(-5, 5, -5), Color.WHITE);
        var light2 = Light.pointLight(Tuple.point(5, 0.4, -5), new Color(0.6, 0.6, 0.15));
        var floor = new Plane(new Material.Builder()
                .color(new Color(1, 0, 0))
//                .pattern(new Blended(new Stripe(), new Stripe(Matrix4x4.rotationOnY(HALF_PI))))
                .pattern(new Ring(new Solid(Color.RED), new Solid(Color.GREEN)))
                .build());

        var wall = new Plane(
                Matrix4x4.rotationOnX(HALF_PI).multiply(Matrix4x4.translation(0, 0, 4)),
                new Material.Builder()
                        .color(new Color(0, 0, 1))
                        .pattern(new Perturbed(new Stripe(new Solid(Color.WHITE), new Solid(Color.BLACK)), 0.5))
                        .build());

        var sphere1 = new Sphere(
                Matrix4x4.translation(0, 0.5, -3).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
                new Material.Builder()
                        .pattern(new Checkers(new Solid(Color.YELLOW), new Solid(Color.BLUE),
                                Matrix4x4.translation(0, 0.5, 0.5).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5))))
                        .build());

        var sphere2 = new Sphere(
                Matrix4x4.translation(2, 1, -1),
                new Material.Builder()
                        .pattern(new Gradient(new Solid(Color.WHITE), new Solid(Color.GREEN),
                                Matrix4x4.translation(1, 0, 0).multiply(Matrix4x4.scaling(2, 2, 2))))
                        .build());

        var world = new World(new Shape[] { floor, wall, sphere1, sphere2 }, new Light[] { light1, light2 });

        visualize(world, "ch10BasicScene");
    }

    @Test
    @EnabledIf("runVisuals")
    void radialGradientPatternsScene() throws IOException, InterruptedException {
        var pattern = new RadialGradient();
        var light1 = Light.pointLight(Tuple.point(-5, 5, -5), Color.WHITE);
        var light2 = Light.pointLight(Tuple.point(5, 0.4, -5), new Color(0.6, 0.6, 0.15));
        var floor = new Plane(new Material.Builder()
                .pattern(pattern)
                .build());

        var wall = new Plane(
                Matrix4x4.rotationOnX(HALF_PI).multiply(Matrix4x4.translation(0, 0, 4)),
                new Material.Builder()
                        .color(new Color(0, 0, 1))
                        .pattern(pattern)
                        .build());

        var sphere1 = new Sphere(
                Matrix4x4.translation(0, 0.5, -3).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
                new Material.Builder()
                        .pattern(new RadialGradient(Matrix4x4.rotationOnY(QTR_PI)))
                        .build());

        var sphere2 = new Sphere(
                Matrix4x4.translation(2, 1, -1),
                new Material.Builder()
                        .pattern(new RadialGradient(Matrix4x4.rotationOnX(QTR_PI).multiply(Matrix4x4.rotationOnZ(-QTR_PI)).multiply(Matrix4x4.scaling(.25, .25, .25))))
                        .build());

        var world = new World(new Shape[] { floor, wall, sphere1, sphere2 }, new Light[] { light1, light2 });

        visualize(world, "ch10RadialGradientPatternsScene");
    }

    @Test
    @EnabledIf("runVisuals")
    void nestedPatternsScene() throws IOException, InterruptedException {
        var c1 = new Checkers();
        var c2 = new Checkers(new Solid(Color.RED), new Solid(Color.GREEN));
        var checkerboard = new Ring(c1, c2);

        var checkerboard2 = new Ring(c1, c2, Matrix4x4.rotationOnY(QTR_PI).multiply(Matrix4x4.scaling(.3, .3, .3)));

        var light = Light.pointLight(Tuple.point(-5, 5, -5), Color.WHITE);
        var floor = new Plane(new Material.Builder()
                .pattern(checkerboard)
                .build());

        var wall = new Plane(
                Matrix4x4.rotationOnX(HALF_PI).multiply(Matrix4x4.translation(0, 0, 4.1)),
                new Material.Builder()
                        .pattern(checkerboard)
                        .build());

        var sphere1 = new Sphere(
                Matrix4x4.translation(0, 0.5, -3).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
                new Material.Builder()
                        .pattern(checkerboard2)
                        .build());

        var sphere2 = new Sphere(
                Matrix4x4.translation(2, 1, -1),
                new Material.Builder()
                        .pattern(checkerboard2)
                        .build());

        var world = new World(new Shape[] { floor, wall, sphere1, sphere2 }, new Light[] { light });

        visualize(world, "ch10NestedPatternsScene");
    }

    @Test
    @EnabledIf("runVisuals")
    void blendPatternsScene() throws IOException, InterruptedException {
        var s1 = new Stripe(new Solid(Color.WHITE), new Solid(Color.GREEN), Matrix4x4.rotationOnY(HALF_PI));
        var s2 = new Stripe(new Solid(Color.WHITE), new Solid(Color.GREEN));
        var blend = new Blended(s1, s2);

        var light = Light.pointLight(Tuple.point(-5, 5, -5), Color.WHITE);
        var floor = new Plane(new Material.Builder()
                .pattern(blend)
                .build());

        var wall = new Plane(
                Matrix4x4.rotationOnX(HALF_PI).multiply(Matrix4x4.translation(0, 0, 4.1)),
                new Material.Builder()
                        .pattern(blend)
                        .build());

        var sphere1 = new Sphere(
                Matrix4x4.translation(0, 0.5, -3).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
                new Material.Builder()
                        .pattern(blend)
                        .build());

        var sphere2 = new Sphere(
                Matrix4x4.translation(2, 1, -1),
                new Material.Builder()
                        .pattern(blend)
                        .build());

        var world = new World(new Shape[] { floor, wall, sphere1, sphere2 }, new Light[] { light });

        visualize(world, "ch10BlendPatternsScene");
    }

    private static void visualize(World world, String fileName) throws InterruptedException, IOException {
        var camera = new Camera(640, 480, PI / 3, Tuple.viewTransform(
                Tuple.point(0, 2, -10),
                Tuple.point(0, 2, 4),
                Tuple.vector(0, 1, 0)));

        var canvas = camera.renderParallel(world, true);
        canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/" + fileName + ".png"));
        Thread.sleep(2000);
    }

    private boolean runVisuals() {
        return RUN_VISUALS;
    }
}