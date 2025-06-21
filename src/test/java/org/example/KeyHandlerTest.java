package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

class KeyHandlerTest {
    private KeyHandler handler;

    @BeforeEach
    void setUp() {
        handler = new KeyHandler();
    }

    private KeyEvent key(int keyCode) {
        return new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    @Test
    void testKeyPressedSetsFlags() {
        handler.keyPressed(key(KeyEvent.VK_UP));
        assertTrue(handler.upKey);
        handler.keyPressed(key(KeyEvent.VK_DOWN));
        assertTrue(handler.downKey);
        handler.keyPressed(key(KeyEvent.VK_LEFT));
        assertTrue(handler.leftKey);
        handler.keyPressed(key(KeyEvent.VK_RIGHT));
        assertTrue(handler.rightKey);
    }

    @Test
    void testKeyReleasedClearsFlags() {
        // Set all to true first
        handler.upKey = handler.downKey = handler.leftKey = handler.rightKey = true;
        handler.keyReleased(key(KeyEvent.VK_UP));
        assertFalse(handler.upKey);
        handler.keyReleased(key(KeyEvent.VK_DOWN));
        assertFalse(handler.downKey);
        handler.keyReleased(key(KeyEvent.VK_LEFT));
        assertFalse(handler.leftKey);
        handler.keyReleased(key(KeyEvent.VK_RIGHT));
        assertFalse(handler.rightKey);
    }

    @Test
    void testKeyTypedDoesNothing() {
        handler.upKey = true;
        handler.keyTyped(key(KeyEvent.VK_UP));
        // Should not change any state
        assertTrue(handler.upKey);
    }
}
