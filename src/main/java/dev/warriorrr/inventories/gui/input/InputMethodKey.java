package dev.warriorrr.inventories.gui.input;

import net.kyori.adventure.key.Key;

public class InputMethodKey<T extends InputOptionsBuilder> {
    private final Key key;

    protected InputMethodKey(final Key key) {
        this.key = key;
    }

    public static <T extends InputOptionsBuilder> InputMethodKey<T> of(final Key key) {
        return new InputMethodKey<>(key);
    }

    public Key key() {
        return this.key;
    }
}
