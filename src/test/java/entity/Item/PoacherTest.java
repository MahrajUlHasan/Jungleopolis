package entity.Item;

import entity.Animal;
import entity.Entity;
import org.example.Direction;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PoacherTest {
    private GameEngine engine;
    private MapSpace map;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private Poacher poacher;

    static class TestPoacher extends Poacher {
        public TestPoacher(GameEngine engine, MapSpace map, KeyHandler keyH, MouseHandler mH) {
            super(engine, map, keyH, mH);
        }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override public void changeDirection() { /* no-op for tests */ }
        @Override public void setDefaultValues() {
            // Override to avoid random positioning during tests
            velocity = 1;
            direction = Direction.UP;
            width = engine.tileSize;
            height = engine.tileSize;
        }
        @Override public Rectangle getHitBox() { return new Rectangle(0, 0, 10, 10); }
        @Override public void draw(Graphics2D g2d) { /* no-op for tests */ }
        @Override public void update() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        // Use reflection to set the field since we can't mock fields directly
        setField(engine, "tileSize", 48);
        
        map = new MapSpace(engine);
        keyHandler = mock(KeyHandler.class);
        mouseHandler = mock(MouseHandler.class);
        
        when(engine.getMap()).thenReturn(map);
        
        // Initialize the poacher
        poacher = new TestPoacher(engine, map, keyHandler, mouseHandler);
        
        // Initialize or clear lists that will be used
        if (map.people == null) map.people = new ArrayList<>();
        else map.people.clear();
        
        if (map.poachers == null) map.poachers = new ArrayList<>();
        else map.poachers.clear();
        
        if (map.dynamicEntities == null) map.dynamicEntities = new ArrayList<>();
        else map.dynamicEntities.clear();
        
        // These are final fields, so we can only clear them
        map.entitiesToAdd.clear();
        map.entitiesToRemove.clear();
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(poacher);
        assertEquals(engine, getFieldRecursive(poacher, "engine"));
        assertEquals(map, getFieldRecursive(poacher, "map"));
        // The keyHandler and mouseHandler aren't being set in the Poacher constructor
        // so we won't test for them
        
        // Check path variables are correctly set
        assertEquals("/People/Poachers/up1.png", getFieldRecursive(poacher, "upPath1"));
        assertEquals("/People/Poachers/up2.png", getFieldRecursive(poacher, "upPath2"));
        assertEquals("/People/Poachers/down1.png", getFieldRecursive(poacher, "downPath1"));
        assertEquals("/People/Poachers/down2.png", getFieldRecursive(poacher, "downPath2"));
        assertEquals("/People/Poachers/right1.png", getFieldRecursive(poacher, "rightPath1"));
        assertEquals("/People/Poachers/right2.png", getFieldRecursive(poacher, "rightPath2"));
        assertEquals("/People/Poachers/left1.png", getFieldRecursive(poacher, "leftPath1"));
        assertEquals("/People/Poachers/left2.png", getFieldRecursive(poacher, "leftPath2"));
    }
    
    @Test
    void testGetAttackPower() {
        int attackPower = 20; // Default value in Poacher class
        assertEquals(attackPower, poacher.getAttackPower());
    }
    
    @Test
    void testSetUnderPersueAndIsUnderPersue() {
        // Default should be false
        assertFalse(poacher.isUnderPersue());
        
        // Test setting to true
        poacher.setUnderPersue(true);
        assertTrue(poacher.isUnderPersue());
        
        // Test setting back to false
        poacher.setUnderPersue(false);
        assertFalse(poacher.isUnderPersue());
    }
    
    @Test
    void testAttack() {
        Animal animal = mock(Animal.class);
        // Use doReturn to avoid issues with when()
        doReturn(true).when(animal).isAlive();
        
        poacher.attack(animal);
        // We're not testing the Animal class's attacked method, just that it was called
        verify(animal).attacked(any(Poacher.class));
    }
    
    @Test
    void testAttackDoesNothingIfAnimalIsNotAlive() {
        Animal animal = mock(Animal.class);
        // Use doReturn to avoid issues with when()
        doReturn(false).when(animal).isAlive();
        
        poacher.attack(animal);
        // We're not testing the Animal class's attacked method, just that it was never called
        verify(animal, never()).attacked(any(Poacher.class));
    }
    
    @Test
    void testSpawnAddsToPoachersList() {
        poacher.spawn(map);
        assertTrue(map.entitiesToAdd.contains(poacher));
        assertTrue(map.people.contains(poacher));
        assertTrue(map.poachers.contains(poacher));
    }
    
    @Test
    void testDieRemovesFromLists() {
        // First add the poacher to relevant lists
        map.people.add(poacher);
        map.poachers.add(poacher);
        map.dynamicEntities.add(poacher);
        
        // Ensure poacher is alive initially
        setField(poacher, "alive", true);
        assertTrue((Boolean) getFieldRecursive(poacher, "alive"));
        
        // Call die method
        callMethod(poacher, "die");
        
        // Check if poacher is removed from lists and marked not alive
        assertFalse((Boolean) getFieldRecursive(poacher, "alive"));
        assertFalse(map.people.contains(poacher));
        assertFalse(map.poachers.contains(poacher));
        assertTrue(map.entitiesToRemove.contains(poacher));
        assertFalse(map.dynamicEntities.contains(poacher));
    }
    
    @Test
    void testSetTargetWhenNotUnderPersue() {
        // Create a mock MapSpace that returns what we want
        MapSpace mockMap = mock(MapSpace.class);
        Entity animal = mock(Entity.class);
        // Use doReturn for clearer stubbing
        doReturn(animal).when(mockMap).getClosestEntity(anyString(), any(Entity.class));
        
        // Create a poacher with the mock map
        Poacher poacherWithMockMap = new TestPoacher(engine, mockMap, keyHandler, mouseHandler);
        
        poacherWithMockMap.setTarget();
        
        // Target should be the closest animal
        assertEquals(animal, getFieldRecursive(poacherWithMockMap, "target"));
    }
    
    @Test
    void testSetTargetWhenUnderPersue() {
        // Create a mock MapSpace that returns what we want
        MapSpace mockMap = mock(MapSpace.class);
        Entity escapePath = mock(Entity.class);
        // Use doReturn for clearer stubbing
        doReturn(escapePath).when(mockMap).getFurthestEntity(any(Entity.class));
        
        // Create a poacher with the mock map
        Poacher poacherWithMockMap = new TestPoacher(engine, mockMap, keyHandler, mouseHandler);
        
        poacherWithMockMap.setUnderPersue(true);
        poacherWithMockMap.setTarget();
        
        // Target should be the furthest entity (escape path)
        assertEquals(escapePath, getFieldRecursive(poacherWithMockMap, "target"));
    }
    
    @Test
    void testInteractWithAnimalKills() {
        // Create mock animal
        Animal animal = mock(Animal.class);
        // Simulate animal is alive for attack, then dead after
        when(animal.isAlive()).thenReturn(true, false);
        // Set difficulty level to 1
        doReturn(1).when(engine).getDifficulty();
        // Call interact method
        poacher.interact(animal);
        // attacked should be called once
        verify(animal).attacked(any(Poacher.class));
        // Poacher should die after killing one animal (when difficulty is 1)
        assertFalse((Boolean) getFieldRecursive(poacher, "alive"));
    }
    
    @Test
    void testInteractWithAnimalDoesntKillEnough() {
        // Create mock animal
        Animal animal = mock(Animal.class);
        // Simulate animal is alive for attack, then dead after
        when(animal.isAlive()).thenReturn(true, false);
        // Set difficulty level to 2
        doReturn(2).when(engine).getDifficulty();
        // Call interact method
        poacher.interact(animal);
        // attacked should be called once
        verify(animal).attacked(any(Poacher.class));
        // Poacher should still be alive (need to kill 2 animals)
        assertTrue((Boolean) getFieldRecursive(poacher, "alive"));
    }
    
    @Test
    void testAttackedByRanger() {
        // Create a mock ranger with attack power
        Ranger ranger = mock(Ranger.class);
        when(ranger.getAttackPower()).thenReturn(50);
        
        // Set poacher's health to 100
        setField(poacher, "health", 100);
        
        // Ranger attacks poacher
        poacher.attacked(ranger);
        
        // Health should be reduced
        assertEquals(50, getFieldRecursive(poacher, "health"));
        
        // Poacher should still be alive
        assertTrue((Boolean) getFieldRecursive(poacher, "alive"));
        
        // Ranger attacks again
        poacher.attacked(ranger);
        
        // Health should be 0, poacher should be dead
        assertEquals(0, getFieldRecursive(poacher, "health"));
        assertFalse((Boolean) getFieldRecursive(poacher, "alive"));
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

    private void callMethod(Object obj, String methodName, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var method = c.getDeclaredMethod(methodName, argTypes);
                method.setAccessible(true);
                method.invoke(obj, args);
                return;
            } catch (NoSuchMethodException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Method '" + methodName + "' not found");
    }
}
