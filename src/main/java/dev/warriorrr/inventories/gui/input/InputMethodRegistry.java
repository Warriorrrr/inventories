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
    private final Map<Key, UserInputMethod<?>> backends;
    private final TreeMap<TextLength, UserInputMethod<?>> backendByLength = new TreeMap<>();

    @ApiStatus.Internal
    public InputMethodRegistry(final Map<Key, UserInputMethod<?>> backends) {
        Preconditions.checkState(!backends.isEmpty(), "backends may not be empty"); // Already checked in the Inventories builder as well

        this.backends = Collections.unmodifiableMap(new ConcurrentHashMap<>(backends));

        for (final UserInputMethod<?> backend : backends.values()) {
            this.backendByLength.putIfAbsent(backend.maximumTextLength(), backend); // If a second backend uses the same text length, it's currently out of luck when using TextLength
        }
    }

    @ApiStatus.Internal
    public void shutdown() {
        for (final UserInputMethod<?> backend : backends.values()) {
            backend.disable();
        }
    }

    @Nullable
    public <T extends InputOptionsBuilder> UserInputMethod<T> backend(final Key key) {
        return (UserInputMethod<T>) this.backends.get(key);
    }

    public <T extends InputOptionsBuilder> UserInputMethod<T> backend(final InputMethodKey<T> key) {
        return (UserInputMethod<T>) this.backends.get(key.key());
    }

    public <T extends InputOptionsBuilder> UserInputMethod<T> backendFor(final TextLength textLength) {
        final Map.Entry<TextLength, UserInputMethod<?>> ceil = this.backendByLength.ceilingEntry(textLength);
        if (ceil != null) {
            return (UserInputMethod<T>) ceil.getValue();
        }

        // can't find a backend that can fit the minimum length, so just try the backend with the highest supported length
        return (UserInputMethod<T>) this.backendByLength.lastEntry().getValue();
    }
}
