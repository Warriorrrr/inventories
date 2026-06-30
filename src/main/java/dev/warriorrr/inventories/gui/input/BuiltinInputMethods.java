package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.input.impl.dialog.DialogInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.impl.sign.SignInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputOptionsBuilder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class that holds the keys for builtin input methods.
 */
public class BuiltinInputMethods {
    private static final Collection<Key> KEYS;

    private BuiltinInputMethods() {}

    /**
     * Key for the builtin chat input method.
     */
    public static final InputMethodKey<ChatInputOptionsBuilder> CHAT = InputMethodKey.of(Key.key("inventories", "chat"));

    /**
     * Key for the builtin sign input method.
     */
    public static final InputMethodKey<SignInputOptionsBuilder> SIGN = InputMethodKey.of(Key.key("inventories", "sign"));

    /**
     * Key for the builtin dialog input method.
     */
    public static final InputMethodKey<DialogInputOptionsBuilder> DIALOG = InputMethodKey.of(Key.key("inventories", "dialog"));

    /**
     * {@return the keys for all builtin input methods}
     */
    public static @Unmodifiable Collection<Key> keys() {
        return KEYS;
    }

    static {
        KEYS = Stream.of(BuiltinInputMethods.class.getFields())
                .filter(field -> field.getType().equals(InputMethodKey.class) && Modifier.isStatic(field.getModifiers()))
                .map(field -> {
                    try {
                        return ((InputMethodKey<?>) field.get(null)).key();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toUnmodifiableSet());
    }
}
