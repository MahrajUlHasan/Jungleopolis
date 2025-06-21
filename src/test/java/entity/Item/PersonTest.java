package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonTest {
    private GameEngine engine;
    private MapSpace map;
    private Person person;

    static class TestPerson extends Person {
        public TestPerson(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override public void changeDirection() { /* no-op for tests */ }
        @Override public void setDefaultValues() { /* no-op for tests */ }
        @Override public Rectangle getHitBox() { return new Rectangle(0, 0, 1, 1); }
        @Override public void draw(Graphics2D g2d) { /* no-op for tests */ }
        @Override public void update() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        person = new TestPerson(engine, map);
        
        // Initialize or clear lists
        if (map.people == null) map.people = new ArrayList<>();
        else map.people.clear();
        
        if (map.dynamicEntities == null) map.dynamicEntities = new ArrayList<>();
        else map.dynamicEntities.clear();
        
        // These are final fields, so we can only clear them
        map.entitiesToAdd.clear();
        map.entitiesToRemove.clear();
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(person);
        assertEquals(engine, getFieldRecursive(person, "engine"));
        assertEquals(map, getFieldRecursive(person, "map"));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        assertDoesNotThrow(() -> person.updateStats());
    }

    @Test
    void testSetTargetDoesNotThrow() {
        assertDoesNotThrow(() -> person.setTarget());
    }

    @Test
    void testInteractDoesNotThrow() {
        Entity entity = mock(Entity.class);
        assertDoesNotThrow(() -> person.interact(entity));
    }

    @Test
    void testSpawnAddsToPeopleAndEntitiesToAddLists() {
        person.spawn(map);
        assertTrue(map.entitiesToAdd.contains(person));
        assertTrue(map.people.contains(person));
    }

    @Test
    void testDieRemovesFromListsAndMarksNotAlive() {
        // First add the person to relevant lists
        map.people.add(person);
        map.dynamicEntities.add(person);
        
        // Ensure person is alive initially
        setField(person, "alive", true);
        assertTrue((Boolean) getFieldRecursive(person, "alive"));
        
        // Call die method
        callMethod(person, "die");
        
        // Check if person is removed from lists and marked not alive
        assertFalse((Boolean) getFieldRecursive(person, "alive"));
        assertFalse(map.people.contains(person));
        assertTrue(map.entitiesToRemove.contains(person));
        assertFalse(map.dynamicEntities.contains(person));
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
