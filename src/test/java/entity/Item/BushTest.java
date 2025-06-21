package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BushTest {
    private GameEngine engine;
    private Bush bush;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        org.example.MapSpace map = new org.example.MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        bush = new TestBush(engine);
    }

    static class TestBush extends Bush {
        public TestBush(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
    }

    @Test
    void testConstructorSetsPaths() {
        assertEquals("/Greenery/bushes.png", getFieldRecursive(bush, "upPath1"));
        assertEquals("/Greenery/bushes.png", getFieldRecursive(bush, "upPath2"));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        bush.updateStats();
    }

    @Test
    void testInteractDelegatesToSuper() {
        entity.DynamicEntity entity = mock(entity.DynamicEntity.class);
        bush.interact(entity);
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
