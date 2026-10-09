import io.zocalo.kernel.Camera;
import io.zocalo.scene.Material;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;

import static io.zocalo.Utils.*;

void main() throws IOException {

    var floor = new Sphere(Matrix4x4.scaling(10,0.01f, 10),
            new Material.Builder()
                    .color(Color.create(1, 0.9f, 0.9f))
                    .specular(0)
                    .build()
    );

    var left_wall = new Sphere(
            Matrix4x4.translation(0, 0, 5)
                    .multiply(Matrix4x4.rotationOnY(-QTR_PI))
                    .multiply(Matrix4x4.rotationOnX(HALF_PI))
                    .multiply(Matrix4x4.scaling(10, 0.01f, 10)),
            floor.getMaterial()
    );

    var right_wall = new Sphere(
            Matrix4x4.translation(0, 0, 5)
                    .multiply(Matrix4x4.rotationOnY(QTR_PI))
                    .multiply(Matrix4x4.rotationOnX(HALF_PI))
                    .multiply(Matrix4x4.scaling(10, 0.01f, 10)),
            floor.getMaterial()
    );

    var middle = new Sphere(
            Matrix4x4.translation(-0.5f, 1, 0.5f),
            new Material.Builder()
                    .color(Color.create(0.1f, 1, 0.5f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var right = new Sphere(
            Matrix4x4.translation(1.5f, 0.5f, -0.5f)
                    .multiply(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)),
            new Material.Builder()
                    .color(Color.create(0.5f, 1, 0.1f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var left = new Sphere(
            Matrix4x4.translation(-1.5f, 0.33f, -0.75f)
                    .multiply(Matrix4x4.scaling(0.33f, 0.33f, 0.33f)),
            new Material.Builder()
                    .color(Color.create(1, 0.8f, 0.1f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var objects = new Shape[] { floor, left_wall, right_wall, middle, right, left };
    var light = Light.pointLight(Tuple.point(-10, 10, -10), Color.WHITE);
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(1280, 1024, PI / 3, Tuple.point(0, 1.5f, -5).viewTransform(
            Tuple.point(0,    1,  0),
            Tuple.vector(0,    1,  0)
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}