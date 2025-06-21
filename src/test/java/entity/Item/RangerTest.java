package entity.Item;

import entity.*;
import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RangerTest {
    private GameEngine engine;
    private MapSpace map;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private Ranger ranger;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        keyHandler = mock(KeyHandler.class);
        mouseHandler = mock(MouseHandler.class);
        when(engine.getMap()).thenReturn(map);
        map.rangers = new ArrayList<>();
        map.entities = new ArrayList<>();
        map.dynamicEntities = new ArrayList<>();
        map.poachers = new ArrayList<>();
        ranger = new Ranger(engine, map, keyHandler, mouseHandler);
    }

    @Test
    void testConstructorSetsPathsAndDefaults() {
        assertNotNull(ranger);
        assertEquals("/People/Rangers/up1.png", getFieldRecursive(ranger, "upPath1"));
        assertEquals("/People/Rangers/up2.png", getFieldRecursive(ranger, "upPath2"));
        assertEquals("/People/Rangers/down1.png", getFieldRecursive(ranger, "downPath1"));
        assertEquals("/People/Rangers/down2.png", getFieldRecursive(ranger, "downPath2"));
        assertEquals("/People/Rangers/right1.png", getFieldRecursive(ranger, "rightPath1"));
        assertEquals("/People/Rangers/right2.png", getFieldRecursive(ranger, "rightPath2"));
        assertEquals("/People/Rangers/left1.png", getFieldRecursive(ranger, "leftPath1"));
        assertEquals("/People/Rangers/left2.png", getFieldRecursive(ranger, "leftPath2"));
        assertEquals(30, ranger.getAttackPower());
        assertEquals(7 * engine.tileSize, ranger.getVisRadius());
    }

    @Test
    void testSetDefaultValuesSetsFields() {
        when(engine.getMonthsPassed()).thenReturn(42L);
        ranger.setDefaultValues();
        assertEquals(2, getIntField(ranger, "velocity"));
        assertEquals(42L, getLongField(ranger, "purchasedMonth"));
        assertEquals(org.example.Direction.UP, ranger.direction);
        assertEquals(engine.tileSize, ranger.width);
        assertEquals(engine.tileSize, ranger.height);
    }

    @Test
    void testSpawnAddsToLists() {
        ranger.spawn(map);
        assertTrue(map.rangers.contains(ranger));
        assertTrue(map.entities.contains(ranger));
        assertTrue(map.dynamicEntities.contains(ranger));
    }

    @Test
    void testDieRemovesFromRangersList() throws Exception {
        map.rangers.add(ranger);
        ranger.die();
        assertFalse(map.rangers.contains(ranger));
    }

    @Test
    void testSetTargetSetsPoacher() throws Exception {
        Poacher poacher = mock(Poacher.class);
        map.poachers.add(poacher);
        MapSpace spyMap = spy(map);
        doReturn(poacher).when(spyMap).getClosestSelectedAnimalAndPoacher(ranger);
        setField(ranger, "map", spyMap);
        ranger.setTarget();
        assertEquals(poacher, getFieldRecursive(ranger, "target"));
    }

    @Test
    void testInteractWithAnimal() {
        Animal animal = mock(Animal.class);
        when(animal.isAlive()).thenReturn(true);
        ranger.interact(animal);
        verify(animal, atLeastOnce()).attacked(ranger);
    }

    @Test
    void testInteractWithPoacher() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.isAlive()).thenReturn(true);
        ranger.interact(poacher);
        verify(poacher, atLeastOnce()).attacked(ranger);
    }

    @Test
    void testAttackAnimal() {
        Animal animal = mock(Animal.class);
        when(animal.isAlive()).thenReturn(true);
        ranger.attack(animal);
        verify(animal, atLeastOnce()).attacked(ranger);
    }

    @Test
    void testAttackPoacher() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.isAlive()).thenReturn(true);
        ranger.attack(poacher);
        verify(poacher, atLeastOnce()).attacked(ranger);
    }

    @Test
    void testInVisionReturnsPoachersInRadius() throws Exception {
        Poacher p1 = mock(Poacher.class);
        Poacher p2 = mock(Poacher.class);
        when(p1.getX()).thenReturn(0);
        when(p1.getY()).thenReturn(0);
        when(p2.getX()).thenReturn(10000);
        when(p2.getY()).thenReturn(10000);
        Ranger spyRanger = spy(ranger);
        doReturn(0).when(spyRanger).getX();
        doReturn(0).when(spyRanger).getY();
        MapSpace spyMap = spy(map);
        spyMap.poachers.add(p1);
        spyMap.poachers.add(p2);
        setField(spyRanger, "visRadius", 100);
        setField(spyRanger, "map", spyMap);
        Entity[] inVision = spyRanger.inVision();
        assertTrue(java.util.Arrays.asList(inVision).contains(p1));
        assertFalse(java.util.Arrays.asList(inVision).contains(p2));
    }

    @Test
    void testMarkVisibleCallsSetVisible() throws Exception {
        Poacher p1 = mock(Poacher.class);
        when(p1.getX()).thenReturn(0);
        when(p1.getY()).thenReturn(0);
        Ranger spyRanger = spy(ranger);
        doReturn(0).when(spyRanger).getX();
        doReturn(0).when(spyRanger).getY();
        MapSpace spyMap = spy(map);
        spyMap.poachers.add(p1);
        setField(spyRanger, "visRadius", 100);
        setField(spyRanger, "map", spyMap);
        spyRanger.markVisible();
        verify(p1, atLeastOnce()).setVisible();
    }

    @Test
    void testUpdateDiesIfMonthChanges() throws Exception {
        setField(ranger, "purchasedMonth", 1L);
        when(engine.getMonthsPassed()).thenReturn(2L);
        Ranger spyRanger = spy(ranger);
        doNothing().when(spyRanger).die();
        spyRanger.update();
        verify(spyRanger, atLeastOnce()).die();
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
    private long getLongField(Object obj, String field) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.getLong(obj);
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
