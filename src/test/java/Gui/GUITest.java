package Gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GUITest {
    @Test
    void testSoundMuteToggle() {
        // Initial state should be unmuted
        GUI.setSoundMuted(false);
        assertFalse(GUI.isSoundMuted(), "Sound should be unmuted by default");
        // Mute
        GUI.setSoundMuted(true);
        assertTrue(GUI.isSoundMuted(), "Sound should be muted after setting");
        // Unmute again
        GUI.setSoundMuted(false);
        assertFalse(GUI.isSoundMuted(), "Sound should be unmuted after setting");
    }

    @Test
    void testSoundMuteStaticState() {
        // Set to muted
        GUI.setSoundMuted(true);
        assertTrue(GUI.isSoundMuted());
        // Set to unmuted
        GUI.setSoundMuted(false);
        assertFalse(GUI.isSoundMuted());
    }
}
