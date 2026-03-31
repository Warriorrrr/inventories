package dev.warriorrr.inventories.gui.input;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Range;

public record TextLength(@Range(from = 1, to = Integer.MAX_VALUE) int characters) implements Comparable<TextLength> {
    public TextLength {
        Preconditions.checkArgument(characters > 0, "characters must be positive, got %s", characters);
    }

    @Override
    public int compareTo(TextLength o) {
        return Integer.compare(this.characters, o.characters);
    }

    /**
     * @param characters The minimum number of needed characters
     * @return A new {@link TextLength} instance.
     * @throws IllegalArgumentException if characters &lt;= 0
     */
    public static TextLength ofAtLeast(final @Range(from = 1, to = Integer.MAX_VALUE) int characters) {
        Preconditions.checkArgument(characters > 0, "characters must be positive, got %s", characters);
        return new TextLength(characters);
    }
}
