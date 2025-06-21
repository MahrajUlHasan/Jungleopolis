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

class LionTest {
    private GameEngine engine;
    private MapSpace map;
    private Lion lion;

    @BeforeEach
    void setUp() throws Exception {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.lions = new ArrayList<>();
        map.animals = new ArrayList<>();
        lion = new Lion(engine, map);
    }

    @Test
    void testConstructorSetsPathsAndStats() {
        assertNotNull(lion);
        assertEquals("/Animal/up1.png", getFieldRecursive(lion, "upPath1"));
        assertEquals("/Animal/up2.png", getFieldRecursive(lion, "upPath2"));
        assertEquals("/Animal/down1.png", getFieldRecursive(lion, "downPath1"));
        assertEquals("/Animal/down2.png", getFieldRecursive(lion, "downPath2"));
        assertEquals("/Animal/right1.png", getFieldRecursive(lion, "rightPath1"));
        assertEquals("/Animal/right2.png", getFieldRecursive(lion, "rightPath2"));
        assertEquals("/Animal/left1.png", getFieldRecursive(lion, "leftPath1"));
        assertEquals("/Animal/left2.png", getFieldRecursive(lion, "leftPath2"));
        assertEquals(100, getIntField(lion, "health"));
        assertEquals(100, getIntField(lion, "maxHp"));
        assertEquals(500, getIntField(lion, "maxAge"));
        assertEquals(100, getIntField(lion, "cost"));
        assertEquals(10, getIntField(lion, "attackPower"));
    }

    @Test
    void testSetTargetReproduce() throws Exception {
        setField(lion, "status", Status.REPRODUCE);
        Entity dummy = mock(Entity.class);
        // Simulate getClosestEntity returns dummy
        MapSpace spyMap = spy(map);
        doReturn(dummy).when(spyMap).getClosestEntity(eq("Lion"), eq(lion));
        setField(lion, "map", spyMap);
        lion.setTarget();
        assertEquals(dummy, getFieldRecursive(lion, "target"));
    }

    @Test
    void testDoReproduceAddsOffspring() throws Exception {
        Lion mate = new Lion(engine, map);
        MapSpace spyMap = spy(map);
        doNothing().when(spyMap).addEnt(any(Lion.class));
        when(engine.getMap()).thenReturn(spyMap);
        setField(lion, "status", Status.REPRODUCE);
        lion.doReproduce(mate);
        assertEquals(Status.HEARD, getFieldRecursive(lion, "status"));
    }

    @Test
    void testSpawnAddsToLionsList() {
        lion.spawn(map);
        assertTrue(map.lions.contains(lion));
    }

    @Test
    void testDieRemovesFromLionsList() throws Exception {
        map.lions.add(lion);
        lion.die();
        assertFalse(map.lions.contains(lion));
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
