package entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusTest {
    @Test
    void testEnumValues() {
        Status[] values = Status.values();
        assertEquals(6, values.length);
        assertArrayEquals(new Status[] {
            Status.SURVIVE,
            Status.HUNT,
            Status.THIRST,
            Status.SLEEP,
            Status.REPRODUCE,
            Status.HEARD
        }, values);
    }

    @Test
    void testValueOf() {
        assertEquals(Status.SURVIVE, Status.valueOf("SURVIVE"));
        assertEquals(Status.HUNT, Status.valueOf("HUNT"));
        assertEquals(Status.THIRST, Status.valueOf("THIRST"));
        assertEquals(Status.SLEEP, Status.valueOf("SLEEP"));
        assertEquals(Status.REPRODUCE, Status.valueOf("REPRODUCE"));
        assertEquals(Status.HEARD, Status.valueOf("HEARD"));
    }
}
