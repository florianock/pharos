import io.zocalo.base.Camera;
import io.zocalo.base.Matrix4x4;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.World;
import io.zocalo.scene.shapes.*;

import static io.zocalo.Utils.*;

void main() throws IOException {
    drawHexagon();
    drawLotsOfObjects();
    drawCubeOfSpheres();
}

private void drawCubeOfSpheres() throws IOException {
    var background = new Plane(Transform.rotationOnX(-QTR_PI).translate(0, 0, 250), new Material.Builder().color(Color.WHITE).build());
    var groups = new ArrayList<Shape>(8);
    for (var x = -1; x < 1; x++) {
        for (var y = -1; y < 1; y++) {
            for (var z = -1; z < 1; z++) {
                var spheres = getSpheres(x, y, z);
                var g = new Group.Builder().transform(Transform.translation(x * 7.5, y * 7.5, z * 7.5)).add(spheres).build();
                groups.add(g);
            }
        }
    }
    var box = new Group.Builder().transform(Transform.rotationOnY(2*PI)).add(groups).build();
    var light1 = Light.pointLight(Tuple.point(5, 9, -150), Color.WHITE);
    var light2 = Light.pointLight(Tuple.point(-50, 25, 30), Color.WHITE);
    var world = new World(List.of(background, box), List.of(light1, light2));
    var view = Transform.viewTransform(
            Tuple.point(-26, 8, -35),
            Tuple.point(0, -7, 0),
            Tuple.vector(0, 1, 0)
    );
    var camera = new Camera(1200, 600, THIRD_PI, view);
    var canvas = camera.renderParallel(world, Color.WHITE, true);
//    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14CHQ.ppm"));
//    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14CHQ.png"));
}

private Collection<Shape> getSpheres(int rBlock, int gBlock, int bBlock) {
    var redHalf = (rBlock + 1) * 0.5;
    var greenHalf = (gBlock + 1) * 0.5;
    var blueHalf = (bBlock + 1) * 0.5;
    var spheres = new ArrayList<Shape>(25);
    for (var x = -3d; x < 5; x+=1.5) {
        for (var y = -3d; y < 5; y+=1.5) {
            for (var z =  -3d; z < 5; z+=1.5) {
                var color = new Color(redHalf+((x+3)/14), greenHalf+((y+3)/14), blueHalf+((z+3)/14));
                var s = new Sphere(Transform.scaling(0.5, 0.5, 0.5).translate(x, y, z),
                        new Material.Builder().color(color).build());
                spheres.add(s);
            }
        }
    }
    return spheres;
}

private void drawLotsOfObjects() throws IOException {
    var random = ThreadLocalRandom.current();
    var shapes = new ArrayList<Shape>(100);
    for (var x = 0; x < 10; x++) {
        for (var y = 0; y < 10; y++) {
            var spheres = new ArrayList<Shape>();
            for (var i = 0; i < 40; i++) {
                var s = new Sphere(
                        Transform.translation(random.nextDouble(0, 15), random.nextDouble(0, 10), random.nextDouble(0, 10)),
                        new Material.Builder().color(random.nextDouble(), random.nextDouble(), random.nextDouble()).build()
                );
                spheres.add(s);
            }
            var g = new Group.Builder().transform(Transform.translation(x * 15, y * 10, 0)).add(spheres).build();
            shapes.add(g);
        }
    }
    var light = Light.pointLight(Tuple.point(5, 9, -50), Color.WHITE);
    var world = new World(shapes, List.of(light));
    var view = Transform.viewTransform(
            Tuple.point(30, 4, -50),
            Tuple.point(60, 35, 14),
            Tuple.vector(0, 1, 0)
    );
    var camera = new Camera(1200, 600, 2.0, view);
    var canvas = camera.renderParallel(world);
//    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14BHQ.ppm"));
//    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14BHQ.png"));
}

private void drawHexagon() throws IOException {
    var light = Light.pointLight(Tuple.point(5, 18, -9), Color.WHITE);
    var world = new World(new Shape[]{hexagon()}, new Light[]{light});
    var view = Transform.viewTransform(
            Tuple.point(2, 4, -9),
            ORIGIN,
            Tuple.vector(0, 1, 0)
    );
    var camera = new Camera(1200, 600, 0.3, view);
    var canvas = camera.renderParallel(world);
//    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14AHQ.ppm"));
//    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14AHQ.png"));
}

private Shape hexagon() {
    var material = new Material.Builder().color(1, 0.2, 0.4).diffuse(0.8).specular(0.6).ambient(0).shininess(50).reflective(0.3).build();
    var sides = new ArrayList<Shape>(6);
    for (var n = 0; n < 6; n++) {
        var side = hexagonSide(Transform.rotationOnY(n * THIRD_PI));
        sides.add(side);
    }
    return new Group.Builder().add(sides).material(material).build();
}

private Shape hexagonSide(Matrix4x4 transform) {
    return new Group.Builder()
            .transform(transform)
            .add(hexagonCorner())
            .add(hexagonEdge())
            .build();
}

private Shape hexagonCorner() {
    return new Sphere(Transform.scaling(0.25, 0.25, 0.25).translate(0, 0, -1));
}

private Shape hexagonEdge() {
    return new Cylinder(
            Transform.scaling(0.25, 1, 0.25)
                    .rotateOnZ(-HALF_PI)
                    .rotateOnY(-SIXTH_PI)
                    .translate(0, 0, -1),
            Material.create(), 0, 1
    );
}