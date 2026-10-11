import io.zocalo.kernel.Camera;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.World;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

import static io.zocalo.Utils.*;

void main() throws IOException {
    var subpat = new Checkers(new Solid(0.65, 0.2, 0.65), new Solid(0.75, 0.25, 0.75),
            Matrix4x4.scaling(0.25, 0.25, 0.25));
    var pattern = new Checkers(subpat, new Solid(0.9, 0.9, 1.0), Matrix4x4.scaling(0.5, 0.5, 0.5));

    var floorMaterial = new Material.Builder().pattern(pattern).specular(0).build();
    var floor = new Plane(floorMaterial);

    var lwtx = Matrix4x4.translation(0, 0, 5);
    var lwry = Matrix4x4.rotationOnY(-QTR_PI);
    var lwrx = Matrix4x4.rotationOnX(HALF_PI);
    var leftWall = new Plane(lwtx.multiply(lwry).multiply(lwrx), floorMaterial);

    var rwtx = Matrix4x4.translation(0, 0, 5);
    var rwry = Matrix4x4.rotationOnY(QTR_PI);
    var rwrx = Matrix4x4.rotationOnX(HALF_PI);
    var rightWall = new Plane(rwtx.multiply(rwry).multiply(rwrx), floorMaterial);

    var mtx = Matrix4x4.rotationOnX(-PI / 3).multiply(Matrix4x4.scaling(0.2, 0.2, 0.2));
    var ring = new Ring(new Solid(0, 0.3, 0.6), new Solid(0.1, 1, 0.8), mtx);
    var middle = new Sphere(Matrix4x4.translation(-0.5, 1, 0.5), new Material.Builder()
            .pattern(ring)
            .diffuse(0.7)
            .specular(0.3)
            .build());

    var rptx = Matrix4x4.rotationOnZ(-QTR_PI).multiply(Matrix4x4.scaling(.2, .2, .2));
    var stripe = new Stripe(new Solid(0, .3, .6), new Solid(.1, 1, .8), rptx);
    var rtx = Matrix4x4.translation(1.5, .5, -.5).multiply(Matrix4x4.scaling(.5, .5, .5));
    var right = new Sphere(rtx, new Material.Builder().pattern(stripe).diffuse(.7).specular(.3).build());

    var lptx = Matrix4x4.translation(-1, 0, 0).multiply(Matrix4x4.scaling(2, 2, 2));
    var gradient = new Gradient(new Solid(1, 0, 0), new Solid(0, 1, 1), lptx);
    var ltx = Matrix4x4.translation(-1.5, .33, -.75).multiply(Matrix4x4.scaling(.33, .33, .33));
    var left = new Sphere(ltx, new Material.Builder().pattern(gradient).diffuse(.7).specular(.3).build());

    var light = Light.pointLight(Tuple.point(-10, 10, -10), Color.WHITE);
    var shapes = new Shape[] { floor, leftWall, rightWall, left, middle, right };
    var w = new World(shapes, new Light[] {light});

    var view = Tuple.point(0, 1.5, -5).viewTransform(Tuple.point(0, 1, 0), Tuple.vector(0, 1, 0));
    var camera = new Camera(1280, 1024, PI / 3, view);

    var canvas = camera.renderParallel(w);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh10.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh10.ppm"));
}