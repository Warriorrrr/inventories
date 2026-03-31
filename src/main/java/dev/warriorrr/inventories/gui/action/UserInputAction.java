package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.event.input.StartAwaitingInputEvent;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.InputMethodRegistry;
import dev.warriorrr.inventories.gui.input.InputMethodKey;
import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.TextLengths;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class UserInputAction<T extends InputOptionsBuilder> implements ClickAction {
    private final @Nullable InputMethodKey<T> key;
    private final @Nullable TextLength desiredSupportedTextLength;
    private final Consumer<T> configurer;

    public UserInputAction(Component title, Function<PlayerInput, List<InputResponse>> inputFunction) {
        this(TextLengths.SHORT, builder -> builder.title(title).onInput(inputFunction));
    }

    public UserInputAction(@Nullable TextLength desiredSupportedTextLength, Consumer<T> configurer) {
        this.key = null;
        this.desiredSupportedTextLength = desiredSupportedTextLength;
        this.configurer = configurer;
    }

    public UserInputAction(final InputMethodKey<T> key, final Consumer<T> configurer) {
        this.key = key;
        this.desiredSupportedTextLength = null;
        this.configurer = configurer;
    }

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        final InputMethodRegistry registry = Inventories.getInstance().inputBackendRegistry();

        final UserInputMethod<T> backend;
        if (this.key != null) {
            backend = Objects.requireNonNull(registry.backend(this.key.key()), () -> "could not find input backend for key '" + this.key.key().asString() + "'");
        } else {
            backend = registry.backendFor(Objects.requireNonNullElse(this.desiredSupportedTextLength, TextLengths.SHORT));
        }

        final T builder = backend.newOptionsBuilder();
        this.configurer.accept(builder);

        final Player player = (Player) event.getWhoClicked();

        new StartAwaitingInputEvent(player, inventory, backend, builder).callEvent();

        backend.startAwaitingInput(player, inventory, builder);
    }
}
