package entity.Item;

import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DronePatrolFlagTest {
    private GameEngine engine;
    private MapSpace map;
    private DronePatrolFlag flag;

    static class TestDronePatrolFlag extends DronePatrolFlag {
        public TestDronePatrolFlag(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override protected void setRandPos() { /* no-op for tests */ }
        @Override public void setDefaultValues() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.dronePatrolFlags = new ArrayList<>();
        map.dronePathHandler = mock(entity.util.PathHandler.class);
        flag = new TestDronePatrolFlag(engine);
    }

    @Test
    void testConstructorAssignsUniqueId() {
        DronePatrolFlag flag2 = new TestDronePatrolFlag(engine);
        assertNotEquals(flag.getId(), flag2.getId());
    }

    @Test
    void testUpdateStatsDoesNothing() {
        // Should not throw or change state
        flag.updateStats();
    }

    @Test
    void testSpawnAddsToMapAndPathHandler() {
        map.dronePatrolFlags.clear();
        flag.spawn(map);
        assertTrue(map.dronePatrolFlags.contains(flag));
        verify(map.dronePathHandler, atLeastOnce()).addNode(flag);
    }

    @Test
    void testInteractWithDroneCallsSetTargetIfCollides() {
        Drone drone = mock(Drone.class);
        when(drone.collides(flag)).thenReturn(true);
        flag.interact(drone);
        verify(drone, atLeastOnce()).setTarget();
    }

    @Test
    void testInteractWithDroneDoesNotCallSetTargetIfNotCollides() {
        Drone drone = mock(Drone.class);
        when(drone.collides(flag)).thenReturn(false);
        flag.interact(drone);
        verify(drone, never()).setTarget();
    }

    @Test
    void testInteractWithNonDroneDoesNothing() {
        // Use a DynamicEntity mock to avoid ClassCastException in Building.interact
        entity.DynamicEntity entity = mock(entity.DynamicEntity.class);
        // Should not throw or call setTarget
        flag.interact(entity);
    }
}
