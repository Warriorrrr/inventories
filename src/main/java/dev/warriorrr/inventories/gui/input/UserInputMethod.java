package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.MenuInventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

public interface UserInputMethod<T extends InputOptionsBuilder> {
    void startAwaitingInput(final Player player, final MenuInventory currentInventory, T options);

    /**
     * {@return the maximum supported text length for this input method type}
     */
    TextLength maximumTextLength();

    @ApiStatus.Internal
    T newOptionsBuilder();

    default void disable() {}
}
