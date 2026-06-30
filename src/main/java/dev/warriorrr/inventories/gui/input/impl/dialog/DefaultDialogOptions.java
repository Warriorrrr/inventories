package dev.warriorrr.inventories.gui.input.impl.dialog;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A builder for the default dialog that's used when a more specific one isn't provided.
 */
@SuppressWarnings("UnstableApiUsage")
public class DefaultDialogOptions {
    protected Component label = Component.text("Input");
    protected int maxLength = 1024;
    protected Consumer<TextDialogInput.Builder> inputConsumer = input -> {};

    public DefaultDialogOptions label(final @NotNull Component label) {
        this.label = label;
        return this;
    }

    /**
     * Sets the maximum text length for the input box.
     * @param maxLength The maximum length
     * @return {@code this}
     */
    public DefaultDialogOptions maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public DefaultDialogOptions input(final @NotNull Consumer<TextDialogInput.Builder> inputConsumer) {
        this.inputConsumer = inputConsumer;
        return this;
    }
}
