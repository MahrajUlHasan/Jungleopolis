package entity;

import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Gui.GUI;
import static org.junit.jupiter.api.Assertions.*;

class BuildingTest {
    private GameEngine engine;
    private MapSpace mapSpace;

    static class TestBuilding extends Building {
        public TestBuilding(GameEngine engine) {
            super(engine);
        }
        @Override public void draw(java.awt.Graphics2D g2d) {}
        @Override public void update() {}
        @Override public void setDefaultValues() {}
        @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
    }

    static class DistanceBuilding extends TestBuilding {
        private final int closeDist, farDist;
        private final DynamicEntity close, far;
        public DistanceBuilding(GameEngine engine, DynamicEntity close, int closeDist, DynamicEntity far, int farDist) {
            super(engine);
            this.close = close;
            this.closeDist = closeDist;
            this.far = far;
            this.farDist = farDist;
        }
        @Override
        public int distance(Entity ent) {
            if (ent == close) return closeDist;
            if (ent == far) return farDist;
            return 10000;
        }
    }

    static class DummyDynamicEntity extends DynamicEntity {
        public DummyDynamicEntity(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void draw(java.awt.Graphics2D g2d) {}
        @Override public void update() {}
        @Override public void setDefaultValues() {}
        @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
        @Override public void interact(Entity other) {}
        @Override public void spawn(org.example.MapSpace mapSpace) {}
        @Override protected void updateStats() {}
        @Override public void setTarget() {}
    }

    @BeforeEach
    void setUp() {
        engine = new GameEngine((GUI)null); // Use real GameEngine with null GUI
        mapSpace = new MapSpace(engine);
    }

    @Test
    void testAddOccupantAndIsOccupied() {
        TestBuilding building = new TestBuilding(engine);
        DummyDynamicEntity occupant = new DummyDynamicEntity(engine, mapSpace);
        building.addOccupant(occupant);
        assertTrue(building.occupants.contains(occupant));
        // Add up to max_occupants
        for (int i = 1; i < building.max_occupants; i++) {
            DummyDynamicEntity occ = new DummyDynamicEntity(engine, mapSpace);
            building.addOccupant(occ);
        }
        assertTrue(building.IsOccupied());
    }

    @Test
    void testInteractAddsOccupant() {
        TestBuilding building = new TestBuilding(engine);
        DummyDynamicEntity occupant = new DummyDynamicEntity(engine, mapSpace);
        building.interact(occupant);
        assertTrue(building.occupants.contains(occupant));
    }

    @Test
    void testUpdateOccupantsRemovesDistant() {
        DummyDynamicEntity close = new DummyDynamicEntity(engine, mapSpace);
        DummyDynamicEntity far = new DummyDynamicEntity(engine, mapSpace);
        DistanceBuilding building = new DistanceBuilding(engine, close, engine.tileSize*2, far, engine.tileSize*4);
        building.occupants.add(close);
        building.occupants.add(far);
        building.updateOccupants();
        assertTrue(building.occupants.contains(close));
        assertFalse(building.occupants.contains(far));
    }

    @Test
    void testSpawnAddsToMapSpace() {
        TestBuilding building = new TestBuilding(engine);
        building.spawn(mapSpace);
        assertTrue(mapSpace.entitiesToAdd.contains(building));
        assertTrue(mapSpace.buildings.contains(building));
    }

    @Test
    void testSetXAndSetYSnapToGrid() {
        TestBuilding building = new TestBuilding(engine);
        int x = 45, y = 77;
        building.setX(x);
        building.setY(y);
        assertEquals(engine.tileSize, building.x); // 45/32 = 1, 1*32 = 32 (tileSize=32)
        assertEquals(engine.tileSize*2, building.y); // 77/32 = 2, 2*32 = 64
    }
}
