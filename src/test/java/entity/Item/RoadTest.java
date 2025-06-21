package entity.Item;

import entity.util.jeepAndVisitorHandler;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoadTest {
    private GameEngine engine;
    private MapSpace map;
    private Road road;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = mock(MapSpace.class);
        when(engine.getMap()).thenReturn(map);
        road = new Road(engine);
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(road);
        assertEquals(engine, getFieldRecursive(road, "engine"));
        assertEquals("/Objects/Road.png", getFieldRecursive(road, "upPath1"));
        assertEquals("/Objects/Road.png", getFieldRecursive(road, "upPath2"));
        assertFalse((Boolean) getFieldRecursive(road, "COLLIDABLE"));
    }

    @Test
    void testSetDefaultValuesDoesNotThrow() {
        assertDoesNotThrow(() -> road.setDefaultValues());
    }

    @Test
    void testIsOccupiedDelegatesToHandler() {
        // Setup
        map.jeepHandler = mock(jeepAndVisitorHandler.class);
        road.row = 2;
        road.col = 3;
        when(map.jeepHandler.isOccupied(2, 3)).thenReturn(true);
        // Should delegate to handler
        assertTrue(road.IsOccupied());
        when(map.jeepHandler.isOccupied(2, 3)).thenReturn(false);
        assertFalse(road.IsOccupied());
    }

    @Test
    void testSpawnAddsToRoadsAndCallsHandler() {
        // Setup
        map.roads = new java.util.ArrayList<>();
        map.jeepHandler = mock(jeepAndVisitorHandler.class);
        // These are final fields, so we can only clear them if not null
        if (map.entitiesToAdd == null) {
            setField(map, "entitiesToAdd", new java.util.ArrayList<>());
        } else {
            map.entitiesToAdd.clear();
        }
        if (map.buildings == null) {
            setField(map, "buildings", new java.util.ArrayList<>());
        } else {
            map.buildings.clear();
        }
        // Set x and y so col/row are predictable
        setField(road, "x", 96); // tileSize will be 48
        setField(road, "y", 144);
        setField(engine, "tileSize", 48);
        // Call
        road.spawn(map);
        // Should be added to roads
        assertTrue(map.roads.contains(road));
        // Should call addRoad
        verify(map.jeepHandler).addRoad(road);
        // Should set col/row correctly
        assertEquals(4, road.col); // 192/48 = 4
        assertEquals(3, road.row); // 144/48 = 3
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

    private void setField(Object obj, String fieldName, Object value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + fieldName + "' not found");
    }
}
