import io.zocalo.kernel.Camera;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.World;
import io.zocalo.scene.patterns.Checkers;
import io.zocalo.scene.patterns.Solid;
import io.zocalo.scene.shapes.Cone;
import io.zocalo.scene.shapes.Cylinder;
import io.zocalo.scene.shapes.Plane;
import io.zocalo.scene.shapes.Shape;

void main() throws IOException {
    var floor = new Plane(new Material.Builder()
            .ambient(0.2)
            .diffuse(0.9)
            .specular(0)
            .pattern(new Checkers(
                    new Solid(0.5, 0.5, 0.5),
                    new Solid(0.75, 0.75, 0.75),
                    Matrix4x4.rotationOnY(0.3)
//                            .multiply(Matrix4x4.rotationOnX(1.57))
//                            .multiply(Matrix4x4.rotationOnZ(1.57))
                            .multiply(Matrix4x4.scaling(0.25, 0.25, 0.25))
            ))
            .build());
    var c1 = new Cylinder(Matrix4x4.translation(-1, 0, 1).multiply(Matrix4x4.scaling(0.5, 1, 0.5)),
            new Material.Builder().color(0, 0, 0.6).diffuse(0.1).specular(0.9).shininess(300).reflective(0.9).build(),
            0.0, 0.75, true);

    // concentric cylinders
    var c2 = new Cylinder(Matrix4x4.translation(1, 0, 0).multiply(Matrix4x4.scaling(0.8, 1, 0.8)),
            new Material.Builder().color(1, 1, 0.3).ambient(0.1).diffuse(0.8).specular(0.9).shininess(300).build(),
            0.0, 0.2);
    var c3 = new Cylinder(Matrix4x4.translation(1, 0, 0).multiply(Matrix4x4.scaling(0.6, 1, 0.6)),
            new Material.Builder().color(1, 0.9, 0.4).ambient(0.1).diffuse(0.8).specular(0.9).shininess(300).build(),
            0.0, 0.3);
    var c4 = new Cylinder(Matrix4x4.translation(1, 0, 0).multiply(Matrix4x4.scaling(0.4, 1, 0.4)),
            new Material.Builder().color(1, 0.8, 0.5).ambient(0.1).diffuse(0.8).specular(0.9).shininess(300).build(),
            0.0, 0.4);
    var c5 = new Cylinder(Matrix4x4.translation(1, 0, 0).multiply(Matrix4x4.scaling(0.2, 1, 0.2)),
            new Material.Builder().color(1, 0.7, 0.6).ambient(0.1).diffuse(0.8).specular(0.9).shininess(300).build(),
            0.0, 0.5, true);

    // decorative cylinders
    var c6 = new Cylinder(Matrix4x4.translation(0, 0, -0.75).multiply(Matrix4x4.scaling(0.05, 1, 0.05)),
            new Material.Builder().color(1, 0, 0).ambient(0.1).diffuse(0.9).specular(0.9).shininess(300).build(),
            0.0, 0.3, true);
    var c7 = new Cylinder(Matrix4x4.translation(0, 0, -2.25)
            .multiply(Matrix4x4.rotationOnY(-0.15))
            .multiply(Matrix4x4.translation(0, 0, 1.5))
            .multiply(Matrix4x4.scaling(0.05, 1, 0.05)),
            new Material.Builder().color(1, 1, 0).ambient(0.1).diffuse(0.9).specular(0.9).shininess(300).build(),
            0.0, 0.3, true);
    var c8 = new Cylinder(Matrix4x4.translation(0, 0, -2.25)
            .multiply(Matrix4x4.rotationOnY(-0.3))
            .multiply(Matrix4x4.translation(0, 0, 1.5))
            .multiply(Matrix4x4.scaling(0.05, 1, 0.05)),
            new Material.Builder().color(0, 1, 0).ambient(0.1).diffuse(0.9).specular(0.9).shininess(300).build(),
            0.0, 0.3, true);
    var c9 = new Cylinder(Matrix4x4.translation(0, 0, -2.25)
            .multiply(Matrix4x4.rotationOnY(-0.45))
            .multiply(Matrix4x4.translation(0, 0, 1.5))
            .multiply(Matrix4x4.scaling(0.05, 1, 0.05)),
            new Material.Builder().color(0, 1, 1).ambient(0.1).diffuse(0.9).specular(0.9).shininess(300).build(),
            0.0, 0.3, true);

    // glass cylinder
    var c10 = new Cylinder(Matrix4x4.translation(0, 0, -1.5).multiply(Matrix4x4.scaling(0.33, 1, 0.33)),
            new Material.Builder().color(0.25, 0, 0).diffuse(0.1).specular(0.9).shininess(300).reflective(0.9).transparency(0.9).refractiveIndex(1.5).build(),
            0.0001, 0.5, true);

    // cones
    var cone1 = new Cone(Matrix4x4.translation(0, 0.55, -0.75).multiply(Matrix4x4.scaling(0.1, 0.25, 0.1)),
            new Material.Builder().color(0.8, 0.2, 0.3).ambient(0.1).diffuse(0.7).specular(0.9).shininess(300).reflective(0.2).build(),
            -1, 0);
    var cone2 = new Cone(Matrix4x4.translation(1, 1, 0).multiply(Matrix4x4.scaling(0.2, 0.5, 0.2)),
            c5.getMaterial(), -1, -0.5);
    var cone3 = new Cone(Matrix4x4.translation(1, 1.25, 0).multiply(Matrix4x4.scaling(0.1, 0.5, 0.1)),
            c5.getMaterial(), -1, 0);

    var light = Light.pointLight(Tuple.point(1, 6.9, -4.9), Color.WHITE);
    var shapes = new Shape[]{floor, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, cone1, cone2, cone3};
    var world = new World(shapes, new Light[]{light});
    var camera = new Camera(3840, 2160, 0.3, Tuple.point(8, 3.5, -9), Tuple.point(0, 0.3, 0), Tuple.vector(0, 1, 0));
    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh13HQ.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/sceneCh13HQ.ppm"));
}
