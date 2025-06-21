package entity.Item;

import entity.Animal;
import entity.Entity;
import entity.util.jeepAndVisitorHandler;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JeepTest {
    private GameEngine engine;
    private MapSpace map;
    private Jeep jeep;
    private jeepAndVisitorHandler handler;

    static class TestJeep extends Jeep {
        public TestJeep(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override public void changeDirection() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        handler = mock(jeepAndVisitorHandler.class);
        map.jeepHandler = handler;
        when(engine.getMap()).thenReturn(map);
        jeep = new TestJeep(engine, map);
        map.jeeps = new ArrayList<>();
        map.dynamicEntities = new ArrayList<>();
        map.entitiesToAdd.clear();
        map.entities = new ArrayList<>();
        map.animals = new ArrayList<>();
    }

    @Test
    void testConstructorSetsPathsAndHandler() {
        assertNotNull(jeep);
        assertEquals(map.jeepHandler, getFieldRecursive(jeep, "handler"));
        assertEquals("/Jeep/jeepup.png", getFieldRecursive(jeep, "upPath1"));
        assertEquals("/Jeep/jeepup.png", getFieldRecursive(jeep, "upPath2"));
        assertEquals("/Jeep/jeepRight.png", getFieldRecursive(jeep, "rightPath1"));
        assertEquals("/Jeep/jeepRight.png", getFieldRecursive(jeep, "rightPath2"));
        assertEquals("/Jeep/jeepLeft.png", getFieldRecursive(jeep, "leftPath1"));
        assertEquals("/Jeep/jeepLeft.png", getFieldRecursive(jeep, "leftPath2"));
        assertEquals("/Jeep/jeepdown.png", getFieldRecursive(jeep, "downPath1"));
        assertEquals("/Jeep/jeepdown.png", getFieldRecursive(jeep, "downPath2"));
        assertEquals(1, getIntField(jeep, "velocity"));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        jeep.updateStats(); // Should not throw
    }

    @Test
    void testSpawnAddsToMapAndHandlerIfRoadExists() {
        // Mock static method
        try (var mocked = mockStatic(jeepAndVisitorHandler.class)) {
            mocked.when(jeepAndVisitorHandler::hasRoad).thenReturn(true);
            jeep.spawn(map);
            assertTrue(map.entitiesToAdd.contains(jeep));
            assertTrue(map.dynamicEntities.contains(jeep));
            assertTrue(map.jeeps.contains(jeep));
            verify(handler, atLeastOnce()).addJeep(jeep);
        }
    }

    @Test
    void testSpawnShowsDialogIfNoRoad() {
        try (var mocked = mockStatic(jeepAndVisitorHandler.class)) {
            mocked.when(jeepAndVisitorHandler::hasRoad).thenReturn(false);
            try (var dialogMock = mockStatic(JOptionPane.class)) {
                jeep.spawn(map);
                dialogMock.verify(() -> JOptionPane.showMessageDialog(any(), any(), any(), anyInt()), atLeastOnce());
            }
            assertFalse(map.entitiesToAdd.contains(jeep));
            assertFalse(map.dynamicEntities.contains(jeep));
            assertFalse(map.jeeps.contains(jeep));
            verify(handler, never()).addJeep(jeep);
        }
    }

    @Test
    void testGetVisRadius() {
        assertEquals(jeep.visRadius, jeep.getVisRadius());
    }

    @Test
    void testInVisionReturnsAnimalsInRadius() {
        Animal animal1 = mock(Animal.class);
        Animal animal2 = mock(Animal.class);
        jeep.x = 0; jeep.y = 0;
        when(animal1.getX()).thenReturn(3 * 32);
        when(animal1.getY()).thenReturn(4 * 32);
        when(animal2.getX()).thenReturn(10 * 32);
        when(animal2.getY()).thenReturn(10 * 32);
        map.animals.add(animal1);
        map.animals.add(animal2);
        jeep.visRadius = 5 * 32;
        Entity[] inVision = jeep.inVision();
        assertTrue(java.util.Arrays.asList(inVision).contains(animal1));
        assertFalse(java.util.Arrays.asList(inVision).contains(animal2));
    }

    @Test
    void testGetPopularity() {
        jeep.passengers = 2;
        Animal animal = mock(Animal.class);
        when(animal.getX()).thenReturn(jeep.x);
        when(animal.getY()).thenReturn(jeep.y);
        map.animals.add(animal);
        jeep.visRadius = 5 * 32;
        assertEquals((2 * 1) / 4, jeep.getPopularity());
    }

    @Test
    void testDrawDoesNotThrow() {
        Graphics2D g2d = mock(Graphics2D.class);
        jeep.x = 10; jeep.y = 20; jeep.width = 30; jeep.height = 40; jeep.passengers = 3;
        assertDoesNotThrow(() -> jeep.draw(g2d));
    }

    // Reflection helpers for protected/private fields
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
    private int getIntField(Object obj, String field) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.getInt(obj);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
    }
}
