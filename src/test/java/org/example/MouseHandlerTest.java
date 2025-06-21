package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseHandlerTest {
    private GameEngine engine;
    private MouseHandler handler;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        handler = new MouseHandler(engine);
    }

    @Test
    void testMouseClickedLeftButton() {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(event.getX()).thenReturn(42);
        when(event.getY()).thenReturn(24);
        handler.mouseClicked(event);
        assertTrue(handler.mClicked);
        assertEquals(42, handler.lastX);
        assertEquals(24, handler.lastY);
    }

    @Test
    void testMousePressedLeftButton() {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(event.getX()).thenReturn(10);
        when(event.getY()).thenReturn(20);
        handler.mousePressed(event);
        assertTrue(handler.mPressed);
        assertFalse(handler.mReleased);
        assertEquals(10, handler.lastX);
        assertEquals(20, handler.lastY);
    }

    @Test
    void testMouseReleasedLeftButton() {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(event.getX()).thenReturn(5);
        when(event.getY()).thenReturn(15);
        handler.mouseReleased(event);
        assertTrue(handler.mReleased);
        assertFalse(handler.mPressed);
        assertEquals(5, handler.lastX);
        assertEquals(15, handler.lastY);
    }

    @Test
    void testMouseDraggedWhenPressed() {
        handler.mPressed = true;
        MouseEvent event = mock(MouseEvent.class);
        when(event.getX()).thenReturn(7);
        when(event.getY()).thenReturn(8);
        handler.mouseDragged(event);
        assertTrue(handler.mDragging);
        assertEquals(7, handler.lastX);
        assertEquals(8, handler.lastY);
    }

    @Test
    void testMouseDraggedWhenNotPressed() {
        handler.mPressed = false;
        MouseEvent event = mock(MouseEvent.class);
        handler.mouseDragged(event);
        assertFalse(handler.mDragging);
    }
}
