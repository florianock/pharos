import io.zocalo.kernel.Camera;
import io.zocalo.scene.Material;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;
import io.zocalo.scene.patterns.*;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

import static io.zocalo.Utils.*;

void main() throws IOException {

    var floor = new Plane(Matrix4x4.identity(),
            new Material.Builder()
                    .color(new Color(0.2, 0.2, 0.2))
                    .specular(0)
                    .build()
    );

    var middle = new Sphere(
            Matrix4x4.translation(-0.5f, 1, 0.5f),
            new Material.Builder()
                    .color(new Color(0.1f, 1, 0.5))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var right = new Sphere(
            Matrix4x4.translation(1.5f, 0.5f, -0.5f)
                    .multiply(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)),
            new Material.Builder()
                    .color(new Color(0.5f, 1, 0.1f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var left = new Sphere(
            Matrix4x4.translation(-1.5f, 0.33f, -0.75f)
                    .multiply(Matrix4x4.scaling(0.33f, 0.33f, 0.33f)),
            new Material.Builder()
                    .color(new Color(1, 0.8f, 0.1f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var objects = new Shape[] { floor, middle, right, left };
    var light = Light.pointLight(Tuple.point(-10, 10, -10), new Color(0.8, 0.8, 0.8));
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(1280, 1024, PI / 3, Tuple.point(0, 1.5, -5).viewTransform(
           Tuple.point(0,    1,  0),
           Tuple.vector(0,    1,  0)
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}