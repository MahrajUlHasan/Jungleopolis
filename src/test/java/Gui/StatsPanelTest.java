package Gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class StatsPanelTest {
    static class DummyGameEngine extends org.example.GameEngine {
        public int cash = 12345;
        public int difficulty = 2;
        public boolean paused = false;
        public int visitors = 7;
        public int herbivores = 3;
        public int carnivores = 2;
        public String speedLevel = "Minute";
        public DummyGameEngine() { super(null); }
        @Override public int getCash() { return cash; }
        @Override public String getTimePassed() { return "12:34:56"; }
        @Override public String getSpeedLevel() { return speedLevel; }
        @Override public boolean isPaused() { return paused; }
        @Override public void pauseGame() { paused = true; }
        @Override public void resumeGame() { paused = false; }
        @Override public void cycleSpeed() { speedLevel = "Hour"; }
        @Override public int getVisitors() { return visitors; }
        @Override public int getHerbivores() { return herbivores; }
        @Override public int getCarnivores() { return carnivores; }
    }

    DummyGameEngine engine;
    StatsPanel panel;

    @BeforeEach
    void setUp() {
        engine = new DummyGameEngine();
        panel = new StatsPanel(engine);
    }

    @Test
    void testUpdateStatsReflectsGameEngine() {
        panel.updateStats();
        // Use reflection to access private labels
        JLabel cashLabel = (JLabel) getPrivateField(panel, "cashLabel");
        JLabel timeLabel = (JLabel) getPrivateField(panel, "timeLabel");
        JLabel difficultyLabel = (JLabel) getPrivateField(panel, "difficultyLabel");
        JLabel visitorsLabel = (JLabel) getPrivateField(panel, "visitorsLabel");
        JLabel herbivoresLabel = (JLabel) getPrivateField(panel, "herbivoresLabel");
        JLabel carnivoresLabel = (JLabel) getPrivateField(panel, "carnivoresLabel");
        JButton speedButton = (JButton) getPrivateField(panel, "speedButton");
        JButton pauseButton = (JButton) getPrivateField(panel, "pauseButton");
        System.out.println("cashLabel: " + cashLabel.getText());
        System.out.println("timeLabel: " + timeLabel.getText());
        System.out.println("difficultyLabel: " + difficultyLabel.getText());
        System.out.println("visitorsLabel: " + visitorsLabel.getText());
        System.out.println("herbivoresLabel: " + herbivoresLabel.getText());
        System.out.println("carnivoresLabel: " + carnivoresLabel.getText());
        System.out.println("speedButton: " + speedButton.getText());
        System.out.println("pauseButton: " + pauseButton.getText());
        assertTrue(cashLabel.getText().contains("12,345"));
        assertTrue(timeLabel.getText().contains("12:34:56"));
        assertTrue(difficultyLabel.getText().contains("Easy"));
        assertTrue(visitorsLabel.getText().contains("7"));
        assertTrue(herbivoresLabel.getText().contains("3"));
        assertTrue(carnivoresLabel.getText().contains("2"));
        assertEquals(engine.getSpeedLevel(), speedButton.getText());
        assertEquals("Pause", pauseButton.getText());
    }

    @Test
    void testPauseButtonTogglesPause() {
        JButton pauseButton = (JButton) getPrivateField(panel, "pauseButton");
        assertEquals("Pause", pauseButton.getText());
        pauseButton.doClick();
        assertTrue(engine.isPaused());
        panel.updateStats();
        assertEquals("Resume", pauseButton.getText());
        pauseButton.doClick();
        assertFalse(engine.isPaused());
        panel.updateStats();
        assertEquals("Pause", pauseButton.getText());
    }

    @Test
    void testSpeedButtonCyclesSpeed() {
        JButton speedButton = (JButton) getPrivateField(panel, "speedButton");
        panel.updateStats();
        assertEquals("Minute", speedButton.getText());
        speedButton.doClick();
        assertEquals("Hour", engine.getSpeedLevel());
    }

    @Test
    void testPaintComponentDoesNotThrow() {
        assertDoesNotThrow(() -> {
            panel.setSize(200, 60);
            panel.paintComponent(new BufferedImage(200, 60, BufferedImage.TYPE_INT_ARGB).createGraphics());
        });
    }

    // Helper to access private fields via reflection
    private Object getPrivateField(Object obj, String name) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
