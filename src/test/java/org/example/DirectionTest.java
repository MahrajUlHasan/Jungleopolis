package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void testEnumValues() {
        Direction[] values = Direction.values();
        assertEquals(4, values.length);
        assertArrayEquals(new Direction[] {
            Direction.UP,
            Direction.RIGHT,
            Direction.DOWN,
            Direction.LEFT
        }, values);
    }

    @Test
    void testValueOf() {
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
    }
}
