import io.zocalo.base.Canvas;
import io.zocalo.base.Tuple;
import io.zocalo.scene.Color;

void main() throws IOException {
    var start = Tuple.point(0, 1, 0);
    var velocity = Tuple.vector(1, 1.8f, 0).normalize().multiply(11.25f);
    var p = new Projectile(start, velocity);

    var gravity = Tuple.vector(0, -0.1f, 0);
    var wind = Tuple.vector(-0.01f, 0, 0);
    var e = new Environment(gravity, wind);

    var canvasWidth = 900;
    var canvasHeight = 550;

    var c = new Canvas(canvasWidth, canvasHeight);
    while (p.position().y() >= 0 && p.position().y() < canvasHeight && p.position().x() >= 0 && p.position().x() < canvasWidth) {
        c.writePixel((int) (p.position().x() + 0.5), canvasHeight - (int) (p.position().y() + 0.5), Color.RED);
        p = p.tick(e);
    }

    var ppm = c.toPPM();

    var path = Paths.get(System.getProperty("user.home"),"Pictures/pharos/test.ppm");
    if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
    }

    try {
        Files.write(path, ppm.getBytes());
    } catch (IOException ex) {
        throw new RuntimeException(ex);
    }
}

record Projectile(Tuple position, Tuple velocity) {

    public Projectile tick(Environment env) {
        Tuple position = this.position().add(this.velocity());
        Tuple velocity = this.velocity().add(env.gravity()).add(env.wind());
        return new Projectile(position, velocity);
    }
}

record Environment(Tuple gravity, Tuple wind) { }