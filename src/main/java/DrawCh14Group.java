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
    var light = Light.pointLight(Tuple.point(5, 18, -9), Color.WHITE);
    var world = new World(new Shape[]{hexagon()}, new Light[]{light});
    var view = Transform.viewTransform(
            Tuple.point(2, 4, -9),
            ORIGIN,
            Tuple.vector(0, 1, 0)
    );
    var camera = new Camera(800, 400, 0.3, view);
    var canvas = camera.renderParallel(world);
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14.ppm"));
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh14.png"));
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