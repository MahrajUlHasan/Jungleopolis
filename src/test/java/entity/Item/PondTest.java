package entity.Item;

import entity.Animal;
import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PondTest {
    private GameEngine engine;
    private MapSpace map;
    private Pond pond;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        map = new MapSpace(engine);
        when(engine.getMap()).thenReturn(map);
        map.ponds = new ArrayList<>();
        pond = new Pond(engine);
    }

    @Test
    void testSpawnAddsToPondsList() {
        pond.spawn(map);
        assertTrue(map.ponds.contains(pond));
    }

    @Test
    void testInteractWithAnimalCallsDrinkWater() {
        Animal animal = mock(Animal.class);
        pond.interact(animal);
        verify(animal, atLeastOnce()).drinkWater();
    }

    @Test
    void testInteractWithNonAnimalThrowsClassCast() {
        Entity entity = mock(Entity.class);
        assertThrows(ClassCastException.class, () -> pond.interact(entity));
    }

    @Test
    void testUpdateStatsDoesNotThrow() {
        assertDoesNotThrow(() -> pond.updateStats());
    }
}
