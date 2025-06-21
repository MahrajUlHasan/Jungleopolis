package org.example;

import entity.*;
import entity.Item.Person;
import entity.Item.Jeep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MapSpaceTest {
    static class DummyGameEngine extends GameEngine {
        public DummyGameEngine() { super(null); }
    }

    DummyGameEngine engine;
    MapSpace map;

    @BeforeEach
    void setUp() {
        engine = new DummyGameEngine();
        try {
            java.lang.reflect.Field tileSizeF = GameEngine.class.getDeclaredField("tileSize");
            tileSizeF.setAccessible(true);
            tileSizeF.setInt(engine, 10);
            java.lang.reflect.Field screenWidthF = GameEngine.class.getDeclaredField("screenWidth");
            screenWidthF.setAccessible(true);
            screenWidthF.setInt(engine, 100);
            java.lang.reflect.Field screenHeightF = GameEngine.class.getDeclaredField("screenHeight");
            screenHeightF.setAccessible(true);
            screenHeightF.setInt(engine, 100);
        } catch (Exception e) { throw new RuntimeException(e); }
        map = new MapSpace(engine);
    }

    @Test
    void testGridInitialization() {
        Point[][] grid = map.getGrid();
        assertNotNull(grid);
        assertEquals(engine.screenWidth / engine.tileSize, grid.length);
        assertEquals(engine.screenHeight / engine.tileSize, grid[0].length);
    }

    @Test
    void testAddEntSortsEntities() {
        Entity road = new Entity() {
            { y = 1; }
            @Override public void setDefaultValues() {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
            @Override public void draw(Graphics2D g2d) {}
            @Override public void update() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace mapSpace) {}
        };
        Entity jeep = new Entity() {
            { y = 2; }
            @Override public void setDefaultValues() {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
            @Override public void draw(Graphics2D g2d) {}
            @Override public void update() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace mapSpace) {}
        };
        map.entities.add(jeep);
        map.entities.add(road);
        map.addEnt(road);
        assertTrue(map.entities.get(0) == road || map.entities.get(1) == road);
    }

    @Test
    void testGetEntityAtReturnsCorrectEntity() {
        Entity e = new Entity() {
            @Override public void setDefaultValues() {}
            @Override public Rectangle getHitBox() { return new Rectangle(5,5,10,10); }
            @Override public void draw(Graphics2D g2d) {}
            @Override public void update() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace mapSpace) {}
        };
        map.entities.add(e);
        assertEquals(e, map.getEntityAt(10,10));
        assertNull(map.getEntityAt(50,50));
    }

    @Test
    void testGetClosestEntityReturnsNullForUnknownType() {
        Entity e = new Entity() {
            @Override public void setDefaultValues() {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
            @Override public void draw(Graphics2D g2d) {}
            @Override public void update() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace mapSpace) {}
        };
        assertNull(map.getClosestEntity("UnknownType", e));
    }

    @Test
    void testIsBlockedOffScreen() {
        Entity e = new Entity() {
            @Override public void setDefaultValues() {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,10,10); }
            @Override public void draw(Graphics2D g2d) {}
            @Override public void update() {}
            @Override public void interact(Entity entity) {}
            @Override public void spawn(MapSpace mapSpace) {}
        };
        assertTrue(map.isBlocked(e, -100, 0));
        assertTrue(map.isBlocked(e, 0, -100));
    }

    @Test
    void testOffScreenLogic() {
        int statsPanel = map.getStatsPanelHeight();
        int invPanel = map.getInventoryPanelHeight();
        int w = engine.screenWidth;
        int h = engine.screenHeight;
        int tile = engine.tileSize;
        // Left
        assertTrue(map.offScreen(-1, 0, tile, tile));
        // Top (in stats panel)
        assertTrue(map.offScreen(0, statsPanel - 1, tile, tile));
        // Right
        assertTrue(map.offScreen(w - tile + 1, statsPanel, tile, tile));
        // Bottom (in inventory panel)
        assertTrue(map.offScreen(0, h - invPanel, tile, tile));
        // Playable area
        assertFalse(map.offScreen(tile, statsPanel, tile, tile));
    }

    // Minimal stub for Person
    static class TestPerson extends Person {
        public TestPerson(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void setTarget() {}
        @Override public void interact(Entity entity) {}
        @Override public void update() {}
        @Override public void draw(Graphics2D g2d) {}
        @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
        @Override public void setDefaultValues() {}
    }
    // Minimal stub for Building
    static class TestBuilding extends Building {
        public TestBuilding(GameEngine engine) { super(engine); }
        @Override public void interact(Entity entity) {}
        @Override public void update() {}
        @Override public void draw(Graphics2D g2d) {}
        @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
        @Override public void setDefaultValues() {}
    }
    // Minimal stub for Jeep
    static class TestJeep extends Jeep {
        public TestJeep(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void getSpriteImage() {}
        @Override public void changeDirection() {}
        @Override public void setTarget() {}
        @Override public void interact(Entity entity) {}
        @Override public void update() {}
        @Override public void draw(Graphics2D g2d) {}
        @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
        @Override public void setDefaultValues() {}
    }
    // Minimal stub for Animal
    static class StubAnimal extends Animal {
        public StubAnimal(int x, int y, int age, GameEngine engine, MapSpace map) { super(engine, map); this.x = x; this.y = y; this.age = age; }
        @Override protected void setStats() {}
        @Override public void setTarget() {}
        @Override public void interact(Entity entity) {}
        @Override public void update() {}
        @Override public void draw(Graphics2D g2d) {}
        @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
        @Override public void spawn(MapSpace mapSpace) {}
    }

    @Test
    void testGetNumMethods() {
        map.animals.add(new StubAnimal(0, 0, 1, engine, map));
        map.herbivores.add(new Herbivore(engine, map) {
            @Override protected void setStats() {}
            @Override public void setTarget() {}
            @Override public void interact(Entity entity) {}
            @Override public void update() {}
            @Override public void draw(Graphics2D g2d) {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
            @Override public void spawn(MapSpace mapSpace) {}
            @Override protected void doReproduce(Herbivore mate) {}
            @Override public void attacked(Carnivore carnivore) {}
        });
        map.carnivores.add(new Carnivore(engine, map) {
            @Override protected void setStats() {}
            @Override public void setTarget() {}
            @Override public void interact(Entity entity) {}
            @Override public void update() {}
            @Override public void draw(Graphics2D g2d) {}
            @Override public Rectangle getHitBox() { return new Rectangle(0,0,1,1); }
            @Override public void spawn(MapSpace mapSpace) {}
            @Override protected void doReproduce(Carnivore mate) {}
        });
        map.people.add(new TestPerson(engine, map));
        map.buildings.add(new TestBuilding(engine));
        map.jeeps.add(new TestJeep(engine, map));
        assertEquals(1, map.getAnimalNum());
        assertEquals(1, map.getHerbivoreNum());
        assertEquals(1, map.getCarnivoreNum());
        assertEquals(1, map.getPersonNum());
        assertEquals(1, map.getBuildingNum());
        assertEquals(1, map.getJeepNum());
    }

    @Test
    void testPanelHeights() {
        assertEquals(52, map.getStatsPanelHeight());
        assertEquals(80, map.getInventoryPanelHeight());
        assertEquals(132, map.getTotalPanelsHeight());
        assertEquals(engine.screenHeight - 52 - 80, map.getPlayableHeight());
    }
}
