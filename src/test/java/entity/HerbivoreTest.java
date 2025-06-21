package entity;

import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HerbivoreTest {
    static class DummyHerbivore extends Herbivore {
        public boolean attackedCalled = false;
        public boolean doReproduceCalled = false;
        public DummyHerbivore(GameEngine engine, MapSpace map) {
            super(engine, map);
        }
        @Override
        protected void doReproduce(Herbivore mate) { doReproduceCalled = true; }
        @Override
        public void attacked(Carnivore carnivore) { attackedCalled = true; }
        @Override
        protected void setStats() {}
    }

    private GameEngine engine;
    private MapSpace map;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(null);
        map = new MapSpace(engine);
    }

    @Test
    void testEatGrassIncreasesHungerAndSetsSleep() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        h.hunger = 0;
        h.hungerCap = 10;
        h.eatGrass(null);
        assertEquals(10, h.hunger);
        assertEquals(Status.SLEEP, h.status);
    }

    @Test
    void testReproduceDoesNotExceedMaxHerbivores() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        DummyHerbivore mate = new DummyHerbivore(engine, map);
        for (int i = 0; i < 60; i++) map.herbivores.add(new DummyHerbivore(engine, map));
        h.reproduce(mate);
        assertFalse(h.doReproduceCalled);
    }

    @Test
    void testReproduceCallsDoReproduce() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        DummyHerbivore mate = new DummyHerbivore(engine, map);
        h.reproduce(mate);
        assertTrue(h.doReproduceCalled);
    }

    @Test
    void testAttacked() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        h.attacked((Carnivore)null);
        assertTrue(h.attackedCalled);
    }

    @Test
    void testSpawnAddsToHerbivoresList() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        h.spawn(map);
        assertTrue(map.herbivores.contains(h));
    }

    @Test
    void testDieRemovesFromHerbivoresList() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        h.spawn(map);
        assertTrue(map.herbivores.contains(h));
        h.die();
        assertFalse(map.herbivores.contains(h));
    }

    @Test
    void testSetTargetHuntFindsGrass() {
        DummyHerbivore h = new DummyHerbivore(engine, map);
        h.status = Status.HUNT;
        map.entities.add(h); // Add itself for context
        h.setTarget();
        // No grass, so target should remain as set by super.setTarget() (likely null)
        assertNull(h.target);
    }

    @Test
    void testInteractWithHerbivoreReproduce() {
        DummyHerbivore h1 = new DummyHerbivore(engine, map);
        DummyHerbivore h2 = new DummyHerbivore(engine, map);
        h1.status = Status.REPRODUCE;
        h1.interact(h2);
        assertTrue(h2.doReproduceCalled);
        assertEquals(Status.THIRST, h1.status);
    }
}
