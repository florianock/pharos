import io.zocalo.base.Camera;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.*;
import io.zocalo.scene.shapes.Shape;
import io.zocalo.scene.shapes.Sphere;

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
            Transform.translation(0, 0, 20).multiply(Transform.scaling(200, 200, 0.01f)),
            new Material.Builder().ambient(0).diffuse(0.5f).specular(0).build()
    );

    var palm = new Sphere(
            Transform.translation(0, 0, -15).multiply(Transform.scaling(4, 3, 3)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 0.1f, 1)).build()
    );

    var wrist = new Sphere(
                    Transform.rotationOnZ(QTR_PI)
                    .multiply(Transform.translation(-4, 0, -21))
                    .multiply(Transform.scaling(3, 3, 3)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 1, 1)).build()
    );

    var thumb = new Sphere(
            Transform.translation(-2, 2, -16).multiply(Transform.scaling(1, 3, 1)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 0.1f, 1)).build()
    );

    var index = new Sphere(
            Transform.translation(3, 2, -22).multiply(Transform.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(new Color(1, 1, 0.1f)).build()
    );

    var middle = new Sphere(
            Transform.translation(4, 1, -19).multiply(Transform.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 1, 0.5f)).build()
    );

    var ring = new Sphere(
            Transform.translation(4, 0, -18).multiply(Transform.scaling(3, 0.75f, 0.75f)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 1, 0.1f)).build()
    );

    var pinky = new Sphere(
            Transform.translation(3, -1.5f, -20)
                    .multiply(Transform.rotationOnZ(-PI / 10))
                    .multiply(Transform.translation(1, 0, 0))
                    .multiply(Transform.scaling(2.5f, 0.6f, 0.6f)),
            new Material.Builder().from(baseMaterial).color(new Color(0.1f, 0.5f, 1)).build()
    );

    var objects = new Shape[] { wall, palm, wrist, thumb, index, middle, ring, pinky };
    var light = Light.pointLight(Tuple.point(0, 0, -100), Color.WHITE);
    var world = new World(objects, new Light[]{light});

    var camera = new Camera(1000, 500, PI / 6, Transform.viewTransform(
            Tuple.point(40, 0, -70),
            Tuple.point( 0, 0,  -5),
            Tuple.vector(0, 1,   0)
    ));

    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}