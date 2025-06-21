package entity.Item;

import entity.Carnivore;
import entity.Entity;
import entity.Status;
import entity.Herbivore;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuffaloTest {
    private GameEngine engine;
    private MapSpace map;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private Buffalo buffalo;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = spy(new MapSpace(engine));
        keyHandler = mock(KeyHandler.class);
        mouseHandler = mock(MouseHandler.class);
        buffalo = new TestBuffalo(engine, map, keyHandler, mouseHandler);
        when(engine.getMap()).thenReturn(map);
        map.buffalos = new ArrayList<>();
    }

    static class TestBuffalo extends Buffalo {
        public TestBuffalo(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
            super(engine, m, kH, mH);
        }
        @Override
        public void getSpriteImage() { /* no-op for tests */ }
    }

    @Test
    void testConstructorAndDefaultValues() {
        assertEquals(200, getIntField(buffalo, "health"));
        assertEquals(map, getFieldRecursive(buffalo, "map"));
    }

    @Test
    void testSetDefaultValues() {
        buffalo.setDefaultValues();
        assertEquals(200, getIntField(buffalo, "health"));
    }

    @Test
    void testAttackedReducesHealthAndDies() {
        Carnivore carnivore = mock(Carnivore.class);
        when(carnivore.getAttackPower()).thenReturn(50);
        buffalo.setDefaultValues();
        buffalo.attacked(carnivore);
        assertEquals(150, getIntField(buffalo, "health"));
        // Now kill it
        when(carnivore.getAttackPower()).thenReturn(200);
        buffalo.attacked(carnivore);
        assertTrue(!map.buffalos.contains(buffalo));
    }

    @Test
    void testSetTargetReproduce() {
        setFieldRecursive(buffalo, "status", Status.REPRODUCE);
        Entity mate = mock(Buffalo.class);
        doReturn(mate).when(map).getClosestEntity("Buffalo", buffalo);
        buffalo.setTarget();
        assertEquals(mate, getFieldRecursive(buffalo, "target"));
    }

    @Test
    void testSetTargetHeard() {
        setFieldRecursive(buffalo, "status", Status.HEARD);
        buffalo.setTarget();
        // Should not change target, handled by super
    }

    @Test
    void testDoReproduceWithBuffalo() {
        Buffalo mate = new TestBuffalo(engine, map, keyHandler, mouseHandler);
        map.buffalos.clear();
        setFieldRecursive(buffalo, "status", Status.REPRODUCE);
        buffalo.doReproduce(mate);
        assertTrue(map.buffalos.stream().anyMatch(b -> b != buffalo && b instanceof Buffalo));
        assertEquals(Status.HEARD, getFieldRecursive(buffalo, "status"));
    }

    @Test
    void testDoReproduceWithNonBuffalo() {
        Herbivore notBuffalo = mock(Herbivore.class);
        setFieldRecursive(buffalo, "status", Status.REPRODUCE);
        buffalo.doReproduce(notBuffalo);
        // Should do nothing
        assertNotEquals(Status.HEARD, getFieldRecursive(buffalo, "status"));
    }

    @Test
    void testMoveDelegatesToSuper() {
        // Just ensure no exceptions
        buffalo.move();
    }

    @Test
    void testInteractDelegatesToSuper() {
        Entity entity = mock(Entity.class);
        buffalo.interact(entity);
    }

    @Test
    void testUpdateStatsDelegatesToSuper() {
        buffalo.updateStats();
    }

    @Test
    void testSpawnAddsToMapBuffalos() {
        map.buffalos.clear();
        buffalo.spawn(map);
        assertTrue(map.buffalos.contains(buffalo));
    }

    @Test
    void testDieRemovesFromMapBuffalos() {
        map.buffalos.add(buffalo);
        // Use reflection to set map field if needed
        setFieldRecursive(buffalo, "map", map);
        buffalo.die();
        assertFalse(map.buffalos.contains(buffalo));
    }

    // Helper to get int field by reflection
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
    // Helper to set a field by searching up the class hierarchy
    private void setFieldRecursive(Object obj, String field, Object value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
    }
}
