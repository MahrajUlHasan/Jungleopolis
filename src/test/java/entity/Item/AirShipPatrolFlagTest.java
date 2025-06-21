package entity.Item;

import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AirShipPatrolFlagTest {
    private GameEngine engine;
    private MapSpace map;
    private AirShipPatrolFlag flag;

    static class TestFlag extends AirShipPatrolFlag {
        public TestFlag(GameEngine engine) { super(engine); }
        @Override public void getSpriteImage() { /* no-op for tests */ }
    }

    @BeforeEach
    public void setUp() {
        engine = mock(GameEngine.class);
        // Use a real MapSpace so all fields are initialized
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        flag = new TestFlag(engine);
    }

    @Test
    public void testConstructorSetsIdAndNonCollidable() {
        AirShipPatrolFlag flag1 = new TestFlag(engine);
        AirShipPatrolFlag flag2 = new TestFlag(engine);
        assertNotEquals(flag1.getId(), flag2.getId());
        assertFalse(flag1.COLLIDABLE);
    }

    @Test
    public void testUpdateStatsDoesNotThrow() {
        flag.updateStats();
    }

    @Test
    public void testGetIdReturnsUniqueId() {
        int id1 = flag.getId();
        AirShipPatrolFlag flag2 = new TestFlag(engine);
        int id2 = flag2.getId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testInteractWithAirShipCallsSetTargetIfCollides() {
        AirShip airShip = mock(AirShip.class);
        when(airShip.collides(flag)).thenReturn(true);
        flag.interact(airShip);
        verify(airShip, atLeastOnce()).setTarget();
    }

    @Test
    public void testInteractWithAirShipDoesNotCallSetTargetIfNotCollides() {
        AirShip airShip = mock(AirShip.class);
        when(airShip.collides(flag)).thenReturn(false);
        flag.interact(airShip);
        verify(airShip, never()).setTarget();
    }

    @Test
    public void testInteractWithNonAirShipDoesNotThrow() {
        class DummyDynamicEntity extends entity.DynamicEntity {
            public DummyDynamicEntity(GameEngine engine, MapSpace map) { super(engine, map); }
            @Override public void setDefaultValues() {}
            @Override public void update() {}
            @Override public void draw(java.awt.Graphics2D g2d) {}
            @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(0,0,1,1); }
            @Override public void updateStats() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace map) {}
            @Override public void setTarget() {}
        }
        Entity entity = new DummyDynamicEntity(engine, map);
        flag.interact(entity);
    }

    @Test
    public void testSpawnAddsToMapAndPathHandler() {
        map.airShipPatrolFlags.clear();
        map.airshipPathHandler = new entity.util.PathHandler();
        map.entitiesToAdd.clear();
        AirShipPatrolFlag flag = new TestFlag(engine);
        flag.spawn(map);
        assertTrue(map.airShipPatrolFlags.contains(flag));
        assertTrue(map.airshipPathHandler.getNodes().contains(flag));
    }
}