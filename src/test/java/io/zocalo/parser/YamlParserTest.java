package io.zocalo.parser;

import io.zocalo.kernel.Camera;
import io.zocalo.kernel.Matrix4x4;
import io.zocalo.kernel.Tuple;
import io.zocalo.scene.Color;
import io.zocalo.scene.Light;
import io.zocalo.scene.shapes.Cube;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YamlParserTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void parseWhenEmptyStringReturnsEmptyList() {
        var result = parser.parse("");
        assertTrue(result.isEmpty());
    }

    @Test
    void parseHandlesCamera() {
        var txt = """
            - add: camera
              width: 600
              height: 338
              field-of-view: 0.7854
              from: [0, 0.5, -6]
              to: [0, 0, 0]
              up: [0, 1, 0]
            """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Camera.class, obj);
        var camera = (Camera) obj;
        assertEquals(600, camera.hsize());
        assertEquals(338, camera.vsize());
        assertEquals(0.7854, camera.fov());
        assertNotNull(camera.transform());
    }

    @Test
    void parseHandlesPointLight() {
        var txt = """
            - add: light
              at: [0, 0, 0]
              intensity: [1, 1, 1]
            """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Light.class, obj);
        var light = (Light) obj;
        assertEquals(Color.WHITE, light.intensity());
    }

    @Test
    void parseHandlesAreaLight() {
        var txt = """
            - add: light
              corner: [-1, 2, 4]
              uvec: [2, 0, 0]
              vvec: [0, 2, 0]
              usteps: 10
              vsteps: 10
              jitter: true
              intensity: [1.5, 1.5, 1.5]
            """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Light.class, obj);
        var light = (Light) obj;
        assertEquals(new Color(1.5, 1.5, 1.5), light.intensity());
        assertEquals(Tuple.point(-1, 2, 4), light.corner());
        assertEquals(Tuple.vector(0.2, 0, 0), light.uvec());
        assertEquals(Tuple.vector(0, 0.2, 0), light.vvec());
        assertEquals(100, light.samples());
        assertEquals(10, light.usteps());
        assertEquals(10, light.vsteps());
        assertTrue(light.jitter());
    }

    @Test
    void parseHandlesCube() {
        var txt = """
            - add: cube
              material:
                color: [ 1, 0.2, 0.3 ]
                diffuse: 0.12
                ambient: 0.345
              transform:
                - [ translate, 4, 5, 6 ]
              shadow: false
        """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Cube.class, obj);
        var cube = (Cube) obj;
        assertEquals(new Color(1, 0.2, 0.3), cube.getMaterial().color());
        assertEquals(0.12, cube.getMaterial().diffuse());
        assertEquals(0.345, cube.getMaterial().ambient());
        assertEquals(Matrix4x4.translation(4, 5, 6), cube.getTransform());
        assertEquals(Matrix4x4.translation(4, 5, 6).invert(), cube.getInverseTransform());
        assertFalse(cube.hasShadow());
    }

    @Test
    void parseHandlesMaterialDefines() {
        var txt = """
            - define: white-material
              value:
                color: [ 1, 1, 1 ]
                diffuse: 0.7
                ambient: 0.1
                specular: 0.0
                reflective: 0.1

            - define: blue-material
              extend: white-material
              value:
                color: [ 0.537, 0.831, 0.914 ]

            - add: cube
              material: blue-material
              transform:
                - [ translate, 8.5, 1.5, -0.5 ]
        """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Cube.class, obj);
        var cube = (Cube) obj;
        assertEquals(new Color(0.537, 0.831, 0.914), cube.getMaterial().color());
        assertEquals(0.7, cube.getMaterial().diffuse());
        assertEquals(0.1, cube.getMaterial().ambient());
        assertEquals(0.0, cube.getMaterial().specular());
        assertEquals(0.1, cube.getMaterial().reflective());
        assertEquals(Matrix4x4.translation(8.5, 1.5, -0.5), cube.getTransform());
    }

    @Test
    void parseHandlesTransformDefines() {
        var txt = """
            - define: standard-transform
              value:
                - [ translate, 1, -1, 1 ]
                - [ scale, 0.5, 0.5, 0.5 ]

            - define: large-object
              value:
                - standard-transform
                - [ scale, 3.5, 3.5, 3.5 ]

            - add: cube
              transform:
                - large-object
                - [ translate, 8.5, 1.5, -0.5 ]
        """;
        var result = parser.parse(txt);
        assertEquals(1, result.size());
        var obj = result.stream().findFirst().get();
        assertInstanceOf(Cube.class, obj);
        var cube = (Cube) obj;
        var expectedTransform = Matrix4x4.translation(8.5, 1.5, -0.5)
                .multiply(Matrix4x4.scaling(3.5, 3.5, 3.5))
                .multiply(Matrix4x4.scaling(0.5, 0.5, 0.5))
                .multiply(Matrix4x4.translation(1, -1, 1));
        assertEquals(expectedTransform, cube.getTransform());
    }
}