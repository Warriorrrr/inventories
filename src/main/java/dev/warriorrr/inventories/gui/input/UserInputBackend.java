package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.impl.text.TextInputBackend;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Function;

public interface UserInputBackend {
    void startAwaitingInput(final Player player, final MenuInventory currentInventory, final Component title, Function<PlayerInput, List<InputResponse>> inputFunction);

    static UserInputBackend selectBackend(JavaPlugin plugin) {
        return new TextInputBackend(plugin);
    }
}
