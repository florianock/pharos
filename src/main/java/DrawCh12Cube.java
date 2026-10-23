import io.zocalo.base.Camera;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.Material;
import io.zocalo.scene.World;
import io.zocalo.scene.patterns.Checkers;
import io.zocalo.scene.patterns.Solid;
import io.zocalo.scene.patterns.Stripe;
import io.zocalo.scene.shapes.Cube;
import io.zocalo.scene.shapes.Shape;

import static io.zocalo.scene.MaterialConstants.GLASS;

void main() throws IOException {
    var floorCeiling = new Cube(
            Transform.translation(0, 7, 0).multiply(Transform.scaling(20, 7, 20)),
            new Material.Builder().ambient(0.25).diffuse(0.7).specular(0.9).shininess(300).reflective(0.1)
                    .pattern(new Checkers(new Solid(0, 0, 0), new Solid(0.25, 0.25, 0.25), Transform.scaling(0.07, 0.07, 0.07)))
                    .build()
    );

    var walls = new Cube(
            Transform.scaling(10, 10, 10),
            new Material.Builder().ambient(0.1).diffuse(0.7).specular(0.9).shininess(300).reflective(0.1)
                    .pattern(new Checkers(new Solid(0.4863, 0.3765, 0.2941), new Solid(0.3725, 0.2902, 0.2275), Transform.scaling(0.05, 20, 0.05)))
                    .build()
    );

    var tableTop = new Cube(
            Transform.translation(0, 3.1, 0).multiply(Transform.scaling(3, 0.1, 2)),
            new Material.Builder().ambient(0.1).diffuse(0.7).specular(0.9).shininess(300).reflective(0.2)
                    .pattern(new Stripe(new Solid(0.5529, 0.4235, 0.3255), new Solid(0.6588, 0.5098, 0.4000), Transform.rotationOnY(0.1).multiply(Transform.scaling(0.05, 0.05, 0.05))))
                    .build()
    );

    var leg1 = leg(2.7, -1.7);
    var leg2 = leg(2.7, 1.7);
    var leg3 = leg(-2.7, -1.7);
    var leg4 = leg(-2.7, 1.7);

    var glassCube = new Cube(
            Transform.translation(0, 3.450001, 0).multiply(Transform.rotationOnY(0.2)).multiply(Transform.scaling(0.25, 0.25, 0.25)),
            new Material.Builder().color(1, 1, 0.8).ambient(0).diffuse(0.3).specular(0.9).shininess(300).reflective(0.7).transparency(0.7).refractiveIndex(GLASS.refractiveIndex()).build(),
            false
    );

    var littleCube1 = new Cube(
            Transform.translation(1, 3.35, -0.9).multiply(Transform.rotationOnY(-0.4)).multiply(Transform.scaling(0.15, 0.15, 0.15)),
            new Material.Builder().color(1, 0.5, 0.5).diffuse(0.4).reflective(0.6).build()
    );
    var littleCube2 = new Cube(
            Transform.translation(-1.5, 3.27, 0.3).multiply(Transform.rotationOnY(0.4)).multiply(Transform.scaling(0.15, 0.07, 0.15)),
            new Material.Builder().color(1, 1, 0.5).build()
    );
    var littleCube3 = new Cube(
            Transform.translation(0, 3.25, 1).multiply(Transform.rotationOnY(0.4)).multiply(Transform.scaling(0.2, 0.05, 0.05)),
            new Material.Builder().color(0.5, 1, 0.5).build()
    );
    var littleCube4 = new Cube(
            Transform.translation(-0.6, 3.4, -1).multiply(Transform.rotationOnY(0.8)).multiply(Transform.scaling(0.05, 0.2, 0.05)),
            new Material.Builder().color(0.5, 0.5, 1).build()
    );
    var littleCube5 = new Cube(
            Transform.translation(2, 3.4, 1).multiply(Transform.rotationOnY(0.8)).multiply(Transform.scaling(0.05, 0.2, 0.05)),
            new Material.Builder().color(0.5, 1, 1).build()
    );

    var mirrorFrame = new Cube(
            Transform.translation(-2, 3.5, 9.95).multiply(Transform.scaling(5, 1.5, 0.05)),
            new Material.Builder().color(0.3882, 0.2627, 0.1882).diffuse(0.7).build()
    );
    var mirror = new Cube(
            Transform.translation(-2, 3.5, 9.95).multiply(Transform.scaling(4.8, 1.4, 0.06)),
            new Material.Builder().color(0, 0, 0).ambient(0).diffuse(0).specular(1).shininess(300).reflective(1).build()
    );

    var frame1 = new Cube(
            Transform.translation(-10, 4, 1).multiply(Transform.scaling(0.05, 1, 1)),
            new Material.Builder().color(0.7098, 0.2471, 0.2196).diffuse(0.6).build()
    );
    var frame2 = new Cube(
            Transform.translation(-10, 3.4, 2.7).multiply(Transform.scaling(0.05, 0.4, 0.4)),
            new Material.Builder().color(0.2667, 0.2706, 0.6902).diffuse(0.6).build()
    );
    var frame3 = new Cube(
            Transform.translation(-10, 4.6, 2.7).multiply(Transform.scaling(0.05, 0.4, 0.4)),
            new Material.Builder().color(0.3098, 0.5961, 0.3098).diffuse(0.6).build()
    );

    var light = Light.pointLight(Tuple.point(0, 6.9, -5), Color.WHITE);
    var shapes = new Shape[]{floorCeiling, walls, tableTop, leg1, leg2, leg3, leg4, glassCube,
    littleCube1, littleCube2, littleCube3, littleCube4, littleCube5, mirror, mirrorFrame, frame1, frame2, frame3};
    var world = new World(shapes, new Light[]{light});
    var view = Transform.viewTransform(Tuple.point(8, 6, -8), Tuple.point(0, 3, 0), Tuple.vector(0, 1, 0));
    var camera = new Camera(3840, 2160, 0.7854, view);
    var canvas = camera.renderParallel(world);
    canvas.toPNG(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.png"));
    canvas.toPPM(Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm"));
}

private Cube leg(double x, double z) {
    return new Cube(
            Transform.translation(x, 1.5, z).multiply(Transform.scaling(0.1, 1.5, 0.1)),
            new Material.Builder().color(0.5529, 0.4235, 0.3255).ambient(0.2).diffuse(0.7).build()
    );
}
