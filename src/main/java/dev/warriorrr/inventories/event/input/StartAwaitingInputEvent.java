package dev.warriorrr.inventories.event.input;

import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Called when input starts being awaited for a player, allowing options to be configured by casting the options builder to specific implementations.
 */
public class StartAwaitingInputEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final MenuInventory currentInventory;

    private final UserInputMethod<?> inputBackend;
    private final InputOptionsBuilder optionsBuilder;

    @ApiStatus.Internal
    public StartAwaitingInputEvent(Player player, MenuInventory currentInventory, UserInputMethod<?> inputBackend, InputOptionsBuilder optionsBuilder) {
        this.player = player;
        this.currentInventory = currentInventory;
        this.inputBackend = inputBackend;
        this.optionsBuilder = optionsBuilder;
    }

    public Player getPlayer() {
        return player;
    }

    public MenuInventory getCurrentInventory() {
        return currentInventory;
    }

    public UserInputMethod<?> getInputBackend() {
        return inputBackend;
    }

    public InputOptionsBuilder getOptionsBuilder() {
        return optionsBuilder;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
