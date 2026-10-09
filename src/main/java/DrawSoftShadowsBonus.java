import io.zocalo.kernel.Camera;
import io.zocalo.scene.Material;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;

import static io.zocalo.Utils.*;

void main() throws IOException {

    var floor = new Plane(Matrix4x4.identity(),
            new Material.Builder()
                    .color(Color.create(1, 1, 1))
                    .ambient(0.025)
                    .diffuse(0.67)
                    .specular(0)
                    .build()
    );

    var right = new Sphere(
            Matrix4x4.translation(0.5, 0.5, 0)
                    .multiply(Matrix4x4.scaling(0.5, 0.5, 0.5)),
            new Material.Builder()
                    .color(Color.create(1, 0, 0))
                    .ambient(0.1)
                    .specular(0)
                    .diffuse(0.6)
                    // .reflective(0.3)
                    .build()
    );

    var left = new Sphere(
            Matrix4x4.translation(-0.25, 0.33, 0)
                    .multiply(Matrix4x4.scaling(0.33, 0.33, 0.33)),
            new Material.Builder()
                    .color(Color.create(0.5, 0.5, 1))
                    .ambient(0.1)
                    .specular(0)
                    .diffuse(0.6)
                    // .reflective(0.3)
                    .build()
    );

    // TODO add cube for light reflection

    var objects = new Shape[] { floor, right, left };
    var light = Light.areaLight(Tuple.point(-1, 2, 4), Tuple.point(2, 0, 0), 10, Tuple.point(0, 2, 0), 10, true, Color.create(1.5, 1.5, 1.5));
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(1000, 500, PI / 4, Tuple.viewTransform(
            new double[] { -3,   1, 2.5, 1 },
            new double[] {  0, 0.5,   0, 1 },
            new double[] {  0,   1,   0, 0 }
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}