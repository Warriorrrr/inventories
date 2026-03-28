package dev.warriorrr.inventories.gui.input;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class InputBackendRegistry {
    private final Map<Key, UserInputBackend> backends;
    private final TreeMap<TextLength, UserInputBackend> backendByLength = new TreeMap<>();

    @ApiStatus.Internal
    public InputBackendRegistry(final Map<Key, UserInputBackend> backends) {
        Preconditions.checkState(!backends.isEmpty(), "backends may not be empty"); // Already checked in the Inventories builder as well

        this.backends = Collections.unmodifiableMap(new ConcurrentHashMap<>(backends));

        for (final UserInputBackend backend : backends.values()) {
            this.backendByLength.putIfAbsent(backend.maximumTextLength(), backend); // If a second backend uses the same text length, it's currently out of luck when using TextLength
        }
    }

    @ApiStatus.Internal
    public void shutdown() {
        for (final UserInputBackend backend : backends.values()) {
            backend.disable();
        }
    }

    @Nullable
    public UserInputBackend backend(final Key key) {
        return this.backends.get(key);
    }

    public UserInputBackend backendFor(final TextLength textLength) {
        final Map.Entry<TextLength, UserInputBackend> ceil = this.backendByLength.ceilingEntry(textLength);
        if (ceil != null) {
            return ceil.getValue();
        }

        // can't find a backend that can fit the minimum length, so just try the backend with the highest supported length
        return this.backendByLength.lastEntry().getValue();
    }
}
