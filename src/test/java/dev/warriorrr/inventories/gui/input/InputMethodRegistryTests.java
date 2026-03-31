package dev.warriorrr.inventories.gui.input;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputMethodRegistryTests {
    @Test
    void testMethodFor_returnsCorrectMethod_forGivenTextLength() {
        final UserInputMethod<?> longInput = mock(UserInputMethod.class);
        when(longInput.maximumTextLength()).thenReturn(TextLength.ofAtLeast(100));
        when(longInput.toString()).thenReturn("long input");

        final UserInputMethod<?> shortInput = mock(UserInputMethod.class);
        when(shortInput.maximumTextLength()).thenReturn(TextLength.ofAtLeast(20));
        when(shortInput.toString()).thenReturn("short input");

        final InputMethodRegistry registry = new InputMethodRegistry(Map.of(
                Key.key("test", "short"), shortInput,
                Key.key("test", "long"), longInput
        ));

        assertEquals(shortInput, registry.methodFor(TextLength.ofAtLeast(10)));
        assertEquals(shortInput, registry.methodFor(TextLength.ofAtLeast(20)));

        assertEquals(longInput, registry.methodFor(TextLength.ofAtLeast(40)));
    }
}
