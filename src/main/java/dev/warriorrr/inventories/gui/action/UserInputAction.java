package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.Function;

public class UserInputAction implements ClickAction {
    private final Component title;
    private final Function<PlayerInput, List<InputResponse>> inputFunction;

    public UserInputAction(Component title, Function<PlayerInput, List<InputResponse>> inputFunction) {
        this.title = title;
        this.inputFunction = inputFunction;
    }

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        Inventories.getInstance().getUserInputBackend().startAwaitingInput((Player) event.getWhoClicked(), inventory, title, inputFunction);
    }
}
