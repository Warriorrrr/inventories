package dev.warriorrr.inventories.gui.input;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputBackendRegistryTests {
    @Test
    void testBackendFor_returnsCorrectBackend_forGivenTextLength() {
        final UserInputBackend longInput = mock(UserInputBackend.class);
        when(longInput.maximumTextLength()).thenReturn(TextLength.ofAtLeast(100));
        when(longInput.toString()).thenReturn("long input");

        final UserInputBackend shortInput = mock(UserInputBackend.class);
        when(shortInput.maximumTextLength()).thenReturn(TextLength.ofAtLeast(20));
        when(shortInput.toString()).thenReturn("short input");

        final InputBackendRegistry registry = new InputBackendRegistry(Map.of(
                Key.key("test", "short"), shortInput,
                Key.key("test", "long"), longInput
        ));

        assertEquals(shortInput, registry.backendFor(TextLength.ofAtLeast(10)));
        assertEquals(shortInput, registry.backendFor(TextLength.ofAtLeast(20)));

        assertEquals(longInput, registry.backendFor(TextLength.ofAtLeast(40)));
    }
}
