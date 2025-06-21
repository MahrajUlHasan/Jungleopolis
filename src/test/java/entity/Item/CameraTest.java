package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CameraTest {
    private GameEngine engine;
    private MapSpace map;
    private Camera camera;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.poachers = new ArrayList<>();
        map.cameras = new ArrayList<>();
        camera = new TestCamera(engine, map);
    }

    static class TestCamera extends Camera {
        public TestCamera(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
    }

    @Test
    void testConstructorSetsPathsAndMap() {
        assertEquals(map, camera.map);
        assertEquals("/surveilance/camera/camera1.png", getFieldRecursive(camera, "upPath1"));
        assertEquals("/surveilance/camera/camera2.png", getFieldRecursive(camera, "upPath2"));
    }

    @Test
    void testSpawnAddsToMapCameras() {
        map.cameras.clear();
        camera.spawn(map);
        assertTrue(map.cameras.contains(camera));
    }

    @Test
    void testGetVisRadius() {
        assertEquals(7 * 32, camera.getVisRadius());
    }

    @Test
    void testInVisionReturnsPoachersInRadius() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(0);
        when(poacher.getY()).thenReturn(0);
        camera.x = 0;
        camera.y = 0;
        map.poachers.add(poacher);
        Entity[] inVision = camera.inVision();
        assertEquals(1, inVision.length);
        // Out of range
        when(poacher.getX()).thenReturn(10000);
        when(poacher.getY()).thenReturn(10000);
        assertEquals(0, camera.inVision().length);
    }

    @Test
    void testMarkVisibleCallsSetVisibleOnInVision() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(0);
        when(poacher.getY()).thenReturn(0);
        camera.x = 0;
        camera.y = 0;
        map.poachers.add(poacher);
        camera.markVisible();
        verify(poacher, atLeastOnce()).setVisible();
    }

    @Test
    void testDrawDelegatesToSuper() {
        Graphics2D g2d = mock(Graphics2D.class);
        camera.draw(g2d);
    }

    // Helper to get a field by searching up the class hierarchy
    private Object getFieldRecursive(Object obj, String field) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.get(obj);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
    }
}
