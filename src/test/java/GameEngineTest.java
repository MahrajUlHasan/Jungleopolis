import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import Gui.GUI;
import org.example.GameEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameEngineTest {

    @Test
    public void testCycleSpeed() {
        // Create a mock GUI
        GUI mockGUI = Mockito.mock(GUI.class);

        // Creating an instance of GameEngine with a Mock GUI
        GameEngine engine = new GameEngine(mockGUI);

        // Assert initial speed level is "Hour"
        assertEquals("hour", engine.getSpeedLevel());

        // Cycle speed to "Day"
        engine.cycleSpeed();
        assertEquals("day", engine.getSpeedLevel());

        // Cycle speed to "Week"
        engine.cycleSpeed();
        assertEquals("week", engine.getSpeedLevel());

        // Cycle speed back to "Hour"
        engine.cycleSpeed();
        assertEquals("hour", engine.getSpeedLevel()); // Loops back
    }

    @Test
    public void getMultiplierReturnsOneForHourSpeed() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        assertEquals(3600, engine.getMultiplier());
    }

    @Test
    public void getMultiplierReturnsTwentyFourForDaySpeed() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        engine.cycleSpeed(); // Set to "Day"
        engine.cycleSpeed(); // Set to "Week"
        engine.cycleSpeed(); // Back to "Hour"
        engine.cycleSpeed(); // Set to "Day"
        assertEquals(86400, engine.getMultiplier());
    }

    @Test
    public void getMultiplierReturnsOneSixtyEightForWeekSpeed() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        engine.cycleSpeed(); // Set to "Day"
        engine.cycleSpeed(); // Set to "Week"
        assertEquals(604800, engine.getMultiplier());
    }

    @Test
    public void getMultiplierReturnsDefaultOneForInvalidSpeed() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        engine.timeSpeed = 0; // Invalid speed
        assertEquals(3600, engine.getMultiplier());
    }

    @Test
    public void testGetTileSize() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        assertEquals(engine.tileUnit * engine.scale, engine.getTileSize());
    }

    @Test
    public void testGetAndUpdateCash() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        int initialCash = engine.getCash();
        engine.updateCash(500);
        assertEquals(initialCash + 500, engine.getCash());
    }

    @Test
    public void testGetDifficulty() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        assertEquals(1, engine.getDifficulty());
    }

    @Test
    public void testGetInventoryNotNull() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        assertEquals(false, engine.getInventory() == null);
    }

    @Test
    public void testPauseAndResumeGame() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        engine.pauseGame();
        assertEquals(true, engine.isPaused());
        engine.resumeGame();
        assertEquals(false, engine.isPaused());
    }

    @Test
    public void testPutEntityInHandAndIsEntityAtHand() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        entity.Entity mockEntity = Mockito.mock(entity.Entity.class);
        engine.putEntityInHand(mockEntity);
        assertEquals(true, engine.isEntityAtHand());
    }

    @Test
    public void testGetKeyHandlerAndMouseHandlerNotNull() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        assertEquals(false, engine.getKeyHandler() == null);
        assertEquals(false, engine.getMouseHandler() == null);
    }

    @Test
    public void testGetTimePassedAndMonthsPassed() {
        GameEngine engine = new GameEngine(Mockito.mock(GUI.class));
        // Should return default values before game started
        assertEquals("00:00:00:00", engine.getTimePassed());
        assertEquals(0, engine.getMonthsPassed());
    }
}