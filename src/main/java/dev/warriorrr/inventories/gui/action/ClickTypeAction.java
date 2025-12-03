package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.gui.MenuInventory;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ClickTypeAction implements ClickAction {
    private final ClickType type;
    private final ClickAction clickAction;

    public ClickTypeAction(@NotNull ClickType type, @NotNull ClickAction clickAction) {
        this.type = type;
        this.clickAction = clickAction;
    }

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        if (event.getClick() == this.type)
            this.clickAction.onClick(inventory, event);
    }
}
