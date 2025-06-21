package entity.Item;

import Gui.GUI;
import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AirShipTest {
    private GameEngine engine;
    private MapSpace map;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private AirShip airShip;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(mock(GUI.class));
        map = new MapSpace(engine);
        map.airshipPathHandler = new entity.util.PathHandler();
        keyHandler = mock(KeyHandler.class);
        mouseHandler = mock(MouseHandler.class);
        airShip = new TestAirShip(engine, map, keyHandler, mouseHandler);
    }

    // Test subclass to override protected methods for move tests
    static class TestAirShip extends AirShip {
        boolean collidesOrIntersectsValue = false;
        int xDeltaValue = 1;
        int yDeltaValue = 1;
        public TestAirShip(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
            super(engine, m, kH, mH);
        }
        @Override
        protected boolean collidesOrIntersects(Entity target) {
            return collidesOrIntersectsValue;
        }
        @Override
        protected int xDelta(Entity target) {
            return xDeltaValue;
        }
        @Override
        protected int yDelta(Entity target) {
            return yDeltaValue;
        }
    }
    static class TestAirShipPatrolFlag extends AirShipPatrolFlag {
        public TestAirShipPatrolFlag(GameEngine engine) {
            super(engine);
        }
        @Override
        public void getSpriteImage() {
            // Do nothing to avoid loading images in tests
        }
    }
    static class DummyEntity extends Entity {
        public DummyEntity(int x, int y) { this.x = x; this.y = y; }
        @Override public void setDefaultValues() {}
        @Override public void spawn(org.example.MapSpace map) {}
        @Override public void interact(Entity entity) {}
        @Override public void update() {}
        @Override public void draw(java.awt.Graphics2D g2d) {}
        @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(x, y, 1, 1); }
    }

    @Test
    void testConstructorAndDefaultValues() {
        assertEquals(3, getIntField(airShip, "velocity"));
        assertEquals(32 * 7, airShip.getVisRadius());
        assertEquals(org.example.Direction.UP, airShip.direction);
    }
    @Test
    void testSetDefaultValues() {
        airShip.setDefaultValues();
        assertEquals(3, getIntField(airShip, "velocity"));
        assertEquals(32, airShip.width);
        assertEquals(32, airShip.height);
    }
    @Test
    void testUpdateStats() {
        airShip.updateStats();
    }

    @Test
    void testInteractWithPatrolFlag() {
        Entity flag = mock(AirShipPatrolFlag.class);
        airShip.interact(flag);
    }

    @Test
    void testInteractWithAnotherAirShip() {
        AirShip other = mock(AirShip.class);
        int oldX = airShip.x;
        int oldY = airShip.y;
        boolean moved = false;
        // Try up to 10 times to account for random movement
        for (int i = 0; i < 10; i++) {
            airShip.interact(other);
            if (airShip.x != oldX || airShip.y != oldY) {
                moved = true;
                break;
            }
        }
        assertTrue(moved, "AirShip should have moved after interacting with another AirShip");
    }

    @Test
    void testSpawn() {
        map.entities = new ArrayList<>();
        map.dynamicEntities = new ArrayList<>();
        map.airships = new ArrayList<>();
        airShip.spawn(map);
        assertTrue(map.entities.contains(airShip));
        assertTrue(map.dynamicEntities.contains(airShip));
        assertTrue(map.airships.contains(airShip));
    }

    @Test
    void testSetTargetNoNodes() {
        map.airshipPathHandler = new entity.util.PathHandler();
        airShip.setTarget();
        assertNull(getTarget(airShip));
    }

    private void setIntFieldRecursive(Object obj, String field, int value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                f.setInt(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
    }

    @Test
    void testSetTargetWithNodes() {
        map.airshipPathHandler = new entity.util.PathHandler();
        AirShipPatrolFlag flag1 = new TestAirShipPatrolFlag(engine);
        flag1.x = 100;
        flag1.y = 100;
        AirShipPatrolFlag flag2 = new TestAirShipPatrolFlag(engine);
        flag2.x = 200;
        flag2.y = 200;
        airShip.x = 0;
        airShip.y = 0;
        map.airshipPathHandler.addNode(flag1);
        map.airshipPathHandler.addNode(flag2);
        // Print the declaring class for currentNodeIndex
        Class<?> c = airShip.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField("currentNodeIndex");
                System.out.println("DEBUG: currentNodeIndex declared in " + c.getName());
                break;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        setIntFieldRecursive(airShip, "currentNodeIndex", -1);
        System.out.println("DEBUG: After setIntFieldRecursive, currentNodeIndex=" + getIntField(airShip, "currentNodeIndex"));
        setFieldRecursive(airShip, "patrolling", true);
        // Print patrolling value
        Object patrollingValue = null;
        try {
            patrollingValue = getFieldRecursive(airShip, "patrolling");
        } catch (Exception e) {
            System.out.println("DEBUG: Exception getting patrolling: " + e);
        }
        System.out.println("DEBUG: patrolling=" + patrollingValue);
        setTargetField(airShip, null);
        ((TestAirShip)airShip).collidesOrIntersectsValue = false;
        setFieldRecursive(airShip, "map", map);
        assertSame(map, getFieldRecursive(airShip, "map"), "airShip.map should be the test map");
        System.out.println("DEBUG: airShip.map.airshipPathHandler nodes: " + map.airshipPathHandler.getNodes());
        System.out.println("DEBUG: Before setTarget, currentNodeIndex=" + getIntField(airShip, "currentNodeIndex"));
        airShip.setTarget();
        System.out.println("DEBUG: After setTarget, currentNodeIndex=" + getIntField(airShip, "currentNodeIndex"));
        Entity tgt = getTarget(airShip);
        if (tgt == null) {
            System.out.println("DEBUG: target is null after setTarget");
        } else {
            System.out.println("DEBUG: target set to " + tgt + " at (" + tgt.x + "," + tgt.y + ")");
        }
        assertNotNull(tgt);
    }

    @Test
    void testInVision() {
        map.poachers = new ArrayList<>();
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(airShip.getX());
        when(poacher.getY()).thenReturn(airShip.getY());
        map.poachers.add(poacher);
        Entity[] inVision = airShip.inVision();
        assertEquals(1, inVision.length);
    }

    @Test
    void testMarkVisible() {
        map.poachers = new ArrayList<>();
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(airShip.getX());
        when(poacher.getY()).thenReturn(airShip.getY());
        map.poachers.add(poacher);
        airShip.markVisible();
        verify(poacher, atLeastOnce()).setVisible();
    }

    @Test
    void testMoveWithTargetNoCollision() {
        Entity target = new DummyEntity(101, 101);
        setFieldRecursive(airShip, "target", target);
        setIntField(airShip, "velocity", 1);
        airShip.x = 100;
        airShip.y = 100;
        ((TestAirShip)airShip).collidesOrIntersectsValue = false;
        ((TestAirShip)airShip).xDeltaValue = 1;
        ((TestAirShip)airShip).yDeltaValue = 1;
        int oldX = airShip.x;
        int oldY = airShip.y;
        airShip.move();
        assertEquals(oldX + 1, airShip.x);
        assertEquals(oldY + 1, airShip.y);
    }

    @Test
    void testMoveWithTargetAndCollision() {
        Entity target = mock(Entity.class);
        setTargetField(airShip, target);
        engine.canUpdateStats = true;
        ((TestAirShip)airShip).collidesOrIntersectsValue = true;
        airShip.move();
        assertNull(getTarget(airShip));
    }

    // Helper to access protected fields in DynamicEntity
    private int getIntField(Object obj, String field) {
        try {
            var f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception e) {
            return -1;
        }
    }
    private void setIntField(Object obj, String field, int value) {
        try {
            var f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
            f.setAccessible(true);
            f.setInt(obj, value);
        } catch (Exception ignored) {}
    }
    private Entity getTarget(Object obj) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField("target");
                f.setAccessible(true);
                return (Entity) f.get(obj);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    private void setTargetField(Object obj, Entity value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField("target");
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field 'target' not found");
    }

    // Helper to set a field by searching up the class hierarchy
    private void setFieldRecursive(Object obj, String field, Object value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(field);
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Field '" + field + "' not found");
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

    // Remove unused helpers for Mockito on protected methods
}
