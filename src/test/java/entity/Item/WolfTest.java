package entity.Item;

import entity.Status;
import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WolfTest {
    private GameEngine engine;
    private MapSpace map;
    private Wolf wolf;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.wolves = new ArrayList<>();
        map.animals = new ArrayList<>();
        wolf = new Wolf(engine, map);
    }

    @Test
    void testConstructorSetsPathsAndStats() {
        assertNotNull(wolf);
        assertEquals("/Animal/wolf/up1.png", getFieldRecursive(wolf, "upPath1"));
        assertEquals("/Animal/wolf/up2.png", getFieldRecursive(wolf, "upPath2"));
        assertEquals("/Animal/wolf/down1.png", getFieldRecursive(wolf, "downPath1"));
        assertEquals("/Animal/wolf/down2.png", getFieldRecursive(wolf, "downPath2"));
        assertEquals("/Animal/wolf/right1.png", getFieldRecursive(wolf, "rightPath1"));
        assertEquals("/Animal/wolf/right2.png", getFieldRecursive(wolf, "rightPath2"));
        assertEquals("/Animal/wolf/left1.png", getFieldRecursive(wolf, "leftPath1"));
        assertEquals("/Animal/wolf/left2.png", getFieldRecursive(wolf, "leftPath2"));
        wolf.setDefaultValues();
        assertEquals(100, getIntField(wolf, "health"));
        assertEquals(100, getIntField(wolf, "maxHp"));
        assertEquals(500, getIntField(wolf, "maxAge"));
        assertEquals(100, getIntField(wolf, "cost"));
        assertEquals(10, getIntField(wolf, "attackPower"));
    }

    @Test
    void testSetTargetReproduce() throws Exception {
        setField(wolf, "status", Status.REPRODUCE);
        Entity dummy = mock(Entity.class);
        MapSpace spyMap = spy(map);
        doReturn(dummy).when(spyMap).getClosestEntity(eq("Wolf"), eq(wolf));
        setField(wolf, "map", spyMap);
        wolf.setTarget();
        assertEquals(dummy, getFieldRecursive(wolf, "target"));
    }

    @Test
    void testDoReproduceAddsOffspring() throws Exception {
        Wolf mate = new Wolf(engine, map);
        MapSpace spyMap = spy(map);
        doNothing().when(spyMap).addEnt(any(Wolf.class));
        when(engine.getMap()).thenReturn(spyMap);
        setField(wolf, "status", Status.REPRODUCE);
        wolf.doReproduce(mate);
        assertEquals(Status.HEARD, getFieldRecursive(wolf, "status"));
    }

    @Test
    void testSpawnAddsToWolvesList() {
        wolf.spawn(map);
        assertTrue(map.wolves.contains(wolf));
    }

    @Test
    void testDieRemovesFromWolvesList() throws Exception {
        map.wolves.add(wolf);
        wolf.die();
        assertFalse(map.wolves.contains(wolf));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        assertDoesNotThrow(() -> wolf.updateStats());
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
    private void setField(Object obj, String field, Object value) throws Exception {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
    }
}
