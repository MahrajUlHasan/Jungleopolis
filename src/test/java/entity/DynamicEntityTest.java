package entity;

import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class DynamicEntityTest {
    private GameEngine engine;
    private MapSpace map;

    // Minimal concrete DynamicEntity for testing
    static class TestDynamicEntity extends DynamicEntity {
        public boolean interactCalled = false;
        public boolean updateStatsCalled = false;
        public boolean setTargetCalled = false;
        public TestDynamicEntity(GameEngine engine, MapSpace map) {
            super(engine, map);
        }
        @Override
        public void interact(Entity entity) { interactCalled = true; }
        @Override
        protected void updateStats() { updateStatsCalled = true; }
        @Override
        public void setTarget() { setTargetCalled = true; }
        @Override
        public void spawn(MapSpace mapSpace) {
            // Minimal implementation for testing
        }
    }

    @BeforeEach
    void setUp() {
        engine = new GameEngine(null);
        map = new MapSpace(engine);
    }

    @Test
    void testSetAndGetStatus() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        entity.setStatus(Status.HUNT);
        assertEquals(Status.HUNT, entity.getStatus());
    }

    @Test
    void testSetAndGetTarget() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        Entity dummy = new TestDynamicEntity(engine, map);
        entity.setStatus(Status.HUNT, dummy);
        assertEquals(Status.HUNT, entity.getStatus());
        assertEquals(dummy, entity.getTarget());
    }

    @Test
    void testIsAlive() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        assertTrue(entity.isAlive());
        entity.alive = false;
        assertFalse(entity.isAlive());
    }

    @Test
    void testSetDefaultValues() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        entity.setDefaultValues();
        assertEquals(1, entity.velocity);
        assertEquals(engine.tileSize, entity.width);
        assertEquals(engine.tileSize, entity.height);
    }

    @Test
    void testUpdateCallsUpdateStatsAndSetTarget() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map) {
            @Override
            public void move() {}
        };
        engine.canUpdateStats = true;
        entity.target = null;
        entity.update();
        assertTrue(entity.setTargetCalled);
        assertTrue(entity.updateStatsCalled);
    }

    @Test
    void testMoveWithNullTargetDoesNothing() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        entity.target = null;
        entity.move();
        // Should not throw
    }

    @Test
    void testGetHitBox() {
        TestDynamicEntity entity = new TestDynamicEntity(engine, map);
        Rectangle hitBox = entity.getHitBox();
        assertEquals(entity.x, hitBox.x);
        assertEquals(entity.y, hitBox.y);
        assertEquals(entity.width, hitBox.width);
        assertEquals(entity.height, hitBox.height);
    }
}
