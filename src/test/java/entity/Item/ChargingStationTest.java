package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChargingStationTest {
    private GameEngine engine;
    private MapSpace map;
    private ChargingStation station;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.chargingStations = new ArrayList<>();
        station = new TestChargingStation(engine);
    }

    static class TestChargingStation extends ChargingStation {
        public TestChargingStation(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
    }

    @Test
    void testConstructorSetsPathsAndId() {
        assertEquals("/surveilance/ChargingStation/chargingStationUp1.png", getFieldRecursive(station, "upPath1"));
        assertEquals("/surveilance/ChargingStation/chargingStationUp2.png", getFieldRecursive(station, "upPath2"));
        int id = station.getId();
        assertTrue(id > 0);
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        station.updateStats();
    }

    @Test
    void testSpawnAddsToMapChargingStations() {
        map.chargingStations.clear();
        station.spawn(map);
        assertTrue(map.chargingStations.contains(station));
    }

    @Test
    void testInteractWithDroneRechargesBattery() {
        Drone drone = mock(Drone.class);
        station.interact(drone);
        verify(drone, atLeastOnce()).rechargeBattery();
    }

    @Test
    void testInteractWithNonDroneDelegatesToSuper() {
        entity.DynamicEntity entity = mock(entity.DynamicEntity.class);
        station.interact(entity);
        // No exception, just delegates to super
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
}
