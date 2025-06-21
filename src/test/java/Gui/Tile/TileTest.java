package Gui.Tile;

import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void testDefaultValues() {
        Tile tile = new Tile();
        assertNull(tile.image, "Default image should be null");
        assertFalse(tile.collision, "Default collision should be false");
    }

    @Test
    void testSetImageAndCollision() {
        Tile tile = new Tile();
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        tile.image = img;
        tile.collision = true;
        assertSame(img, tile.image, "Image should be set correctly");
        assertTrue(tile.collision, "Collision should be set to true");
    }
}
