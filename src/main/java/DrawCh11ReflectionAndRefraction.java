import io.zocalo.base.Camera;
import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Tuple;
import io.zocalo.scene.*;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

import static io.zocalo.scene.MaterialConstants.*;

void main() throws IOException {
    var result1 = drawScene(3840, 2160);
    var sceneCanvas = result1.camera().renderParallel(result1.world());
    sceneCanvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    sceneCanvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));

    var result2 = drawAirBubble(3840, 2160);
    var bubbleCanvas = result2.camera().renderParallel(result2.world());
    bubbleCanvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    bubbleCanvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}

SceneSetup drawScene(int h, int v) {
    var c1 = new Solid(0.45, 0.45, 0.45);
    var c2 = new Solid(0.55, 0.55, 0.55);
    var transform = Matrix4x4.rotationOnY(1.5708).multiply(Matrix4x4.scaling(0.25, 0.25, 0.25));
    var p = new Stripe(c1, c2, transform);
    var wallMaterial = new Material.Builder().pattern(p).ambient(0).diffuse(0.4).specular(0).reflective(0.3).build();

    var checkers = new Material.Builder()
            .pattern(new Checkers(new Solid(0.35, 0.35, 0.35), new Solid(0.65, 0.65, 0.65)))
            .specular(0)
            .reflective(0.4)
            .build();
    var floor = new Plane(Matrix4x4.rotationOnY(0.31415), checkers);

    var ceiling = new Plane(Matrix4x4.translation(0, 5, 0), new Material.Builder()
            .color(0.8, 0.8, 0.8)
            .ambient(0.3)
            .specular(0)
            .build());

    var westWall = new Plane(Matrix4x4.translation(-5, 0, 0)
            .multiply(Matrix4x4.rotationOnZ(1.5708))
            .multiply(Matrix4x4.rotationOnY(1.5708)),
            wallMaterial);

    var eastWall = new Plane(Matrix4x4.translation(5, 0, 0)
            .multiply(Matrix4x4.rotationOnZ(1.5708))
            .multiply(Matrix4x4.rotationOnY(1.5708)),
            wallMaterial);

    var northWall = new Plane(Matrix4x4.translation(0, 0, 5)
            .multiply(Matrix4x4.rotationOnX(1.5708)),
            wallMaterial);

    var southWall = new Plane(Matrix4x4.translation(0, 0, -5)
            .multiply(Matrix4x4.rotationOnX(1.5708)),
            wallMaterial);

    var bgBall1 = new Sphere(Matrix4x4.translation(4.6, 0.4, 1).multiply(Matrix4x4.scaling(0.4, 0.4, 0.4)),
            new Material.Builder().color(0.8, 0.5, 0.3).shininess(50).build());
    var bgBall2 = new Sphere(Matrix4x4.translation(4.7, 0.3, 0.4).multiply(Matrix4x4.scaling(0.3, 0.3, 0.3)),
            new Material.Builder().color(0.9, 0.4, 0.5).shininess(50).build());
    var bgBall3 = new Sphere(Matrix4x4.translation(-1, 0.5, 4.5).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
            new Material.Builder().color(0.4, 0.9, 0.6).shininess(50).build());
    var bgBall4 = new Sphere(Matrix4x4.translation(-1.7, 0.3, 4.7).multiply(Matrix4x4.scaling(0.3, 0.3, 0.3)),
            new Material.Builder().color(0.4, 0.6, 0.9).shininess(50).build());

    var redSphere = new Sphere(Matrix4x4.translation(-0.6, 1, 0.6),
            new Material.Builder().color(1, 0.3, 0.2).specular(0.4).shininess(5).build());
    var blueGlassSphere = new Sphere(Matrix4x4.translation(0.6, 0.7, -0.6).multiply(Matrix4x4.scaling(0.7, 0.7, 0.7)),
            new Material.Builder().color(0, 0, 0.2).ambient(0).diffuse(0.4).specular(0.9).shininess(300)
                    .reflective(0.9).transparency(0.9).refractiveIndex(GLASS.refractiveIndex()).build());
    var greenGlassSphere = new Sphere(Matrix4x4.translation(-0.7, 0.5, -0.8).multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
            new Material.Builder().color(0, 0.2, 0).ambient(0).diffuse(0.4).specular(0.9).shininess(300)
                    .reflective(0.9).transparency(0.9).refractiveIndex(GLASS.refractiveIndex()).build());

    var light = Light.pointLight(Tuple.point(-4.9, 4.9, -1), new Color(1, 1, 1));
    var shapes = new Shape[] { floor, ceiling, westWall, eastWall, northWall, southWall,
            bgBall1, bgBall2, bgBall3, bgBall4,
            redSphere, blueGlassSphere, greenGlassSphere
    };
    var world = new World(shapes, new Light[]{light});
    var view = Tuple.viewTransform(
            Tuple.point(-2.6, 1.5, -3.9),
            Tuple.point(-0.6, 1, -0.8),
            Tuple.vector(0, 1, 0)
    );
    return new SceneSetup(new Camera(h, v, 1.15, view), world);
}

SceneSetup drawAirBubble(int h, int v) {
    var wall = new Plane(Matrix4x4.translation(0, 0, 10).multiply(Matrix4x4.rotationOnX(1.5708)),
            new Material.Builder()
                    .pattern(new Checkers(new Solid(0.15, 0.15, 0.15), new Solid(0.85, 0.85, 0.85)))
                    .ambient(0.8).diffuse(0.2).specular(0).build());
    var glassBall = new Sphere(new Material.Builder().ambient(0).diffuse(0).specular(0.9)
            .shininess(300).reflective(0.9).transparency(0.9).refractiveIndex(GLASS.refractiveIndex()).build());
    var hollowCenter = new Sphere(Matrix4x4.scaling(0.5, 0.5, 0.5),
            new Material.Builder().ambient(0).diffuse(0).specular(0.9).shininess(300)
                    .reflective(0.9).transparency(0.9).refractiveIndex(AIR.refractiveIndex()).build());
    var light = Light.pointLight(Tuple.point(-20, 80, -20), new Color(1, 1, 1));
    var shapes = new Shape[] { wall, glassBall, hollowCenter };
    var world = new World(shapes, new Light[]{light});
    var view = Tuple.viewTransform(
            Tuple.point(0, 0, -5),
            Tuple.point(0, 0, 0),
            Tuple.vector(0, 1, 0)
    );
    return new SceneSetup(new Camera(h, v, 0.45, view), world);
}

record SceneSetup(Camera camera, World world){}