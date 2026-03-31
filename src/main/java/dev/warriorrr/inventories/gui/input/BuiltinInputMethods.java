package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.input.impl.dialog.DialogInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.impl.sign.SignInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputOptionsBuilder;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.Set;

/**
 * A class that holds the keys for builtin input methods.
 */
public class BuiltinInputMethods {
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
    public static Collection<Key> keys() {
        return Set.of(CHAT.key(), SIGN.key(), DIALOG.key());
    }
}
