package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.Clip;

import static org.junit.jupiter.api.Assertions.*;

class SoundTest {
    Sound sound;

    @BeforeEach
    void setUp() {
        sound = new Sound();
    }

    @Test
    void testConstructorInitializesSoundURL() {
        assertNotNull(sound);
        assertNotNull(sound.soundURL[0]);
        assertTrue(sound.soundURL[0].toString().endsWith("/Sound/BlueBoyAdventure.wav"));
    }

    @Test
    void testSetFileHandlesInvalidIndexGracefully() {
        // Should not throw even if index is not set
        assertDoesNotThrow(() -> sound.setFile(1));
    }

    @Test
    void testPlayStopLoopNoClipLoaded() {
        // Should not throw even if clip is null
        assertDoesNotThrow(() -> sound.play());
        assertDoesNotThrow(() -> sound.stop());
        assertDoesNotThrow(() -> sound.loop());
    }

    @Test
    void testSetFileAndPlayWithValidIndex() {
        // This will only work if the resource exists in test env, so just check no exception
        assertDoesNotThrow(() -> sound.setFile(0));
        // play/stop/loop should not throw even if clip is not actually loaded
        assertDoesNotThrow(() -> sound.play());
        assertDoesNotThrow(() -> sound.stop());
        assertDoesNotThrow(() -> sound.loop());
    }
}
