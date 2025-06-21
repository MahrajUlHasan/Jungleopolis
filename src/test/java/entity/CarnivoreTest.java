package entity;

import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Gui.GUI;
import static org.junit.jupiter.api.Assertions.*;

class CarnivoreTest {
    private GameEngine engine;
    private MapSpace mapSpace;

    static class DummyCarnivore extends Carnivore {
        boolean doReproduceCalled = false;
        public DummyCarnivore(GameEngine engine, MapSpace map) {
            super(engine, map);
            this.attackPower = 42;
        }
        @Override
        protected void doReproduce(Carnivore mate) {
            doReproduceCalled = true;
        }
        @Override public void draw(java.awt.Graphics2D g2d) {}
        @Override public void update() {}
        @Override public void setDefaultValues() {}
        @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
        @Override protected void setStats() {}
    }

    static class DummyHerbivore extends Herbivore {
        public boolean wasAttacked = false;
        public DummyHerbivore(GameEngine engine, MapSpace map) { super(engine, map); }
        @Override public void attacked(Carnivore attacker) { 
            System.out.println("attacked called, before: " + wasAttacked + ", hash=" + System.identityHashCode(this));
            wasAttacked = true; 
            System.out.println("attacked called, after: " + wasAttacked + ", hash=" + System.identityHashCode(this));
        }
        @Override public void draw(java.awt.Graphics2D g2d) {}
        @Override public void update() {}
        @Override public void setDefaultValues() {}
        @Override public java.awt.Rectangle getHitBox() { return new java.awt.Rectangle(); }
        @Override protected void doReproduce(Herbivore mate) {}
        @Override protected void setStats() {}
        @Override public Boolean isAlive() { return true; }
    }

    @BeforeEach
    void setUp() {
        engine = new GameEngine((GUI)null);
        mapSpace = new MapSpace(engine);
    }

    @Test
    void testGetAttackPower() {
        DummyCarnivore c = new DummyCarnivore(engine, mapSpace);
        assertEquals(42, c.getAttackPower());
    }

    @Test
    void testAttackHerbivoreSetsSleepIfFull() {
        DummyCarnivore c = new DummyCarnivore(engine, mapSpace);
        DummyHerbivore h = new DummyHerbivore(engine, mapSpace);
        c.hunger = c.hungerCap - 5;
        c.attack(h);
        assertEquals(Status.SLEEP, c.status);
        assertEquals(c.hungerCap, c.hunger);
    }

    @Test
    void testInteractWithHerbivoreCallsAttack() {
        DummyCarnivore c = new DummyCarnivore(engine, mapSpace);
        DummyHerbivore h = new DummyHerbivore(engine, mapSpace);
        c.interact(h);
        assertTrue(h.wasAttacked);
    }

    @Test
    void testInteractWithCarnivoreReproduce() {
        DummyCarnivore c1 = new DummyCarnivore(engine, mapSpace);
        DummyCarnivore c2 = new DummyCarnivore(engine, mapSpace);
        c1.status = Status.REPRODUCE;
        c1.interact(c2);
        assertTrue(c2.doReproduceCalled);
        assertEquals(Status.HEARD, c1.status);
    }

    @Test
    void testReproduceDoesNotExceedMaxCarnivores() {
        DummyCarnivore c1 = new DummyCarnivore(engine, mapSpace);
        DummyCarnivore c2 = new DummyCarnivore(engine, mapSpace);
        // Fill carnivores list to max
        for (int i = 0; i < 60; i++) mapSpace.carnivores.add(c1);
        c1.doReproduceCalled = false;
        c1.status = Status.REPRODUCE;
        c1.interact(c2);
        assertFalse(c2.doReproduceCalled);
    }

    @Test
    void testSpawnAddsToCarnivoresList() {
        DummyCarnivore c = new DummyCarnivore(engine, mapSpace);
        c.spawn(mapSpace);
        assertTrue(mapSpace.carnivores.contains(c));
    }

    @Test
    void testDieRemovesFromCarnivoresList() {
        DummyCarnivore c = new DummyCarnivore(engine, mapSpace);
        c.spawn(mapSpace);
        assertTrue(mapSpace.carnivores.contains(c));
        c.die();
        assertFalse(mapSpace.carnivores.contains(c));
    }
}
