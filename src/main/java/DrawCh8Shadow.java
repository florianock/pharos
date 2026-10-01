import io.zocalo.kernel.Camera;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.*;

import static io.zocalo.Utils.PI;
import static io.zocalo.Utils.QTR_PI;

void main() throws IOException {

    var baseMaterial = new Material.Builder()
            .ambient(0.2f)
            .diffuse(0.8f)
            .specular(0.3f)
            .shininess(200)
            .build();

    var wall = new Sphere(
            Matrix4x4.translation(0, 0, 20).multiply(Matrix4x4.scaling(200, 200, 0.01f)),
            new Material.Builder().ambient(0).diffuse(0.5f).specular(0).build()
    );

    var palm = new Sphere(
            Matrix4x4.translation(0, 0, -15).multiply(Matrix4x4.scaling(4, 3, 3)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 0.1f, 1)).build()
    );

    var wrist = new Sphere(
                    Matrix4x4.rotationOnZ(QTR_PI)
                    .multiply(Matrix4x4.translation(-4, 0, -21))
                    .multiply(Matrix4x4.scaling(3, 3, 3)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 1, 1)).build()
    );

    var thumb = new Sphere(
            Matrix4x4.translation(-2, 2, -16).multiply(Matrix4x4.scaling(1, 3, 1)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 0.1f, 1)).build()
    );

    var index = new Sphere(
            Matrix4x4.translation(3, 2, -22).multiply(Matrix4x4.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(Color.create(1, 1, 0.1f)).build()
    );

    var middle = new Sphere(
            Matrix4x4.translation(4, 1, -19).multiply(Matrix4x4.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 1, 0.5f)).build()
    );

    var ring = new Sphere(
            Matrix4x4.translation(4, 0, -18).multiply(Matrix4x4.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 1, 0.1f)).build()
    );

    var pinky = new Sphere(
            Matrix4x4.translation(3, -1.5f, -20)
                    .multiply(Matrix4x4.rotationOnZ(-PI / 10))
                    .multiply(Matrix4x4.translation(1, 0, 0))
                    .multiply(Matrix4x4.scaling(2.5f, 0.6f, 0.6f)),
            new Material.Builder().from(baseMaterial).color(Color.create(0.1f, 0.5f, 1)).build()
    );

    var objects = new Shape[] { wall, palm, wrist, thumb, index, middle, ring, pinky };
    var light = Light.pointLight(Tuple.point(0, 0, -100), Color.WHITE);
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(1000, 500, PI / 6, Tuple.viewTransform(
            new double[] { 40, 0, -70, 1 },
            new double[] {  0, 0,  -5, 1 },
            new double[] {  0, 1,   0, 0 }
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}