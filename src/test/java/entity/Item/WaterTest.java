package entity.Item;

import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WaterTest {
    private GameEngine engine;
    private Water water;
    private MapSpace map;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = mock(org.example.MapSpace.class);
        when(engine.getMap()).thenReturn(map);
        water = new Water(engine);
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(water);
        assertEquals(engine, getFieldRecursive(water, "engine"));
        assertEquals("/Water/water.png", getFieldRecursive(water, "upPath1"));
        assertEquals("/Water/water.png", getFieldRecursive(water, "upPath2"));
    }

    @Test
    void testSetDefaultValuesDoesNotThrow() {
        assertDoesNotThrow(() -> water.setDefaultValues());
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
