package Gui;

import entity.util.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(false);
    }

    @Test
    void testInitialItemCountsAreZero() {
        for (Item item : Item.values()) {
            assertEquals(0, inventory.getItemCount(item), "Initial count should be zero for " + item);
        }
    }

    @Test
    void testAddItemIncreasesCount() {
        inventory.addItem(Item.LION);
        assertEquals(1, inventory.getItemCount(Item.LION));
        inventory.addItem(Item.LION);
        assertEquals(2, inventory.getItemCount(Item.LION));
    }

    @Test
    void testRemoveItemDecreasesCountAndClearsSlot() {
        inventory.addItem(Item.LION);
        inventory.addItem(Item.LION);
        inventory.removeItem(Item.LION);
        assertEquals(1, inventory.getItemCount(Item.LION));
        inventory.removeItem(Item.LION);
        assertEquals(0, inventory.getItemCount(Item.LION));
        assertNull(inventory.getItemAtSlot(Item.LION.ordinal()));
    }

    @Test
    void testSlotAssignment() {
        inventory.addItem(Item.LION);
        assertEquals(Item.LION, inventory.getItemAtSlot(Item.LION.ordinal()));
    }

    @Test
    void testItemSelection() {
        assertFalse(inventory.hasSelectedItem());
        inventory.toggleItemSelection(Item.LION);
        assertTrue(inventory.isItemSelected(Item.LION));
        assertTrue(inventory.hasSelectedItem());
        inventory.toggleItemSelection(Item.LION);
        assertFalse(inventory.isItemSelected(Item.LION));
        assertFalse(inventory.hasSelectedItem());
    }

    @Test
    void testSyncSoundState() {
        inventory.syncSoundState(true);
        // No getter, but should not throw
    }
}
