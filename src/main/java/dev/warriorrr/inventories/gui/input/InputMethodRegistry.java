package dev.warriorrr.inventories.gui.input;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class InputMethodRegistry {
    private final Map<Key, UserInputMethod<?>> methods;
    private final TreeMap<TextLength, UserInputMethod<?>> methodsByLength = new TreeMap<>();

    @ApiStatus.Internal
    public InputMethodRegistry(final Map<Key, UserInputMethod<?>> methods) {
        Preconditions.checkState(!methods.isEmpty(), "methods may not be empty"); // Already checked in the Inventories builder as well

        this.methods = Collections.unmodifiableMap(new ConcurrentHashMap<>(methods));

        for (final UserInputMethod<?> method : methods.values()) {
            this.methodsByLength.putIfAbsent(method.maximumTextLength(), method); // If a second method uses the same text length, it's currently out of luck when using TextLength
        }
    }

    @ApiStatus.Internal
    public void shutdown() {
        for (final UserInputMethod<?> method : methods.values()) {
            method.disable();
        }
    }

    @Nullable
    public <T extends InputOptionsBuilder> UserInputMethod<T> method(final Key key) {
        return (UserInputMethod<T>) this.methods.get(key);
    }

    public <T extends InputOptionsBuilder> UserInputMethod<T> method(final InputMethodKey<T> key) {
        return (UserInputMethod<T>) this.methods.get(key.key());
    }

    public <T extends InputOptionsBuilder> UserInputMethod<T> methodFor(final TextLength textLength) {
        final Map.Entry<TextLength, UserInputMethod<?>> ceil = this.methodsByLength.ceilingEntry(textLength);
        if (ceil != null) {
            return (UserInputMethod<T>) ceil.getValue();
        }

        // can't find a method that can fit the minimum length, so just try the method with the highest supported length
        return (UserInputMethod<T>) this.methodsByLength.lastEntry().getValue();
    }
}
