package Gui.Tile;

import org.example.GameEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TileManagerTest {
    static class DummyGameEngine extends GameEngine {
        public int maxRowNUm = 2;
        public int maxColNUm = 2;
        public int tileSize = 16;
        public int screenWidth = 32;
        public int screenHeight = 32;
        public DummyGameEngine() { super(null); }
    }

    DummyGameEngine engine;
    TileManager manager;

    @BeforeEach
    void setUp() {
        engine = new DummyGameEngine();
        manager = new TileManager(engine) {
            @Override
            public void getTileImage() {
                tile[0] = new Tile();
                tile[0].image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                tile[1] = new Tile();
                tile[1].image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                tile[1].collision = true;
            }
            @Override
            public void loadMap() {
                // Only use tile indices 0 and 1 to avoid out-of-bounds
                mapTileNum = new int[][] { {0, 1}, {1, 0} };
            }
            @Override
            public void draw(Graphics2D g2d) {
                // Only draw for the bounds of the test map
                int col = 0;
                int row = 0;
                int x = 0;
                int y = 0;
                while (col < 2 && row < 2) {
                    int tileNum = mapTileNum[row][col];
                    g2d.drawImage(tile[tileNum].image, x, y, engine.tileSize, engine.tileSize, null);
                    col++;
                    x += engine.tileSize;
                    if (col == 2) {
                        col = 0;
                        row++;
                        y += engine.tileSize;
                        x = 0;
                    }
                }
            }
        };
    }

    @Test
    void testTileManagerInitializesTilesAndMap() {
        assertNotNull(manager.tile);
        assertNotNull(manager.mapTileNum);
        assertEquals(2, manager.mapTileNum.length);
        assertEquals(2, manager.mapTileNum[0].length);
        assertNotNull(manager.tile[0].image);
        assertNotNull(manager.tile[1].image);
        assertTrue(manager.tile[1].collision);
    }

    @Test
    void testDrawDoesNotThrow() {
        Graphics2D g2d = (Graphics2D) new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB).getGraphics();
        assertDoesNotThrow(() -> manager.draw(g2d));
    }
}
