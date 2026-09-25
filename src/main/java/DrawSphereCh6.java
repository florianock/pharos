import io.zocalo.*;

void main() throws IOException {
    var canvasPixels = 1080;
    var canvas = new Canvas(canvasPixels, canvasPixels);
    var start = System.nanoTime();
    canvas.renderParallel(Tuple.point(0, 0, -5), Tuple.point(-10, 10, -10), 10f, 7f);
    var end = System.nanoTime();
    System.out.println("Duration: " + (end - start) + " nanoseconds");
    System.out.println("          " + ((end - start) / 1_000_000) + " milliseconds");
    System.out.println("          " + ((end - start) / 1_000_000_000) + " seconds");

    var path = Paths.get(System.getProperty("user.home"),"Pictures/pharos/sphereCh6.png");
    if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
    }

    canvas.toPNG(path);
    canvas.toPPM(Paths.get(path.getParent().toString(), "sphereCh6.ppm"));
}