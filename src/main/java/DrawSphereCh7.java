import io.zocalo.Material;
import io.zocalo.Matrix4x4;
import io.zocalo.Tuple;
import io.zocalo.scene.*;

import static io.zocalo.Utils.PI;

void main() throws IOException {

    var floor = Shape.sphere(Matrix4x4.scaling(10,0.01f, 10),
            new Material.Builder()
                    .color(Color.create(1, 0.9f, 0.9f))
                    .specular(0)
                    .build()
    );

    var left_wall = Shape.sphere(
            Matrix4x4.translation(0, 0, 5)
                    .multiply(Matrix4x4.rotationOnY(-PI / 4))
                    .multiply(Matrix4x4.rotationOnX(PI / 2))
                    .multiply(Matrix4x4.scaling(10, 0.01f, 10)),
            floor.material()
    );

    var right_wall = Shape.sphere(
            Matrix4x4.translation(0, 0, 5)
                    .multiply(Matrix4x4.rotationOnY(PI / 4))
                    .multiply(Matrix4x4.rotationOnX(PI / 2))
                    .multiply(Matrix4x4.scaling(10, 0.01f, 10)),
            floor.material()
    );

    var middle = Shape.sphere(
            Matrix4x4.translation(-0.5f, 1, 0.5f),
            new Material.Builder()
                    .color(Color.create(0.1f, 1, 0.5f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var right = Shape.sphere(
            Matrix4x4.translation(1.5f, 0.5f, -0.5f)
                    .multiply(Matrix4x4.scaling(0.5f, 0.5f, 0.5f)),
            new Material.Builder()
                    .color(Color.create(0.5f, 1, 0.1f))
                    .diffuse(0.7f)
                    .specular(0.3f)
                    .build()
    );

    var left = Shape.sphere(
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
    var world = new World(objects, light);

    var camera = new Camera(800, 400, PI / 3, Tuple.viewTransform(
            new float[] { 0, 1.5f, -5, 1 },
            new float[] { 0,    1,  0, 1 },
            new float[] { 0,    1,  0, 0 }
    ));

    var canvas = camera.render(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh7.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh7.ppm"));
}