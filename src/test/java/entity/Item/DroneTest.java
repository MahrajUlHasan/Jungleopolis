package entity.Item;

import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DroneTest {
    private GameEngine engine;
    private MapSpace map;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private Drone drone;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        keyHandler = mock(KeyHandler.class);
        mouseHandler = mock(MouseHandler.class);
        when(engine.getMap()).thenReturn(map);
        when(engine.getTimePassed()).thenReturn("0:0:0:0");
        map.chargingStations = new ArrayList<>();
        map.drones = new ArrayList<>();
        map.entities = new ArrayList<>();
        map.dynamicEntities = new ArrayList<>();
        map.dronePathHandler = mock(entity.util.PathHandler.class);
        map.poachers = new ArrayList<>();
        drone = new TestDrone(engine, map, keyHandler, mouseHandler);
    }

    static class TestDrone extends Drone {
        boolean collidesOrIntersectsValue = false;
        int xDeltaValue = 1;
        int yDeltaValue = 1;

        public TestDrone(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
            super(engine, m, kH, mH);
        }

        @Override
        public void getSpriteImage() { /* no-op for tests */ }

        @Override
        protected boolean collidesOrIntersects(Entity target) { return collidesOrIntersectsValue; }

        @Override
        protected int xDelta(Entity target) { return xDeltaValue; }

        @Override
        protected int yDelta(Entity target) { return yDeltaValue; }
    }

    @Test
    void testConstructorAndDefaultValues() {
        assertTrue(drone.hasBattery());
        assertFalse(drone.isRecharging());
        // batteryEndDay = currentDay + 1, rechargeEndDay = 0, so difference is 1
        assertEquals(1, drone.getBatteryEndDay() - drone.getRechargeEndDay());
    }

    @Test
    void testUpdateStatsNormalBattery() {
        when(engine.getTimePassed()).thenReturn("0:0:0:0");
        drone.updateStats();
        assertTrue(drone.hasBattery());
        assertFalse(drone.isRecharging());
    }

    @Test
    void testUpdateStatsBatteryDepleted() {
        when(engine.getTimePassed()).thenReturn("0:0:2:0"); // 2 days later
        setFieldRecursive(drone, "batteryEndDay", 1L);
        drone.updateStats();
        assertFalse(drone.hasBattery());
    }

    @Test
    void testUpdateStatsRecharging() {
        setFieldRecursive(drone, "isRecharging", true);
        setFieldRecursive(drone, "rechargeEndDay", 1L);
        when(engine.getTimePassed()).thenReturn("0:0:2:0"); // 2 days later
        drone.updateStats();
        assertTrue(drone.hasBattery());
        assertFalse(drone.isRecharging());
    }

    @Test
    void testSetTargetRechargingDoesNothing() {
        setFieldRecursive(drone, "isRecharging", true);
        drone.setTarget();
        assertNull(getFieldRecursive(drone, "target"));
    }

    @Test
    void testSetTargetBatteryDepletedFindsChargingStation() {
        setFieldRecursive(drone, "hasBattery", false);
        ChargingStation station = mock(ChargingStation.class);
        map.chargingStations.add(station);
        drone.setTarget();
        assertEquals(station, getFieldRecursive(drone, "target"));
    }

    @Test
    void testSetTargetNormalPatrol() {
        setFieldRecursive(drone, "hasBattery", true);
        when(map.dronePathHandler.hasNodes()).thenReturn(true);
        List<PathNode> nodes = new ArrayList<>();
        // Use a test double that is both Entity and PathNode
        class TestFlag extends Entity implements PathNode {
            @Override public int getId() { return 1; }
            @Override public void setDefaultValues() {}
            @Override public void spawn(MapSpace map) {}
            @Override public void interact(Entity entity) {}
            @Override public void update() {}
            @Override public void draw(java.awt.Graphics2D g2d) {}
            @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(x, y, 1, 1); }
        }
        PathNode flag = new TestFlag();
        nodes.add(flag);
        when(map.dronePathHandler.getNodes()).thenReturn(nodes);
        setFieldRecursive(drone, "target", null);
        drone.setTarget();
        assertEquals(flag, getFieldRecursive(drone, "target"));
    }

    @Test
    void testRechargeBatterySetsRecharging() {
        drone.rechargeBattery();
        assertTrue(drone.isRecharging());
        assertEquals(1, drone.getRechargeEndDay());
    }

    @Test
    void testInteractWithDronePatrolFlag() {
        setFieldRecursive(drone, "hasBattery", true);
        setFieldRecursive(drone, "isRecharging", false);
        DronePatrolFlag flag = mock(DronePatrolFlag.class);
        drone.interact(flag);
        // Should call setTarget (no exception)
    }

    @Test
    void testInteractWithChargingStationTriggersRecharge() {
        setFieldRecursive(drone, "hasBattery", false);
        setFieldRecursive(drone, "isRecharging", false);
        ChargingStation station = mock(ChargingStation.class);
        drone.interact(station);
        assertTrue(drone.isRecharging());
    }

    @Test
    void testSpawnAddsToMapIfChargingStationExists() {
        map.chargingStations.add(mock(ChargingStation.class));
        map.drones.clear();
        map.entities.clear();
        map.dynamicEntities.clear();
        drone.spawn(map);
        assertTrue(map.drones.contains(drone));
        assertTrue(map.entities.contains(drone));
        assertTrue(map.dynamicEntities.contains(drone));
    }

    @Test
    void testSpawnDoesNothingIfNoChargingStation() {
        map.chargingStations.clear();
        map.drones.clear();
        drone.spawn(map);
        assertFalse(map.drones.contains(drone));
    }

    @Test
    void testSetDefaultValues() {
        drone.setDefaultValues();
        assertEquals(3, getIntField(drone, "velocity"));
    }

    @Test
    void testGetVisRadius() {
        assertEquals(7 * 32, drone.getVisRadius());
    }

    @Test
    void testInVisionReturnsPoachersInRadius() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(0);
        when(poacher.getY()).thenReturn(0);
        drone.x = 0;
        drone.y = 0;
        map.poachers.add(poacher);
        Entity[] inVision = drone.inVision();
        assertEquals(1, inVision.length);
        // Out of range
        when(poacher.getX()).thenReturn(10000);
        when(poacher.getY()).thenReturn(10000);
        assertEquals(0, drone.inVision().length);
    }

    @Test
    void testMarkVisibleCallsSetVisibleOnInVision() {
        Poacher poacher = mock(Poacher.class);
        when(poacher.getX()).thenReturn(0);
        when(poacher.getY()).thenReturn(0);
        drone.x = 0;
        drone.y = 0;
        map.poachers.add(poacher);
        drone.markVisible();
        verify(poacher, atLeastOnce()).setVisible();
    }

    @Test
    void testMoveWithTargetNoCollision() {
        Entity target = mock(Entity.class);
        setFieldRecursive(drone, "target", target);
        setIntField(drone, "velocity", 1);
        drone.x = 100;
        drone.y = 100;
        ((TestDrone)drone).collidesOrIntersectsValue = false;
        ((TestDrone)drone).xDeltaValue = 1;
        ((TestDrone)drone).yDeltaValue = 1;
        int oldX = drone.x;
        int oldY = drone.y;
        drone.move();
        assertEquals(oldX + 1, drone.x);
        assertEquals(oldY + 1, drone.y);
    }

    @Test
    void testMoveWithTargetAndCollision() {
        Entity target = mock(Entity.class);
        setFieldRecursive(drone, "target", target);
        engine.canUpdateStats = true;
        ((TestDrone)drone).collidesOrIntersectsValue = true;
        drone.move();
        assertNull(getFieldRecursive(drone, "target"));
    }

    // Helper to get int field by reflection
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
    private void setIntField(Object obj, String field, int value) {
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
}
