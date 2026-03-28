package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.InputBackendRegistry;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.TextLengths;
import dev.warriorrr.inventories.gui.input.UserInputBackend;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class UserInputAction implements ClickAction {
    private final Component title;
    private final Function<PlayerInput, List<InputResponse>> inputFunction;
    private final @Nullable Key inputBackendKey;
    private final @Nullable TextLength desiredSupportedTextLength;

    public UserInputAction(Component title, Function<PlayerInput, List<InputResponse>> inputFunction) {
        this(title, inputFunction, null, null);
    }

    public UserInputAction(Component title, Function<PlayerInput, List<InputResponse>> inputFunction, @Nullable Key inputBackendKey, @Nullable TextLength desiredSupportedTextLength) {
        this.title = title;
        this.inputFunction = inputFunction;
        this.inputBackendKey = inputBackendKey;
        this.desiredSupportedTextLength = desiredSupportedTextLength;
    }

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        final InputBackendRegistry registry = Inventories.getInstance().inputBackendRegistry();

        final UserInputBackend backend;
        if (this.inputBackendKey != null) {
            backend = Objects.requireNonNull(registry.backend(this.inputBackendKey), () -> "could not find input backend for key '" + this.inputBackendKey.asString() + "'");
        } else {
            backend = registry.backendFor(Objects.requireNonNullElse(this.desiredSupportedTextLength, TextLengths.SHORT));
        }

        backend.startAwaitingInput((Player) event.getWhoClicked(), inventory, title, inputFunction);
    }
}
