package dev.warriorrr.inventories.gui.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextLengthTests {
    @Test
    void testConstructor_throwsException_whenGivenNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> TextLength.ofAtLeast(-1));
        assertThrows(IllegalArgumentException.class, () -> TextLength.ofAtLeast(0));
    }

    @Test
    void testConstructor_works_whenGivenPositive() {
        assertEquals(1, TextLength.ofAtLeast(1).characters());
    }
}
