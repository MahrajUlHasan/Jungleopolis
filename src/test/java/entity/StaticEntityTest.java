package entity;

import org.example.GameEngine;
import org.example.MapSpace;
import org.example.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

class StaticEntityTest {
    static class DummyStaticEntity extends StaticEntity {
        public boolean updateStatsCalled = false;
        public boolean interactCalled = false;
        public boolean spawnCalled = false;
        public DummyStaticEntity(GameEngine engine) {
            super(engine);
        }
        @Override
        protected void updateStats() { updateStatsCalled = true; }
        @Override
        public void interact(Entity entity) { interactCalled = true; }
        @Override
        public void spawn(MapSpace mapSpace) { spawnCalled = true; }
    }

    private GameEngine engine;
    private MapSpace map;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(null);
        map = new MapSpace(engine);
    }

    @Test
    void testSetDefaultValuesSetsFields() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.setDefaultValues();
        assertEquals(Direction.UP, entity.direction);
        assertEquals(engine.tileSize, entity.width);
        assertEquals(engine.tileSize, entity.height);
        assertTrue(entity.capacity > 0);
    }

    @Test
    void testSetRandPosSetsWithinBounds() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.setRandPos();
        assertTrue(entity.x >= 0 && entity.x < engine.screenWidth);
        assertTrue(entity.y >= 0 && entity.y < engine.screenHeight);
    }

    @Test
    void testDetermineCapacitySetsCapacity() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.width = engine.tileSize * 2;
        entity.height = engine.tileSize * 3;
        entity.determineCapacity();
        assertEquals(2 * ((3) + (2)), entity.capacity);
    }

    @Test
    void testIsAtCapacity() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.capacity = 5;
        entity.visitors = 5;
        assertTrue(entity.isAtCapacity());
        entity.visitors = 4;
        assertFalse(entity.isAtCapacity());
    }

    @Test
    void testGetHitBox() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.x = 10;
        entity.y = 20;
        entity.width = 30;
        entity.height = 40;
        Rectangle hitBox = entity.getHitBox();
        assertEquals(10, hitBox.x);
        assertEquals(20, hitBox.y);
        assertEquals(30, hitBox.width);
        assertEquals(40, hitBox.height);
    }

    @Test
    void testDrawDoesNotThrow() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.sprite = entity.new Sprite(0, 0, 10, 10);
        Graphics2D g2d = (Graphics2D) new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).getGraphics();
        assertDoesNotThrow(() -> entity.draw(g2d));
    }

    @Test
    void testUpdateCallsUpdateStats() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        engine.canUpdateStats = true;
        entity.sprite = entity.new Sprite(0, 0, 10, 10);
        entity.update();
        assertTrue(entity.updateStatsCalled);
    }

    @Test
    void testSpawnAddsToEntitiesList() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.spawn(map);
        assertTrue(entity.spawnCalled);
    }

    @Test
    void testSetHeightAndSetWidth() {
        DummyStaticEntity entity = new DummyStaticEntity(engine);
        entity.sprite = entity.new Sprite(0, 0, 10, 10);
        entity.setHeight(50);
        assertEquals(50, entity.sprite.width);
        assertEquals(50, entity.width);
        entity.setWidth(60);
        assertEquals(60, entity.sprite.height);
        assertEquals(60, entity.height);
    }
}
