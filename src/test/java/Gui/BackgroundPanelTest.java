package Gui;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class BackgroundPanelTest {
    @Test
    void testSetImageAndStyle() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        BackgroundPanel panel = new BackgroundPanel(img);
        assertEquals(img, getPrivateImage(panel));
        panel.setStyle(BackgroundPanel.TILED);
        // No exception should be thrown
    }

    @Test
    void testSetPaint() {
        BackgroundPanel panel = new BackgroundPanel((Image)null);
        Paint paint = new GradientPaint(0, 0, Color.RED, 10, 10, Color.BLUE);
        panel.setPaint(paint);
        // No exception should be thrown
    }

    @Test
    void testSetImageAlignment() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        BackgroundPanel panel = new BackgroundPanel(img);
        panel.setImageAlignmentX(0.7f);
        panel.setImageAlignmentY(0.3f);
        // No exception should be thrown
    }

    @Test
    void testPreferredSizeWithImage() {
        BufferedImage img = new BufferedImage(15, 25, BufferedImage.TYPE_INT_ARGB);
        BackgroundPanel panel = new BackgroundPanel(img);
        Dimension d = panel.getPreferredSize();
        assertEquals(15, d.width);
        assertEquals(25, d.height);
    }

    @Test
    void testPreferredSizeWithoutImage() {
        BackgroundPanel panel = new BackgroundPanel((Image)null);
        assertNotNull(panel.getPreferredSize());
    }

    @Test
    void testAddComponentTransparency() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        BackgroundPanel panel = new BackgroundPanel(img);
        JButton button = new JButton();
        panel.add(button);
        assertFalse(button.isOpaque());
    }

    @Test
    void testSetTransparentAdd() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        BackgroundPanel panel = new BackgroundPanel(img);
        panel.setTransparentAdd(false);
        JButton button = new JButton();
        panel.add(button);
        // Should remain opaque
        assertTrue(button.isOpaque());
    }

    @Test
    void testDoNothing() {
        BackgroundPanel panel = new BackgroundPanel((Image)null);
        panel.doNothing();
        // No exception should be thrown
    }

    // Helper to access private image field via reflection
    private Image getPrivateImage(BackgroundPanel panel) {
        try {
            java.lang.reflect.Field f = BackgroundPanel.class.getDeclaredField("image");
            f.setAccessible(true);
            return (Image) f.get(panel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
