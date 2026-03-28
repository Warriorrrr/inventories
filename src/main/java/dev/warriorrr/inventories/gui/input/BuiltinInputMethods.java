package dev.warriorrr.inventories.gui.input;

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
    public static final Key CHAT = Key.key("inventories", "chat");

    /**
     * Key for the builtin sign input method.
     */
    public static final Key SIGN = Key.key("inventories", "sign");

    /**
     * {@return the keys for all builtin input methods}
     */
    public static Collection<Key> keys() {
        return Set.of(CHAT, SIGN);
    }
}
