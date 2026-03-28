package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public interface UserInputBackend {
    void startAwaitingInput(final Player player, final MenuInventory currentInventory, final Component title, Function<PlayerInput, List<InputResponse>> inputFunction);

    /**
     * {@return the maximum supported text length for this input backend type}
     */
    TextLength maximumTextLength();

    default void disable() {}
}
