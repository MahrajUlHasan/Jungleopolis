package entity.Item;

import entity.Entity;
import entity.Herbivore;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrassTest {
    private GameEngine engine;
    private MapSpace map;
    private Grass grass;

    static class TestGrass extends Grass {
        public TestGrass(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map); // Fix: ensure getMap() returns a real map
        grass = new TestGrass(engine);
    }

    @Test
    void testConstructorSetsPathsAndNonCollidable() {
        assertNotNull(grass);
        assertFalse(grass.COLLIDABLE);
        assertEquals("/Greenery/grass.png", getFieldRecursive(grass, "upPath1"));
        assertEquals("/Greenery/grass.png", getFieldRecursive(grass, "upPath2"));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        grass.updateStats(); // Should not throw
    }

    @Test
    void testInteractWithHerbivoreCallsEatGrass() {
        Herbivore herb = mock(Herbivore.class);
        grass.interact(herb);
        verify(herb, atLeastOnce()).eatGrass(grass);
    }

    @Test
    void testInteractWithNonHerbivoreDoesNotThrow() {
        entity.DynamicEntity entity = mock(entity.DynamicEntity.class); // Use DynamicEntity to avoid ClassCastException
        grass.interact(entity); // Should not throw
    }

    @Test
    void testSpawnAddsToMapGrasses() {
        map.grasses = new ArrayList<>();
        grass.spawn(map);
        assertTrue(map.grasses.contains(grass));
    }

    // Reflection helper for protected/private fields
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
