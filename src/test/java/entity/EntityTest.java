package entity;

import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {
    static class TestEntity extends Entity {
        public boolean interactCalled = false;
        public boolean updateCalled = false;
        public boolean drawCalled = false;
        public boolean spawnCalled = false;
        public TestEntity() {
            // Set some defaults for testing
            this.x = 10;
            this.y = 20;
            this.width = 30;
            this.height = 40;
        }
        @Override
        public void setDefaultValues() {
            this.x = 1;
            this.y = 2;
            this.width = 3;
            this.height = 4;
        }
        @Override
        public Rectangle getHitBox() {
            return new Rectangle(x, y, width, height);
        }
        @Override
        public void draw(Graphics2D g2d) { drawCalled = true; }
        @Override
        public void update() { updateCalled = true; }
        @Override
        public void interact(Entity entity) { interactCalled = true; }
        @Override
        public void spawn(MapSpace mapSpace) { spawnCalled = true; }
    }

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
    }

    @Test
    void testSetAndGetSelected() {
        assertFalse(entity.isSelected());
        entity.select();
        assertTrue(entity.isSelected());
        entity.deSelect();
        assertFalse(entity.isSelected());
    }

    @Test
    void testSetAndGetVisible() {
        assertFalse(entity.isVisible());
        entity.setVisible();
        assertTrue(entity.isVisible());
        entity.setInvisible();
        assertFalse(entity.isVisible());
    }

    @Test
    void testGetCenterAndCords() {
        Point center = entity.getCenter();
        assertEquals(entity.x + entity.width / 2, center.x);
        assertEquals(entity.y + entity.height / 2, center.y);
        Point cords = entity.getCords();
        assertEquals(entity.x, cords.x);
        assertEquals(entity.y, cords.y);
    }

    @Test
    void testSetSpriteAndHitbox() {
        entity.setSprite(1, 2, 3, 4);
        assertNotNull(entity.sprite);
        entity.setHitbox(5, 6, 7, 8);
        assertEquals(5, entity.x);
        assertEquals(6, entity.y);
        assertEquals(7, entity.width);
        assertEquals(8, entity.height);
    }

    @Test
    void testGetDimensionAndWidthHeight() {
        Dimension dim = entity.getDimension();
        assertEquals(entity.width, dim.width);
        assertEquals(entity.height, dim.height);
        assertEquals(entity.width, entity.getWidth());
        assertEquals(entity.height, entity.getHeight());
    }

    @Test
    void testCollides() {
        TestEntity e2 = new TestEntity();
        e2.x = entity.x + 1;
        e2.y = entity.y + 1;
        assertTrue(entity.collides(e2));
        e2.x = 1000;
        e2.y = 1000;
        assertFalse(entity.collides(e2));
    }

    @Test
    void testDistance() {
        TestEntity e2 = new TestEntity();
        e2.x = entity.x + 3;
        e2.y = entity.y + 4;
        assertEquals(5, entity.distance(e2));
    }

    @Test
    void testSetXAndSetY() {
        entity.setX(123);
        entity.setY(456);
        assertEquals(123, entity.x);
        assertEquals(456, entity.y);
    }

    @Test
    void testSetDefaultValues() {
        entity.setDefaultValues();
        assertEquals(1, entity.x);
        assertEquals(2, entity.y);
        assertEquals(3, entity.width);
        assertEquals(4, entity.height);
    }

    @Test
    void testDrawUpdateInteractSpawn() {
        entity.draw(null);
        entity.update();
        entity.interact(entity);
        entity.spawn(null);
        assertTrue(entity.drawCalled);
        assertTrue(entity.updateCalled);
        assertTrue(entity.interactCalled);
        assertTrue(entity.spawnCalled);
    }
}
