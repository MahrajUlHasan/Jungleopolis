package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuelingStationTest {
    private GameEngine engine;
    private MapSpace map;
    private FuelingStation station;

    static class TestFuelingStation extends FuelingStation {
        public TestFuelingStation(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
        @Override protected void setRandPos() { /* no-op for tests */ }
        @Override public void setDefaultValues() { /* no-op for tests */ }
    }

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        station = new TestFuelingStation(engine);
    }

    @Test
    void testConstructor() {
        assertNotNull(station);
        assertEquals(0, station.getId());
    }

    @Test
    void testUpdateStatsDoesNothing() {
        station.updateStats(); // Should not throw
    }

    @Test
    void testInteractCallsSuper() {
        // Should not throw or do anything
        Entity entity = mock(entity.DynamicEntity.class);
        station.interact(entity);
    }
}
