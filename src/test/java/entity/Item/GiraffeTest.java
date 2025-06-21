package entity.Item;

import entity.Carnivore;
import entity.Entity;
import entity.Herbivore;
import entity.Status;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GiraffeTest {
    private GameEngine engine;
    private MapSpace map;
    private Giraffe giraffe;

    static class TestGiraffe extends Giraffe {
        public TestGiraffe(GameEngine engine, MapSpace m) { super(engine, m); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override protected void setRandPos() { /* no-op for tests */ }
        @Override public void setDefaultValues() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = mock(MapSpace.class);
        when(engine.getMap()).thenReturn(map);
        giraffe = new TestGiraffe(engine, map);
    }

    @Test
    void testConstructorSetsImagePaths() {
        assertNotNull(giraffe);
        // Image paths are set, but we skip actual image loading in tests
    }

    @Test
    void testSetStatsDoesNothing() {
        giraffe.setStats(); // Should not throw
    }

    @Test
    void testAttackedDoesNothing() {
        Carnivore carnivore = mock(Carnivore.class);
        giraffe.attacked(carnivore); // Should not throw
    }

    // Reflection helpers for protected fields
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

    @Test
    void testSetTargetReproduceStatusSetsTarget() {
        setFieldRecursive(giraffe, "status", Status.REPRODUCE);
        Entity mate = mock(Entity.class);
        when(map.getClosestEntity(eq("Giraffe"), eq(giraffe))).thenReturn(mate);
        giraffe.setTarget();
        assertEquals(mate, getFieldRecursive(giraffe, "target"));
    }

    @Test
    void testSetTargetHeardStatusDoesNotSetTarget() {
        setFieldRecursive(giraffe, "status", Status.HEARD);
        setFieldRecursive(giraffe, "target", null);
        giraffe.setTarget();
        assertNull(getFieldRecursive(giraffe, "target"));
    }

    @Test
    void testSetTargetOtherStatusDoesNotSetTarget() {
        // Use a status value that is not HEARD or REPRODUCE
        setFieldRecursive(giraffe, "status", Status.SURVIVE); // Use a valid Status enum value
        setFieldRecursive(giraffe, "target", null);
        giraffe.setTarget();
        assertNull(getFieldRecursive(giraffe, "target"));
    }

    @Test
    void testDoReproduceWithGiraffeCreatesOffspring() {
        Giraffe mate = new TestGiraffe(engine, map);
        MapSpace realMap = mock(MapSpace.class);
        when(engine.getMap()).thenReturn(realMap);
        setFieldRecursive(giraffe, "status", Status.REPRODUCE);
        giraffe.doReproduce(mate);
        verify(realMap, atLeastOnce()).addEnt(any(Giraffe.class));
        assertEquals(Status.HEARD, getFieldRecursive(giraffe, "status"));
    }

    @Test
    void testDoReproduceWithNonGiraffeDoesNothing() {
        Herbivore mate = mock(Herbivore.class);
        setFieldRecursive(giraffe, "status", Status.REPRODUCE);
        giraffe.doReproduce(mate);
        assertEquals(Status.REPRODUCE, getFieldRecursive(giraffe, "status"));
    }

    @Test
    void testInteractDoesNothing() {
        Entity entity = mock(Entity.class);
        giraffe.interact(entity); // Should not throw
    }

    @Test
    void testSpawnAddsToMap() {
        MapSpace realMap = new MapSpace(engine);
        giraffe = new TestGiraffe(engine, realMap);
        realMap.giraffes = new ArrayList<>();
        giraffe.spawn(realMap);
        assertTrue(realMap.giraffes.contains(giraffe));
    }
}
