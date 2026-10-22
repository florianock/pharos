import io.zocalo.kernel.Camera;
import io.zocalo.scene.Material;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;
import io.zocalo.scene.shapes.Cube;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

import static io.zocalo.Utils.*;

void main() throws IOException {

    var floor = new Plane(Matrix4x4.identity(),
            new Material.Builder()
                    .color(new Color(1, 1, 1))
                    .ambient(0.025)
                    .diffuse(0.67)
                    .specular(0)
                    .build()
    );

    var right = new Sphere(
            Matrix4x4.translation(0.5, 0.5, 0)
                    .multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
            new Material.Builder()
                    .color(new Color(1, 0, 0))
                    .ambient(0.1)
                    .specular(0)
                    .diffuse(0.6)
                    .reflective(0.3)
                    .build()
    );

    var left = new Sphere(
            Matrix4x4.translation(-0.25, 0.33, 0)
                    .multiply(Matrix4x4.scaling(0.33, 0.33, 0.33)),
            new Material.Builder()
                    .color(new Color(0.5, 0.5, 1))
                    .ambient(0.1)
                    .specular(0)
                    .diffuse(0.6)
                    .reflective(0.3)
                    .build()
    );

    var lightCube = new Cube(
            Matrix4x4.translation(0, 3, 4).multiply(Matrix4x4.scaling(1, 1, 0.01)),
            new Material.Builder().color(1.5, 1.5, 1.5).ambient(1).diffuse(0).specular(0).build(),
            false
    );

    var objects = new Shape[] { lightCube, floor, right, left };
    var light = Light.areaLight(Tuple.point(-1, 2, 4), Tuple.vector(2, 0, 0), 10, Tuple.vector(0, 2, 0), 10, true, new Color(1.5, 1.5, 1.5));
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(3840, 2160, PI / 4, Tuple.viewTransform(
            Tuple.point(-3,   1, 2.5),
            Tuple.point( 0, 0.5,   0),
            Tuple.vector(0,   1,   0)
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}