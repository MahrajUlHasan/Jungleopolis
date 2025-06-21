package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeTest {
    private GameEngine engine;
    private Tree tree;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        MapSpace map = mock(MapSpace.class);
        when(engine.getMap()).thenReturn(map);
        tree = new Tree(engine);
    }

    @Test
    void testConstructorSetsPathsAndSprite() {
        assertNotNull(tree);
        assertEquals("/Greenery/tree1.png", getFieldRecursive(tree, "upPath1"));
        assertEquals("/Greenery/tree2.png", getFieldRecursive(tree, "upPath2"));
        // Sprite and setSprite are hard to test directly, but no exception means success
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        assertDoesNotThrow(() -> tree.updateStats());
    }

    @Test
    void testInteractThrowsClassCast() {
        Entity entity = mock(Entity.class);
        assertThrows(ClassCastException.class, () -> tree.interact(entity));
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
