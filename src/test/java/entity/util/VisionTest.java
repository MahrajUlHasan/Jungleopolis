package entity.util;

import entity.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisionTest {
    static class DummyVision implements Vision {
        private final int radius;
        private final Entity[] entities;
        private boolean marked = false;
        DummyVision(int radius, Entity[] entities) {
            this.radius = radius;
            this.entities = entities;
        }
        @Override
        public int getVisRadius() { return radius; }
        @Override
        public Entity[] inVision() { return entities; }
        @Override
        public void markVisible() { marked = true; }
        public boolean isMarked() { return marked; }
    }

    @Test
    void testGetVisRadius() {
        Vision v = new DummyVision(7, new Entity[0]);
        assertEquals(7, v.getVisRadius());
    }

    @Test
    void testInVisionReturnsEntities() {
        Entity e1 = new Entity() {
            @Override
            public void spawn(org.example.MapSpace mapSpace) {}
            @Override
            public void interact(entity.Entity other) {}
            @Override
            public void draw(java.awt.Graphics2D g2d) {}
            @Override
            public void update() {}
            @Override
            public void setDefaultValues() {}
            @Override
            public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
        };
        Entity e2 = new Entity() {
            @Override
            public void spawn(org.example.MapSpace mapSpace) {}
            @Override
            public void interact(entity.Entity other) {}
            @Override
            public void draw(java.awt.Graphics2D g2d) {}
            @Override
            public void update() {}
            @Override
            public void setDefaultValues() {}
            @Override
            public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
        };
        Vision v = new DummyVision(3, new Entity[]{e1, e2});
        assertArrayEquals(new Entity[]{e1, e2}, v.inVision());
    }

    @Test
    void testMarkVisibleSetsFlag() {
        DummyVision v = new DummyVision(1, new Entity[0]);
        assertFalse(v.isMarked());
        v.markVisible();
        assertTrue(v.isMarked());
    }

    @Test
    void testImplementsVisionInterface() {
        Vision v = new DummyVision(2, new Entity[0]);
        assertTrue(v instanceof Vision);
    }
}
