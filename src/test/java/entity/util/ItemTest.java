package entity.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    @Test
    void testEnumValues() {
        // Check all expected enum values exist
        assertNotNull(Item.valueOf("LION"));
        assertNotNull(Item.valueOf("WOLF"));
        assertNotNull(Item.valueOf("BUFFALO"));
        assertNotNull(Item.valueOf("GIRAFFE"));
        assertNotNull(Item.valueOf("ROAD"));
        assertNotNull(Item.valueOf("POND"));
        assertNotNull(Item.valueOf("TREE"));
        assertNotNull(Item.valueOf("GRASS"));
        assertNotNull(Item.valueOf("CHARGING_STATION"));
        assertNotNull(Item.valueOf("BUSH"));
        assertNotNull(Item.valueOf("WATER"));
        assertNotNull(Item.valueOf("JEEP"));
        assertNotNull(Item.valueOf("RANGER"));
        assertNotNull(Item.valueOf("DRONE"));
        assertNotNull(Item.valueOf("AIR_SHIP"));
        assertNotNull(Item.valueOf("AIRSHIP_PATROL_FLAG"));
        assertNotNull(Item.valueOf("DRONE_PATROL_FLAG"));
        assertNotNull(Item.valueOf("CAMERA"));
        assertNotNull(Item.valueOf("POACHER"));
    }

    @Test
    void testEnumOrderAndToString() {
        Item[] values = Item.values();
        assertEquals("LION", values[0].toString());
        assertEquals("WOLF", values[1].toString());
        assertEquals("BUFFALO", values[2].toString());
        assertEquals("GIRAFFE", values[3].toString());
        assertEquals("ROAD", values[4].toString());
        assertEquals("POND", values[5].toString());
        assertEquals("TREE", values[6].toString());
        assertEquals("GRASS", values[7].toString());
        assertEquals("CHARGING_STATION", values[8].toString());
        assertEquals("BUSH", values[9].toString());
        assertEquals("WATER", values[10].toString());
        assertEquals("JEEP", values[11].toString());
        assertEquals("RANGER", values[12].toString());
        assertEquals("DRONE", values[13].toString());
        assertEquals("AIR_SHIP", values[14].toString());
        assertEquals("AIRSHIP_PATROL_FLAG", values[15].toString());
        assertEquals("DRONE_PATROL_FLAG", values[16].toString());
        assertEquals("CAMERA", values[17].toString());
        assertEquals("POACHER", values[18].toString());
    }
}
