import io.zocalo.*;
import io.zocalo.base.Canvas;
import io.zocalo.base.Transform;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

void main() throws IOException {
    var canvasWidth = 800;
    var canvasHeight = 800;

    var canvas = new Canvas(canvasWidth, canvasHeight);
    var origin = Tuple.point(canvasWidth / 2f, 0, canvasHeight / 2f);

    var hours = new Tuple[12];
    hours[0] = Tuple.point(0, 0, 1);
    var r = Transform.rotationOnY(Utils.PI / 6);

    for (var i = 1; i < 12; i++) {
        hours[i] = r.multiply(hours[i-1]);
    }

    var clockRadius = canvasWidth * 3f/8;

    for (var i = 0; i < 12; i++) {
        var positionOnCanvas = hours[i].multiply(clockRadius).add(origin);
        canvas.writePixel((int) positionOnCanvas.x(), (int) (canvasHeight - positionOnCanvas.z()), Color.WHITE);
    }

    var ppm = canvas.toPPM();

    var path = Paths.get(System.getProperty("user.home"),"Pictures/pharos/clock.ppm");
    if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
    }

    try {
        Files.write(path, ppm.getBytes());
    } catch (IOException ex) {
        throw new RuntimeException(ex);
    }
}